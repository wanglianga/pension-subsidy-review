package com.civil.pension.entity;

import com.civil.pension.enums.DisabilityLevel;
import com.civil.pension.enums.ElderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "elder_profile")
public class Elder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_card", unique = true, nullable = false, length = 18)
    private String idCard;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private Integer age;

    @Column(length = 2)
    private String gender;

    @Column(name = "household_register", length = 200)
    private String householdRegister;

    @Enumerated(EnumType.STRING)
    @Column(name = "disability_level", length = 20)
    private DisabilityLevel disabilityLevel;

    @Column(name = "is_low_income")
    private Boolean isLowIncome;

    @Column(name = "bank_card", length = 30)
    private String bankCard;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "residential_address", length = 300)
    private String residentialAddress;

    @Column(name = "community_code", length = 20)
    private String communityCode;

    @Column(name = "community_name", length = 100)
    private String communityName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ElderStatus status;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    @Column(name = "move_to_place", length = 200)
    private String moveToPlace;

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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHouseholdRegister() {
        return householdRegister;
    }

    public void setHouseholdRegister(String householdRegister) {
        this.householdRegister = householdRegister;
    }

    public DisabilityLevel getDisabilityLevel() {
        return disabilityLevel;
    }

    public void setDisabilityLevel(DisabilityLevel disabilityLevel) {
        this.disabilityLevel = disabilityLevel;
    }

    public Boolean isLowIncome() {
        return isLowIncome;
    }

    public void setLowIncome(Boolean lowIncome) {
        isLowIncome = lowIncome;
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

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public ElderStatus getStatus() {
        return status;
    }

    public void setStatus(ElderStatus status) {
        this.status = status;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public String getMoveToPlace() {
        return moveToPlace;
    }

    public void setMoveToPlace(String moveToPlace) {
        this.moveToPlace = moveToPlace;
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
