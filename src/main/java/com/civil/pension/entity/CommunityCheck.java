package com.civil.pension.entity;

import com.civil.pension.enums.CheckStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_check")
public class CommunityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "elder_id_card", length = 18)
    private String elderIdCard;

    @Column(name = "elder_name", length = 50)
    private String elderName;

    @Column(name = "check_month", length = 7)
    private String checkMonth;

    @Column(name = "check_date")
    private LocalDate checkDate;

    @Column(name = "checker_name", length = 50)
    private String checkerName;

    @Column(name = "check_way", length = 20)
    private String checkWay;

    @Column(name = "is_still_alive")
    private Boolean isStillAlive;

    @Column(name = "is_in_community")
    private Boolean isInCommunity;

    @Column(name = "is_bank_card_valid")
    private Boolean isBankCardValid;

    @Column(name = "status_changed")
    private Boolean statusChanged;

    @Column(name = "change_reason", length = 300)
    private String changeReason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CheckStatus status;

    @Column(name = "audit_by", length = 50)
    private String auditBy;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "audit_remark", length = 500)
    private String auditRemark;

    @Column(name = "community_code", length = 20)
    private String communityCode;

    @Lob
    @Column(name = "check_content", columnDefinition = "TEXT")
    private String checkContent;

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

    public String getCheckMonth() {
        return checkMonth;
    }

    public void setCheckMonth(String checkMonth) {
        this.checkMonth = checkMonth;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public String getCheckWay() {
        return checkWay;
    }

    public void setCheckWay(String checkWay) {
        this.checkWay = checkWay;
    }

    public Boolean isStillAlive() {
        return isStillAlive;
    }

    public void setStillAlive(Boolean stillAlive) {
        isStillAlive = stillAlive;
    }

    public Boolean isInCommunity() {
        return isInCommunity;
    }

    public void setInCommunity(Boolean inCommunity) {
        isInCommunity = inCommunity;
    }

    public Boolean isBankCardValid() {
        return isBankCardValid;
    }

    public void setBankCardValid(Boolean bankCardValid) {
        isBankCardValid = bankCardValid;
    }

    public Boolean isStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(Boolean statusChanged) {
        this.statusChanged = statusChanged;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public CheckStatus getStatus() {
        return status;
    }

    public void setStatus(CheckStatus status) {
        this.status = status;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    public String getCheckContent() {
        return checkContent;
    }

    public void setCheckContent(String checkContent) {
        this.checkContent = checkContent;
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
