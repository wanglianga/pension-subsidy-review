package com.civil.pension.service;

import com.civil.pension.entity.Elder;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.entity.FinancePayment;
import com.civil.pension.entity.SubsidyAdjustment;
import com.civil.pension.enums.PaymentStatus;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.ElderRepository;
import com.civil.pension.repository.ElderSubsidyRepository;
import com.civil.pension.repository.FinancePaymentRepository;
import com.civil.pension.repository.SubsidyAdjustmentRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FinancePaymentService {

    private final FinancePaymentRepository financePaymentRepository;
    private final ElderSubsidyRepository elderSubsidyRepository;
    private final ElderRepository elderRepository;
    private final SubsidyAdjustmentRepository subsidyAdjustmentRepository;

    @Autowired
    public FinancePaymentService(FinancePaymentRepository financePaymentRepository, ElderSubsidyRepository elderSubsidyRepository, ElderRepository elderRepository, SubsidyAdjustmentRepository subsidyAdjustmentRepository) {
        this.financePaymentRepository = financePaymentRepository;
        this.elderSubsidyRepository = elderSubsidyRepository;
        this.elderRepository = elderRepository;
        this.subsidyAdjustmentRepository = subsidyAdjustmentRepository;
    }


    @Transactional
    public List<FinancePayment> generateMonthlyPayment(String paymentMonth) {
        if (!StringUtils.hasText(paymentMonth)) {
            paymentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        List<FinancePayment> existing = financePaymentRepository.findByPaymentMonth(paymentMonth);
        if (!existing.isEmpty()) {
            throw new BusinessException(paymentMonth + "月份拨付单已生成");
        }

        List<ElderSubsidy> activeSubsidies = elderSubsidyRepository.findByStatus(SubsidyStatus.ACTIVE);
        String batchNo = "BAT" + paymentMonth.replace("-", "") + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        List<FinancePayment> payments = new ArrayList<>();

        for (ElderSubsidy subsidy : activeSubsidies) {
            Elder elder = elderRepository.findById(subsidy.getElderId()).orElse(null);
            if (elder == null) continue;

            FinancePayment payment = new FinancePayment();
            payment.setPaymentNo("PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            payment.setPaymentMonth(paymentMonth);
            payment.setBatchNo(batchNo);
            payment.setElderId(subsidy.getElderId());
            payment.setElderIdCard(elder.getIdCard());
            payment.setElderName(elder.getName());
            payment.setSubsidyTypeId(subsidy.getSubsidyTypeId());
            payment.setSubsidyCode(subsidy.getSubsidyCode());
            payment.setAmount(subsidy.getMonthlyAmount());
            payment.setBankCard(elder.getBankCard());
            payment.setBankName(elder.getBankName());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCommunityCode(elder.getCommunityCode());
            payment.setReissue(false);

            payments.add(financePaymentRepository.save(payment));
        }

        return payments;
    }

    @Transactional
    public List<FinancePayment> generateReissuePayment(Long adjustId) {
        SubsidyAdjustment adjustment = subsidyAdjustmentRepository.findById(adjustId)
                .orElseThrow(() -> new BusinessException("调整记录不存在"));

        if (!"REISSUE".equals(adjustment.getAdjustType())) {
            throw new BusinessException("该调整记录不是补发类型");
        }

        Elder elder = elderRepository.findById(adjustment.getElderId()).orElse(null);
        if (elder == null) {
            throw new BusinessException("老人档案不存在");
        }

        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String batchNo = "BAT" + currentMonth.replace("-", "") + "R" + UUID.randomUUID().toString().substring(0, 3).toUpperCase();

        List<FinancePayment> payments = new ArrayList<>();

        int monthCount = adjustment.getMonthCount() != null ? adjustment.getMonthCount() : 1;
        BigDecimal monthlyAmount = adjustment.getAmount();

        FinancePayment payment = new FinancePayment();
        payment.setPaymentNo("PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        payment.setPaymentMonth(currentMonth);
        payment.setBatchNo(batchNo);
        payment.setElderId(adjustment.getElderId());
        payment.setElderIdCard(adjustment.getElderIdCard());
        payment.setElderName(adjustment.getElderName());
        payment.setSubsidyTypeId(adjustment.getSubsidyTypeId());
        payment.setSubsidyCode(adjustment.getSubsidyCode());
        payment.setAmount(monthlyAmount.multiply(BigDecimal.valueOf(monthCount)));
        payment.setBankCard(elder.getBankCard());
        payment.setBankName(elder.getBankName());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCommunityCode(adjustment.getCommunityCode());
        payment.setReissue(true);
        payment.setRelatedAdjustId(adjustId);

        payments.add(financePaymentRepository.save(payment));

        return payments;
    }

    @Transactional
    public List<FinancePayment> executePayment(String batchNo) {
        List<FinancePayment> payments = financePaymentRepository.findByBatchNo(batchNo);
        if (payments.isEmpty()) {
            throw new BusinessException("批次号不存在");
        }

        for (FinancePayment payment : payments) {
            if (payment.getStatus() != PaymentStatus.PENDING) {
                continue;
            }

            if (!StringUtils.hasText(payment.getBankCard())) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("银行卡号为空");
            } else {
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaymentDate(LocalDate.now());
                payment.setPaymentTime(LocalDateTime.now());
                payment.setPaymentChannel("银行代发");
            }
            financePaymentRepository.save(payment);
        }

        return financePaymentRepository.findByBatchNo(batchNo);
    }

    @Transactional
    public FinancePayment retryPayment(Long id) {
        FinancePayment payment = getById(id);
        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new BusinessException("只有发放失败的记录可以重试");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentTime(LocalDateTime.now());
        payment.setFailureReason(null);
        payment.setPaymentChannel("银行代发");

        return financePaymentRepository.save(payment);
    }

    public FinancePayment getById(Long id) {
        return financePaymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("拨付记录不存在"));
    }

    public List<FinancePayment> getByElderId(Long elderId) {
        return financePaymentRepository.findByElderId(elderId);
    }

    public Page<FinancePayment> list(Long elderId, String paymentMonth, String batchNo,
                                      PaymentStatus status, Boolean isReissue, Pageable pageable) {
        Specification<FinancePayment> spec = Specification.where(null);

        if (elderId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("elderId"), elderId));
        }
        if (StringUtils.hasText(paymentMonth)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentMonth"), paymentMonth));
        }
        if (StringUtils.hasText(batchNo)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("batchNo"), batchNo));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (isReissue != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isReissue"), isReissue));
        }

        return financePaymentRepository.findAll(spec, pageable);
    }

    public List<FinancePayment> getByMonth(String paymentMonth) {
        return financePaymentRepository.findByPaymentMonth(paymentMonth);
    }

    public List<FinancePayment> getByBatchNo(String batchNo) {
        return financePaymentRepository.findByBatchNo(batchNo);
    }

    public List<FinancePayment> getPendingPayments() {
        return financePaymentRepository.findByStatus(PaymentStatus.PENDING);
    }

    @Transactional
    public void cancelPayment(Long id, String reason) {
        FinancePayment payment = getById(id);
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new BusinessException("已发放的记录不能取消");
        }
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setFailureReason(reason);
        financePaymentRepository.save(payment);
    }
}
