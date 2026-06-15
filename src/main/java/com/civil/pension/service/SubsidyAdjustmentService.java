package com.civil.pension.service;

import com.civil.pension.entity.AbnormalAlert;
import com.civil.pension.entity.Elder;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.entity.SubsidyAdjustment;
import com.civil.pension.entity.SubsidyType;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.ElderRepository;
import com.civil.pension.repository.ElderSubsidyRepository;
import com.civil.pension.repository.SubsidyAdjustmentRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class SubsidyAdjustmentService {

    private final SubsidyAdjustmentRepository subsidyAdjustmentRepository;
    private final ElderSubsidyRepository elderSubsidyRepository;
    private final ElderRepository elderRepository;
    private final SubsidyTypeRepository subsidyTypeRepository;

    @Autowired
    public SubsidyAdjustmentService(SubsidyAdjustmentRepository subsidyAdjustmentRepository,
                                     ElderSubsidyRepository elderSubsidyRepository,
                                     ElderRepository elderRepository,
                                     SubsidyTypeRepository subsidyTypeRepository) {
        this.subsidyAdjustmentRepository = subsidyAdjustmentRepository;
        this.elderSubsidyRepository = elderSubsidyRepository;
        this.elderRepository = elderRepository;
        this.subsidyTypeRepository = subsidyTypeRepository;
    }

    @Transactional
    public SubsidyAdjustment createSuspendFromAlert(ElderSubsidy subsidy, AbnormalAlert alert) {
        Elder elder = elderRepository.findById(subsidy.getElderId()).orElse(null);

        SubsidyAdjustment adjustment = new SubsidyAdjustment();
        adjustment.setAdjustNo("ADJ" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        adjustment.setElderId(subsidy.getElderId());
        if (elder != null) {
            adjustment.setElderIdCard(elder.getIdCard());
            adjustment.setElderName(elder.getName());
            adjustment.setCommunityCode(elder.getCommunityCode());
        }
        adjustment.setSubsidyTypeId(subsidy.getSubsidyTypeId());
        adjustment.setSubsidyCode(subsidy.getSubsidyCode());
        adjustment.setAdjustType("SUSPEND");
        adjustment.setFromStatus(SubsidyStatus.ACTIVE.name());
        adjustment.setToStatus(SubsidyStatus.SUSPENDED.name());
        adjustment.setAdjustMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setStartMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setAmount(subsidy.getMonthlyAmount());
        adjustment.setReason(alert.getAlertReason());
        adjustment.setRelatedAlertId(alert.getId());
        adjustment.setStatus(SubsidyStatus.SUSPENDED);
        adjustment.setApprovedBy(alert.getHandledBy());
        adjustment.setApproveTime(LocalDateTime.now());

        SubsidyAdjustment saved = subsidyAdjustmentRepository.save(adjustment);

        subsidy.setStatus(SubsidyStatus.SUSPENDED);
        subsidy.setEndDate(LocalDate.now());
        elderSubsidyRepository.save(subsidy);

        return saved;
    }

    @Transactional
    public SubsidyAdjustment createReissue(Long elderId, Long subsidyTypeId, String startMonth,
                                            String endMonth, BigDecimal amount, String reason,
                                            String approvedBy) {
        Elder elder = elderRepository.findById(elderId)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        ElderSubsidy subsidy = elderSubsidyRepository.findByElderIdAndSubsidyTypeId(elderId, subsidyTypeId)
                .orElseThrow(() -> new BusinessException("老人补贴记录不存在"));

        SubsidyAdjustment adjustment = new SubsidyAdjustment();
        adjustment.setAdjustNo("ADJ" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        adjustment.setElderId(elderId);
        adjustment.setElderIdCard(elder.getIdCard());
        adjustment.setElderName(elder.getName());
        adjustment.setSubsidyTypeId(subsidyTypeId);
        adjustment.setSubsidyCode(subsidy.getSubsidyCode());
        adjustment.setAdjustType("REISSUE");
        adjustment.setFromStatus(subsidy.getStatus().name());
        adjustment.setToStatus(SubsidyStatus.REISSUED.name());
        adjustment.setAdjustMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setStartMonth(startMonth);
        adjustment.setEndMonth(endMonth);
        adjustment.setAmount(amount);
        adjustment.setMonthCount(calculateMonthCount(startMonth, endMonth));
        adjustment.setReason(reason);
        adjustment.setStatus(SubsidyStatus.REISSUED);
        adjustment.setApprovedBy(approvedBy);
        adjustment.setApproveTime(LocalDateTime.now());
        adjustment.setCommunityCode(elder.getCommunityCode());

        return subsidyAdjustmentRepository.save(adjustment);
    }

    @Transactional
    public SubsidyAdjustment createSuspend(Long elderId, Long subsidyTypeId, String reason, String approvedBy) {
        Elder elder = elderRepository.findById(elderId)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        ElderSubsidy subsidy = elderSubsidyRepository.findByElderIdAndSubsidyTypeId(elderId, subsidyTypeId)
                .orElseThrow(() -> new BusinessException("老人补贴记录不存在"));

        if (subsidy.getStatus() != SubsidyStatus.ACTIVE) {
            throw new BusinessException("补贴不是有效状态，无法停发");
        }

        SubsidyAdjustment adjustment = new SubsidyAdjustment();
        adjustment.setAdjustNo("ADJ" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        adjustment.setElderId(elderId);
        adjustment.setElderIdCard(elder.getIdCard());
        adjustment.setElderName(elder.getName());
        adjustment.setSubsidyTypeId(subsidyTypeId);
        adjustment.setSubsidyCode(subsidy.getSubsidyCode());
        adjustment.setAdjustType("SUSPEND");
        adjustment.setFromStatus(SubsidyStatus.ACTIVE.name());
        adjustment.setToStatus(SubsidyStatus.SUSPENDED.name());
        adjustment.setAdjustMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setStartMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setAmount(subsidy.getMonthlyAmount());
        adjustment.setReason(reason);
        adjustment.setStatus(SubsidyStatus.SUSPENDED);
        adjustment.setApprovedBy(approvedBy);
        adjustment.setApproveTime(LocalDateTime.now());
        adjustment.setCommunityCode(elder.getCommunityCode());

        SubsidyAdjustment saved = subsidyAdjustmentRepository.save(adjustment);

        subsidy.setStatus(SubsidyStatus.SUSPENDED);
        subsidy.setEndDate(LocalDate.now());
        elderSubsidyRepository.save(subsidy);

        return saved;
    }

    @Transactional
    public SubsidyAdjustment createReactivate(Long elderId, Long subsidyTypeId, String reason, String approvedBy) {
        Elder elder = elderRepository.findById(elderId)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        ElderSubsidy subsidy = elderSubsidyRepository.findByElderIdAndSubsidyTypeId(elderId, subsidyTypeId)
                .orElseThrow(() -> new BusinessException("老人补贴记录不存在"));

        if (subsidy.getStatus() != SubsidyStatus.SUSPENDED) {
            throw new BusinessException("补贴不是停发状态，无法恢复");
        }

        SubsidyAdjustment adjustment = new SubsidyAdjustment();
        adjustment.setAdjustNo("ADJ" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        adjustment.setElderId(elderId);
        adjustment.setElderIdCard(elder.getIdCard());
        adjustment.setElderName(elder.getName());
        adjustment.setSubsidyTypeId(subsidyTypeId);
        adjustment.setSubsidyCode(subsidy.getSubsidyCode());
        adjustment.setAdjustType("REACTIVATE");
        adjustment.setFromStatus(SubsidyStatus.SUSPENDED.name());
        adjustment.setToStatus(SubsidyStatus.ACTIVE.name());
        adjustment.setAdjustMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setStartMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setAmount(subsidy.getMonthlyAmount());
        adjustment.setReason(reason);
        adjustment.setStatus(SubsidyStatus.ACTIVE);
        adjustment.setApprovedBy(approvedBy);
        adjustment.setApproveTime(LocalDateTime.now());
        adjustment.setCommunityCode(elder.getCommunityCode());

        SubsidyAdjustment saved = subsidyAdjustmentRepository.save(adjustment);

        subsidy.setStatus(SubsidyStatus.ACTIVE);
        subsidy.setEndDate(null);
        elderSubsidyRepository.save(subsidy);

        return saved;
    }

    public SubsidyAdjustment getById(Long id) {
        return subsidyAdjustmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("调整记录不存在"));
    }

    public List<SubsidyAdjustment> getByElderId(Long elderId) {
        return subsidyAdjustmentRepository.findByElderId(elderId);
    }

    public Page<SubsidyAdjustment> list(Long elderId, String adjustType, SubsidyStatus status,
                                          String adjustMonth, Pageable pageable) {
        Specification<SubsidyAdjustment> spec = Specification.where(null);

        if (elderId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("elderId"), elderId));
        }
        if (StringUtils.hasText(adjustType)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("adjustType"), adjustType));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (StringUtils.hasText(adjustMonth)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("adjustMonth"), adjustMonth));
        }

        return subsidyAdjustmentRepository.findAll(spec, pageable);
    }

    public List<SubsidyAdjustment> getByMonth(String adjustMonth) {
        return subsidyAdjustmentRepository.findByAdjustMonth(adjustMonth);
    }

    public List<SubsidyAdjustment> getByType(String adjustType) {
        return subsidyAdjustmentRepository.findByAdjustType(adjustType);
    }

    @Transactional
    public SubsidyAdjustment createDisabilityLevelChange(Long elderId, Long subsidyTypeId,
                                                          String fromLevel, String toLevel,
                                                          BigDecimal fromAmount, BigDecimal toAmount,
                                                          String reason, String approvedBy) {
        Elder elder = elderRepository.findById(elderId)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        ElderSubsidy subsidy = elderSubsidyRepository.findByElderIdAndSubsidyTypeId(elderId, subsidyTypeId)
                .orElseThrow(() -> new BusinessException("老人补贴记录不存在"));

        String nextMonth = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));

        SubsidyAdjustment adjustment = new SubsidyAdjustment();
        adjustment.setAdjustNo("ADJ" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        adjustment.setElderId(elderId);
        adjustment.setElderIdCard(elder.getIdCard());
        adjustment.setElderName(elder.getName());
        adjustment.setSubsidyTypeId(subsidyTypeId);
        adjustment.setSubsidyCode(subsidy.getSubsidyCode());
        adjustment.setAdjustType("AMOUNT_CHANGE");
        adjustment.setFromStatus(subsidy.getStatus().name());
        adjustment.setToStatus(subsidy.getStatus().name());
        adjustment.setAdjustMonth(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        adjustment.setStartMonth(nextMonth);
        adjustment.setFromAmount(fromAmount);
        adjustment.setToAmount(toAmount);
        adjustment.setAmount(toAmount);
        adjustment.setFromDisabilityLevel(fromLevel);
        adjustment.setToDisabilityLevel(toLevel);
        adjustment.setReason(reason);
        adjustment.setStatus(subsidy.getStatus());
        adjustment.setApprovedBy(approvedBy);
        adjustment.setApproveTime(LocalDateTime.now());
        adjustment.setCommunityCode(elder.getCommunityCode());

        SubsidyAdjustment saved = subsidyAdjustmentRepository.save(adjustment);

        subsidy.setMonthlyAmount(toAmount);
        subsidy.setRemark("失能等级由 " + fromLevel + " 调整为 " + toLevel + "，月金额由 " + fromAmount + " 调整为 " + toAmount + "，从 " + nextMonth + " 起生效");
        elderSubsidyRepository.save(subsidy);

        return saved;
    }

    public BigDecimal calculateAmountByDisabilityLevel(SubsidyType subsidyType, String disabilityLevel) {
        if (subsidyType == null || disabilityLevel == null) {
            return subsidyType != null ? subsidyType.getMonthlyAmount() : BigDecimal.ZERO;
        }
        switch (disabilityLevel) {
            case "MILD":
                return subsidyType.getMildAmount() != null ? subsidyType.getMildAmount() : subsidyType.getMonthlyAmount();
            case "MODERATE":
                return subsidyType.getModerateAmount() != null ? subsidyType.getModerateAmount() : subsidyType.getMonthlyAmount();
            case "SEVERE":
                return subsidyType.getSevereAmount() != null ? subsidyType.getSevereAmount() : subsidyType.getMonthlyAmount();
            case "TOTAL":
                return subsidyType.getTotalAmount() != null ? subsidyType.getTotalAmount() : subsidyType.getMonthlyAmount();
            case "NONE":
            default:
                return subsidyType.getMonthlyAmount();
        }
    }

    private int calculateMonthCount(String startMonth, String endMonth) {
        if (!StringUtils.hasText(startMonth) || !StringUtils.hasText(endMonth)) {
            return 0;
        }
        LocalDate start = LocalDate.parse(startMonth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01");
        return (end.getYear() - start.getYear()) * 12 + (end.getMonthValue() - start.getMonthValue()) + 1;
    }
}
