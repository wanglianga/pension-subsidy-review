package com.civil.pension.entity;

import com.civil.pension.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_payment")
public class FinancePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_no", unique = true, length = 30)
    private String paymentNo;

    @Column(name = "payment_month", length = 7)
    private String paymentMonth;

    @Column(name = "batch_no", length = 30)
    private String batchNo;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "elder_id_card", length = 18)
    private String elderIdCard;

    @Column(name = "elder_name", length = 50)
    private String elderName;

    @Column(name = "subsidy_type_id")
    private Long subsidyTypeId;

    @Column(name = "subsidy_code", length = 20)
    private String subsidyCode;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "bank_card", length = 30)
    private String bankCard;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentStatus status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "payment_channel", length = 50)
    private String paymentChannel;

    @Column(name = "failure_reason", length = 300)
    private String failureReason;

    @Column(name = "community_code", length = 20)
    private String communityCode;

    @Column(name = "is_reissue")
    private Boolean isReissue;

    @Column(name = "related_adjust_id")
    private Long relatedAdjustId;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(String paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public String getElderIdCard() {
        return elderIdCard;
    }

    public void setElderIdCard(String elderIdCard) {
        this.elderIdCard = elderIdCard;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public Long getSubsidyTypeId() {
        return subsidyTypeId;
    }

    public void setSubsidyTypeId(Long subsidyTypeId) {
        this.subsidyTypeId = subsidyTypeId;
    }

    public String getSubsidyCode() {
        return subsidyCode;
    }

    public void setSubsidyCode(String subsidyCode) {
        this.subsidyCode = subsidyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    public Boolean isReissue() {
        return isReissue;
    }

    public void setReissue(Boolean reissue) {
        isReissue = reissue;
    }

    public Long getRelatedAdjustId() {
        return relatedAdjustId;
    }

    public void setRelatedAdjustId(Long relatedAdjustId) {
        this.relatedAdjustId = relatedAdjustId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
