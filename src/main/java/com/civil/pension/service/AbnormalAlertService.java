package com.civil.pension.service;

import com.civil.pension.entity.AbnormalAlert;
import com.civil.pension.entity.CommunityCheck;
import com.civil.pension.entity.Elder;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.enums.AlertType;
import com.civil.pension.enums.CheckStatus;
import com.civil.pension.enums.ElderStatus;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.AbnormalAlertRepository;
import com.civil.pension.repository.ElderRepository;
import com.civil.pension.repository.ElderSubsidyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class AbnormalAlertService {

    private final AbnormalAlertRepository abnormalAlertRepository;
    private final ElderRepository elderRepository;
    private final ElderSubsidyRepository elderSubsidyRepository;
    private final SubsidyAdjustmentService subsidyAdjustmentService;

    @Autowired
    public AbnormalAlertService(AbnormalAlertRepository abnormalAlertRepository, ElderRepository elderRepository, ElderSubsidyRepository elderSubsidyRepository, SubsidyAdjustmentService subsidyAdjustmentService) {
        this.abnormalAlertRepository = abnormalAlertRepository;
        this.elderRepository = elderRepository;
        this.elderSubsidyRepository = elderSubsidyRepository;
        this.subsidyAdjustmentService = subsidyAdjustmentService;
    }


    @Transactional
    public void generateFromCheck(CommunityCheck check) {
        Elder elder = elderRepository.findById(check.getElderId()).orElse(null);
        if (elder == null) return;

        if (Boolean.FALSE.equals(check.isStillAlive())) {
            createAlert(check.getElderId(), check.getId(), AlertType.DECEASED,
                    check.getCheckMonth(), "社区核查确认老人去世", check.getCommunityCode());
        } else if (Boolean.FALSE.equals(check.isInCommunity())) {
            createAlert(check.getElderId(), check.getId(), AlertType.MOVED_OUT,
                    check.getCheckMonth(), "社区核查确认老人迁出辖区: " + check.getChangeReason(),
                    check.getCommunityCode());
        } else if (Boolean.FALSE.equals(check.isBankCardValid())) {
            createAlert(check.getElderId(), check.getId(), AlertType.BANK_CARD_ERROR,
                    check.getCheckMonth(), "银行卡信息异常", check.getCommunityCode());
        } else if (Boolean.TRUE.equals(check.isStatusChanged())) {
            createAlert(check.getElderId(), check.getId(), AlertType.STATUS_CHANGE,
                    check.getCheckMonth(), "资格状态变化: " + check.getChangeReason(),
                    check.getCommunityCode());
        }
    }

    @Transactional
    public void handleCheckResult(CommunityCheck check) {
        List<AbnormalAlert> alerts = abnormalAlertRepository.findByElderId(check.getElderId());
        for (AbnormalAlert alert : alerts) {
            if (alert.getStatus() == CheckStatus.PENDING &&
                alert.getRelatedCheckId() != null &&
                alert.getRelatedCheckId().equals(check.getId())) {
                if (check.getStatus() == CheckStatus.VERIFIED) {
                    alert.setStatus(CheckStatus.VERIFIED);
                    alert.setHandledBy(check.getAuditBy());
                    alert.setHandleTime(check.getAuditTime());
                    alert.setHandleResult("社区核查已审核确认");
                    abnormalAlertRepository.save(alert);
                }
            }
        }
    }

    @Transactional
    public AbnormalAlert createAlert(Long elderId, Long relatedCheckId, AlertType alertType,
                                      String alertMonth, String alertReason, String communityCode) {
        Elder elder = elderRepository.findById(elderId)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        AbnormalAlert alert = new AbnormalAlert();
        alert.setAlertNo("ALT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        alert.setElderId(elderId);
        alert.setElderIdCard(elder.getIdCard());
        alert.setElderName(elder.getName());
        alert.setAlertType(alertType);
        alert.setAlertMonth(alertMonth);
        alert.setAlertReason(alertReason);
        alert.setRelatedCheckId(relatedCheckId);
        alert.setStatus(CheckStatus.PENDING);
        alert.setCommunityCode(communityCode);

        return abnormalAlertRepository.save(alert);
    }

    @Transactional
    public AbnormalAlert handleAlert(Long id, boolean approve, String handledBy, String handleResult) {
        AbnormalAlert alert = getById(id);

        if (alert.getStatus() != CheckStatus.PENDING) {
            throw new BusinessException("预警状态不是待处理");
        }

        alert.setStatus(approve ? CheckStatus.VERIFIED : CheckStatus.REJECTED);
        alert.setHandledBy(handledBy);
        alert.setHandleTime(LocalDateTime.now());
        alert.setHandleResult(handleResult);

        AbnormalAlert saved = abnormalAlertRepository.save(alert);

        if (approve) {
            processAlertImpact(alert);
        }

        return saved;
    }

    @Transactional
    public void processAlertImpact(AbnormalAlert alert) {
        List<ElderSubsidy> subsidies = elderSubsidyRepository.findByElderIdAndStatus(alert.getElderId(), SubsidyStatus.ACTIVE);

        switch (alert.getAlertType()) {
            case DECEASED:
            case MOVED_OUT:
                for (ElderSubsidy subsidy : subsidies) {
                    subsidyAdjustmentService.createSuspendFromAlert(subsidy, alert);
                }
                break;
            case BANK_CARD_ERROR:
                for (ElderSubsidy subsidy : subsidies) {
                    subsidy.setStatus(SubsidyStatus.SUSPENDED);
                    elderSubsidyRepository.save(subsidy);
                }
                break;
            case STATUS_CHANGE:
                for (ElderSubsidy subsidy : subsidies) {
                    subsidyAdjustmentService.createSuspendFromAlert(subsidy, alert);
                }
                break;
            case INFO_UNVERIFIED:
                break;
            case HISTORICAL_REISSUE:
                break;
        }
    }

    public AbnormalAlert getById(Long id) {
        return abnormalAlertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("异动预警不存在"));
    }

    public List<AbnormalAlert> getByElderId(Long elderId) {
        return abnormalAlertRepository.findByElderId(elderId);
    }

    public Page<AbnormalAlert> list(Long elderId, AlertType alertType, CheckStatus status,
                                     String alertMonth, String communityCode, Pageable pageable) {
        Specification<AbnormalAlert> spec = Specification.where(null);

        if (elderId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("elderId"), elderId));
        }
        if (alertType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("alertType"), alertType));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (StringUtils.hasText(alertMonth)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("alertMonth"), alertMonth));
        }
        if (StringUtils.hasText(communityCode)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("communityCode"), communityCode));
        }

        return abnormalAlertRepository.findAll(spec, pageable);
    }

    public List<AbnormalAlert> getPendingAlerts() {
        return abnormalAlertRepository.findByStatus(CheckStatus.PENDING);
    }

    public List<AbnormalAlert> getByType(AlertType alertType) {
        return abnormalAlertRepository.findByAlertType(alertType);
    }

    @Transactional
    public void generateUnverifiedAlerts() {
        List<ElderSubsidy> unverified = elderSubsidyRepository.findByStatus(SubsidyStatus.UNVERIFIED);
        for (ElderSubsidy subsidy : unverified) {
            Elder elder = elderRepository.findById(subsidy.getElderId()).orElse(null);
            if (elder == null) continue;

            List<AbnormalAlert> existing = abnormalAlertRepository
                    .findByAlertTypeAndStatus(AlertType.INFO_UNVERIFIED, CheckStatus.PENDING);
            boolean exists = existing.stream()
                    .anyMatch(a -> a.getElderId().equals(subsidy.getElderId()) &&
                            a.getSubsidyTypeId() != null &&
                            a.getSubsidyTypeId().equals(subsidy.getSubsidyTypeId()));

            if (!exists) {
                AbnormalAlert alert = new AbnormalAlert();
                alert.setAlertNo("ALT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
                alert.setElderId(subsidy.getElderId());
                alert.setElderIdCard(elder.getIdCard());
                alert.setElderName(elder.getName());
                alert.setAlertType(AlertType.INFO_UNVERIFIED);
                alert.setAlertMonth(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
                alert.setAlertReason("补贴信息未核实");
                alert.setSubsidyTypeId(subsidy.getSubsidyTypeId());
                alert.setSubsidyCode(subsidy.getSubsidyCode());
                alert.setStatus(CheckStatus.PENDING);
                alert.setCommunityCode(elder.getCommunityCode());
                abnormalAlertRepository.save(alert);
            }
        }
    }
}
