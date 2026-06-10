package com.civil.pension.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subsidy_type")
public class SubsidyType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subsidy_code", unique = true, nullable = false, length = 20)
    private String subsidyCode;

    @Column(name = "subsidy_name", nullable = false, length = 100)
    private String subsidyName;

    @Column(length = 500)
    private String description;

    @Column(name = "monthly_amount", precision = 10, scale = 2)
    private BigDecimal monthlyAmount;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "require_low_income")
    private Boolean requireLowIncome;

    @Column(name = "require_disability")
    private Boolean requireDisability;

    @Column(name = "is_active")
    private Boolean isActive;

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

    public String getSubsidyCode() {
        return subsidyCode;
    }

    public void setSubsidyCode(String subsidyCode) {
        this.subsidyCode = subsidyCode;
    }

    public String getSubsidyName() {
        return subsidyName;
    }

    public void setSubsidyName(String subsidyName) {
        this.subsidyName = subsidyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(BigDecimal monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Boolean isRequireLowIncome() {
        return requireLowIncome;
    }

    public void setRequireLowIncome(Boolean requireLowIncome) {
        this.requireLowIncome = requireLowIncome;
    }

    public Boolean isRequireDisability() {
        return requireDisability;
    }

    public void setRequireDisability(Boolean requireDisability) {
        this.requireDisability = requireDisability;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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
