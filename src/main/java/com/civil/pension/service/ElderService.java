package com.civil.pension.service;

import com.civil.pension.entity.CommunityCheck;
import com.civil.pension.entity.Elder;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.entity.SubsidyType;
import com.civil.pension.enums.AlertType;
import com.civil.pension.enums.CheckStatus;
import com.civil.pension.enums.DisabilityLevel;
import com.civil.pension.enums.ElderStatus;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.ElderRepository;
import com.civil.pension.repository.ElderSubsidyRepository;
import com.civil.pension.repository.SubsidyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ElderService {

    private final ElderRepository elderRepository;
    private final ElderSubsidyRepository elderSubsidyRepository;
    private final SubsidyTypeRepository subsidyTypeRepository;
    private final AbnormalAlertService abnormalAlertService;
    private final CommunityCheckService communityCheckService;
    private final SubsidyAdjustmentService subsidyAdjustmentService;

    @Autowired
    public ElderService(ElderRepository elderRepository,
                        ElderSubsidyRepository elderSubsidyRepository,
                        SubsidyTypeRepository subsidyTypeRepository,
                        AbnormalAlertService abnormalAlertService,
                        CommunityCheckService communityCheckService,
                        SubsidyAdjustmentService subsidyAdjustmentService) {
        this.elderRepository = elderRepository;
        this.elderSubsidyRepository = elderSubsidyRepository;
        this.subsidyTypeRepository = subsidyTypeRepository;
        this.abnormalAlertService = abnormalAlertService;
        this.communityCheckService = communityCheckService;
        this.subsidyAdjustmentService = subsidyAdjustmentService;
    }

    @Transactional
    public Elder create(Elder elder) {
        if (elderRepository.existsByIdCard(elder.getIdCard())) {
            throw new BusinessException("该身份证号已存在");
        }
        if (elder.getBirthDate() != null && elder.getAge() == null) {
            elder.setAge(Period.between(elder.getBirthDate(), LocalDate.now()).getYears());
        }
        if (elder.getStatus() == null) {
            elder.setStatus(ElderStatus.ACTIVE);
        }
        return elderRepository.save(elder);
    }

    @Transactional
    public Elder update(Long id, Elder elder) {
        Elder existing = elderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        if (elder.getIdCard() != null && !elder.getIdCard().equals(existing.getIdCard())) {
            if (elderRepository.existsByIdCard(elder.getIdCard())) {
                throw new BusinessException("该身份证号已存在");
            }
        }

        elder.setId(id);
        if (elder.getBirthDate() != null) {
            elder.setAge(Period.between(elder.getBirthDate(), LocalDate.now()).getYears());
        }
        return elderRepository.save(elder);
    }

    @Transactional
    public void delete(Long id) {
        if (!elderRepository.existsById(id)) {
            throw new BusinessException("老人档案不存在");
        }
        elderRepository.deleteById(id);
    }

    public Elder getById(Long id) {
        return elderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));
    }

    public Elder getByIdCard(String idCard) {
        return elderRepository.findByIdCard(idCard)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));
    }

    public Page<Elder> list(String name, String idCard, String communityCode, ElderStatus status, Pageable pageable) {
        Specification<Elder> spec = Specification.where(null);

        if (StringUtils.hasText(name)) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + name + "%"));
        }
        if (StringUtils.hasText(idCard)) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("idCard"), "%" + idCard + "%"));
        }
        if (StringUtils.hasText(communityCode)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("communityCode"), communityCode));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return elderRepository.findAll(spec, pageable);
    }

    @Transactional
    public Elder markDeceased(Long id, LocalDate deathDate) {
        Elder elder = getById(id);
        if (elder.getStatus() == ElderStatus.DECEASED) {
            throw new BusinessException("该老人已标记为去世状态");
        }
        elder.setStatus(ElderStatus.DECEASED);
        elder.setDeathDate(deathDate);
        Elder saved = elderRepository.save(elder);

        suspendActiveSubsidies(id, "老人去世，暂停补贴发放");
        generateCommunityCheckAndAlert(id, AlertType.DECEASED, deathDate, "外部系统异动：老人去世，死亡日期：" + deathDate);

        return saved;
    }

    @Transactional
    public Elder markMovedOut(Long id, LocalDate moveOutDate, String moveToPlace) {
        Elder elder = getById(id);
        if (elder.getStatus() == ElderStatus.MOVED_OUT) {
            throw new BusinessException("该老人已标记为迁出状态");
        }
        elder.setStatus(ElderStatus.MOVED_OUT);
        elder.setMoveOutDate(moveOutDate);
        elder.setMoveToPlace(moveToPlace);
        Elder saved = elderRepository.save(elder);

        suspendActiveSubsidies(id, "老人迁出辖区，暂停补贴发放");
        String reason = "外部系统异动：老人迁出辖区，迁出日期：" + moveOutDate
                + (StringUtils.hasText(moveToPlace) ? "，迁往：" + moveToPlace : "");
        generateCommunityCheckAndAlert(id, AlertType.MOVED_OUT, moveOutDate, reason);

        return saved;
    }

    @Transactional
    public Elder handleExternalChange(Long id, String changeType, LocalDate changeDate, String changeReason, String moveToPlace) {
        if ("DECEASED".equals(changeType)) {
            return markDeceased(id, changeDate);
        } else if ("MOVED_OUT".equals(changeType)) {
            return markMovedOut(id, changeDate, moveToPlace);
        } else {
            throw new BusinessException("不支持的异动类型：" + changeType);
        }
    }

    @Transactional
    public Elder updateDisabilityLevel(Long id, DisabilityLevel newLevel, String approvedBy, String remark) {
        Elder elder = getById(id);
        DisabilityLevel oldLevel = elder.getDisabilityLevel();

        if (oldLevel == newLevel) {
            throw new BusinessException("失能等级未发生变化");
        }

        elder.setDisabilityLevel(newLevel);
        Elder saved = elderRepository.save(elder);

        List<ElderSubsidy> subsidies = elderSubsidyRepository.findByElderId(id);
        for (ElderSubsidy subsidy : subsidies) {
            if (subsidy.getStatus() == SubsidyStatus.ACTIVE || subsidy.getStatus() == SubsidyStatus.SUSPENDED) {
                SubsidyType subsidyType = subsidyTypeRepository.findById(subsidy.getSubsidyTypeId()).orElse(null);
                if (subsidyType != null) {
                    BigDecimal fromAmount = subsidy.getMonthlyAmount();
                    BigDecimal toAmount = subsidyAdjustmentService.calculateAmountByDisabilityLevel(subsidyType, newLevel.name());

                    if (fromAmount.compareTo(toAmount) != 0) {
                        String reason = "失能等级由 " + (oldLevel != null ? oldLevel.name() : "NONE")
                                + " 调整为 " + newLevel.name()
                                + (StringUtils.hasText(remark) ? "，原因：" + remark : "");
                        subsidyAdjustmentService.createDisabilityLevelChange(
                                id,
                                subsidy.getSubsidyTypeId(),
                                oldLevel != null ? oldLevel.name() : "NONE",
                                newLevel.name(),
                                fromAmount,
                                toAmount,
                                reason,
                                approvedBy
                        );
                    }
                }
            }
        }

        return saved;
    }

    private void suspendActiveSubsidies(Long elderId, String reason) {
        List<ElderSubsidy> activeSubsidies = elderSubsidyRepository.findByElderIdAndStatus(elderId, SubsidyStatus.ACTIVE);
        for (ElderSubsidy subsidy : activeSubsidies) {
            subsidy.setStatus(SubsidyStatus.SUSPENDED);
            subsidy.setEndDate(LocalDate.now());
            subsidy.setRemark(reason);
            elderSubsidyRepository.save(subsidy);
        }
    }

    private void generateCommunityCheckAndAlert(Long elderId, AlertType alertType, LocalDate eventDate, String alertReason) {
        Elder elder = getById(elderId);
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        CommunityCheck check = new CommunityCheck();
        check.setElderId(elderId);
        check.setCheckMonth(currentMonth);
        check.setCheckDate(LocalDate.now());
        check.setCheckWay("系统触发");
        check.setStillAlive(alertType != AlertType.DECEASED);
        check.setInCommunity(alertType != AlertType.MOVED_OUT);
        check.setBankCardValid(true);
        check.setStatusChanged(true);
        check.setChangeReason(alertReason);
        check.setStatus(CheckStatus.PENDING);
        check.setCheckContent(alertReason + "。请社区工作人员核实确认。");
        CommunityCheck savedCheck = communityCheckService.create(check);

        abnormalAlertService.createAlert(
                elderId,
                savedCheck.getId(),
                alertType,
                currentMonth,
                alertReason,
                elder.getCommunityCode()
        );
    }
}
