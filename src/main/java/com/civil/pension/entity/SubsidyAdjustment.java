package com.civil.pension.entity;

import com.civil.pension.enums.SubsidyStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subsidy_adjustment")
public class SubsidyAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adjust_no", unique = true, length = 30)
    private String adjustNo;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "elder_id_card", length = 18)
    private String elderIdCard;

    @Column(name = "elder_name", length = 50)
    private String elderName;

    @Column(name = "subsidy_type_id", nullable = false)
    private Long subsidyTypeId;

    @Column(name = "subsidy_code", length = 20)
    private String subsidyCode;

    @Column(name = "adjust_type", length = 20)
    private String adjustType;

    @Column(name = "from_status")
    private String fromStatus;

    @Column(name = "to_status")
    private String toStatus;

    @Column(name = "adjust_month", length = 7)
    private String adjustMonth;

    @Column(name = "start_month", length = 7)
    private String startMonth;

    @Column(name = "end_month", length = 7)
    private String endMonth;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "from_amount", precision = 10, scale = 2)
    private BigDecimal fromAmount;

    @Column(name = "to_amount", precision = 10, scale = 2)
    private BigDecimal toAmount;

    @Column(name = "from_disability_level", length = 20)
    private String fromDisabilityLevel;

    @Column(name = "to_disability_level", length = 20)
    private String toDisabilityLevel;

    @Column(name = "month_count")
    private Integer monthCount;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "related_alert_id")
    private Long relatedAlertId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SubsidyStatus status;

    @Column(name = "approved_by", length = 50)
    private String approvedBy;

    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    @Column(name = "approve_remark", length = 500)
    private String approveRemark;

    @Column(name = "community_code", length = 20)
    private String communityCode;

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

    public String getAdjustNo() {
        return adjustNo;
    }

    public void setAdjustNo(String adjustNo) {
        this.adjustNo = adjustNo;
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

    public String getAdjustType() {
        return adjustType;
    }

    public void setAdjustType(String adjustType) {
        this.adjustType = adjustType;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getAdjustMonth() {
        return adjustMonth;
    }

    public void setAdjustMonth(String adjustMonth) {
        this.adjustMonth = adjustMonth;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(BigDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public BigDecimal getToAmount() {
        return toAmount;
    }

    public void setToAmount(BigDecimal toAmount) {
        this.toAmount = toAmount;
    }

    public String getFromDisabilityLevel() {
        return fromDisabilityLevel;
    }

    public void setFromDisabilityLevel(String fromDisabilityLevel) {
        this.fromDisabilityLevel = fromDisabilityLevel;
    }

    public String getToDisabilityLevel() {
        return toDisabilityLevel;
    }

    public void setToDisabilityLevel(String toDisabilityLevel) {
        this.toDisabilityLevel = toDisabilityLevel;
    }

    public Integer getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(Integer monthCount) {
        this.monthCount = monthCount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getRelatedAlertId() {
        return relatedAlertId;
    }

    public void setRelatedAlertId(Long relatedAlertId) {
        this.relatedAlertId = relatedAlertId;
    }

    public SubsidyStatus getStatus() {
        return status;
    }

    public void setStatus(SubsidyStatus status) {
        this.status = status;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(LocalDateTime approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveRemark() {
        return approveRemark;
    }

    public void setApproveRemark(String approveRemark) {
        this.approveRemark = approveRemark;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
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
