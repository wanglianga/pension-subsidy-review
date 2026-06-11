package com.civil.pension.service;

import com.civil.pension.entity.AbnormalAlert;
import com.civil.pension.entity.CommunityCheck;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.entity.FinancePayment;
import com.civil.pension.entity.SubsidyAdjustment;
import com.civil.pension.enums.AlertType;
import com.civil.pension.enums.CheckStatus;
import com.civil.pension.enums.PaymentStatus;
import com.civil.pension.enums.SubsidyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final AbnormalAlertService abnormalAlertService;
    private final CommunityCheckService communityCheckService;
    private final ElderSubsidyService elderSubsidyService;
    private final SubsidyAdjustmentService subsidyAdjustmentService;
    private final FinancePaymentService financePaymentService;

    @Autowired
    public ReviewService(AbnormalAlertService abnormalAlertService,
                         CommunityCheckService communityCheckService,
                         ElderSubsidyService elderSubsidyService,
                         SubsidyAdjustmentService subsidyAdjustmentService,
                         FinancePaymentService financePaymentService) {
        this.abnormalAlertService = abnormalAlertService;
        this.communityCheckService = communityCheckService;
        this.elderSubsidyService = elderSubsidyService;
        this.subsidyAdjustmentService = subsidyAdjustmentService;
        this.financePaymentService = financePaymentService;
    }

    @Transactional
    public void runMonthlyReview(String reviewMonth) {
        abnormalAlertService.generateUnverifiedAlerts();
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<ElderSubsidy> activeSubsidies = elderSubsidyService.getActiveSubsidies();
        List<ElderSubsidy> unverifiedSubsidies = elderSubsidyService.getUnverifiedSubsidies();
        List<AbnormalAlert> pendingAlerts = abnormalAlertService.getPendingAlerts();
        List<CommunityCheck> pendingChecks = communityCheckService.getPendingChecks();
        List<FinancePayment> pendingPayments = financePaymentService.getPendingPayments();

        stats.put("activeSubsidyCount", activeSubsidies.size());
        stats.put("unverifiedSubsidyCount", unverifiedSubsidies.size());
        stats.put("pendingAlertCount", pendingAlerts.size());
        stats.put("pendingCheckCount", pendingChecks.size());
        stats.put("pendingPaymentCount", pendingPayments.size());

        BigDecimal totalActiveAmount = activeSubsidies.stream()
                .map(ElderSubsidy::getMonthlyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalMonthlyAmount", totalActiveAmount);

        long deceasedAlertCount = pendingAlerts.stream()
                .filter(a -> a.getAlertType() == AlertType.DECEASED)
                .count();
        long movedOutAlertCount = pendingAlerts.stream()
                .filter(a -> a.getAlertType() == AlertType.MOVED_OUT)
                .count();
        long bankCardErrorCount = pendingAlerts.stream()
                .filter(a -> a.getAlertType() == AlertType.BANK_CARD_ERROR)
                .count();
        long statusChangeCount = pendingAlerts.stream()
                .filter(a -> a.getAlertType() == AlertType.STATUS_CHANGE)
                .count();
        long infoUnverifiedCount = pendingAlerts.stream()
                .filter(a -> a.getAlertType() == AlertType.INFO_UNVERIFIED)
                .count();

        Map<String, Long> alertTypeStats = new HashMap<>();
        alertTypeStats.put("deceased", deceasedAlertCount);
        alertTypeStats.put("movedOut", movedOutAlertCount);
        alertTypeStats.put("bankCardError", bankCardErrorCount);
        alertTypeStats.put("statusChange", statusChangeCount);
        alertTypeStats.put("infoUnverified", infoUnverifiedCount);
        stats.put("alertTypeStats", alertTypeStats);

        return stats;
    }

    @Transactional
    public Map<String, Object> processCheckAndAlert(Long checkId, boolean pass, String operator, String remark) {
        Map<String, Object> result = new HashMap<>();

        CommunityCheck check = communityCheckService.audit(checkId, pass, operator, remark);
        result.put("check", check);

        if (pass && check.getStatus() == CheckStatus.VERIFIED) {
            List<AbnormalAlert> alerts = abnormalAlertService.getByElderId(check.getElderId());
            result.put("relatedAlerts", alerts);
        }

        return result;
    }

    @Transactional
    public Map<String, Object> createHistoricalReissue(Long elderId, Long subsidyTypeId,
                                                        String startMonth, String endMonth,
                                                        BigDecimal amount, String reason, String operator) {
        Map<String, Object> result = new HashMap<>();

        SubsidyAdjustment adjustment = subsidyAdjustmentService.createReissue(
                elderId, subsidyTypeId, startMonth, endMonth, amount, reason, operator);
        result.put("adjustment", adjustment);

        List<FinancePayment> payments = financePaymentService.generateReissuePayment(adjustment.getId());
        result.put("payments", payments);

        return result;
    }
}
