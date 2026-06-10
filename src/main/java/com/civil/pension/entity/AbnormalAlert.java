package com.civil.pension.entity;

import com.civil.pension.enums.AlertType;
import com.civil.pension.enums.CheckStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "abnormal_alert")
public class AbnormalAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_no", unique = true, length = 30)
    private String alertNo;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "elder_id_card", length = 18)
    private String elderIdCard;

    @Column(name = "elder_name", length = 50)
    private String elderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", length = 30)
    private AlertType alertType;

    @Column(name = "alert_month", length = 7)
    private String alertMonth;

    @Column(name = "alert_reason", length = 500)
    private String alertReason;

    @Column(name = "related_check_id")
    private Long relatedCheckId;

    @Column(name = "subsidy_type_id")
    private Long subsidyTypeId;

    @Column(name = "subsidy_code", length = 20)
    private String subsidyCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CheckStatus status;

    @Column(name = "handled_by", length = 50)
    private String handledBy;

    @Column(name = "handle_time")
    private LocalDateTime handleTime;

    @Column(name = "handle_result", length = 500)
    private String handleResult;

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

    public String getAlertNo() {
        return alertNo;
    }

    public void setAlertNo(String alertNo) {
        this.alertNo = alertNo;
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

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public String getAlertMonth() {
        return alertMonth;
    }

    public void setAlertMonth(String alertMonth) {
        this.alertMonth = alertMonth;
    }

    public String getAlertReason() {
        return alertReason;
    }

    public void setAlertReason(String alertReason) {
        this.alertReason = alertReason;
    }

    public Long getRelatedCheckId() {
        return relatedCheckId;
    }

    public void setRelatedCheckId(Long relatedCheckId) {
        this.relatedCheckId = relatedCheckId;
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

    public CheckStatus getStatus() {
        return status;
    }

    public void setStatus(CheckStatus status) {
        this.status = status;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
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
