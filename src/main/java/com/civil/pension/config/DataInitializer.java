package com.civil.pension.config;

import com.civil.pension.entity.Elder;
import com.civil.pension.entity.SubsidyType;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.enums.DisabilityLevel;
import com.civil.pension.enums.ElderStatus;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.repository.ElderRepository;
import com.civil.pension.repository.SubsidyTypeRepository;
import com.civil.pension.repository.ElderSubsidyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ElderRepository elderRepository;
    private final SubsidyTypeRepository subsidyTypeRepository;
    private final ElderSubsidyRepository elderSubsidyRepository;

    @Autowired
    public DataInitializer(ElderRepository elderRepository, SubsidyTypeRepository subsidyTypeRepository, ElderSubsidyRepository elderSubsidyRepository) {
        this.elderRepository = elderRepository;
        this.subsidyTypeRepository = subsidyTypeRepository;
        this.elderSubsidyRepository = elderSubsidyRepository;
    }


    @Override
    public void run(String... args) {
        if (subsidyTypeRepository.count() == 0) {
            initSubsidyTypes();
        }
        if (elderRepository.count() == 0) {
            initElders();
        }
    }

    private void initSubsidyTypes() {
        SubsidyType highAge = new SubsidyType();
        highAge.setSubsidyCode("HIGH_AGE");
        highAge.setSubsidyName("高龄补贴");
        highAge.setDescription("年满80周岁以上老年人享受的高龄津贴");
        highAge.setMonthlyAmount(new BigDecimal("200.00"));
        highAge.setMinAge(80);
        highAge.setRequireLowIncome(false);
        highAge.setRequireDisability(false);
        highAge.setActive(true);
        subsidyTypeRepository.save(highAge);

        SubsidyType nursing = new SubsidyType();
        nursing.setSubsidyCode("NURSING");
        nursing.setSubsidyName("护理补贴");
        nursing.setDescription("失能老年人享受的护理补贴");
        nursing.setMonthlyAmount(new BigDecimal("300.00"));
        nursing.setMildAmount(new BigDecimal("200.00"));
        nursing.setModerateAmount(new BigDecimal("300.00"));
        nursing.setSevereAmount(new BigDecimal("400.00"));
        nursing.setTotalAmount(new BigDecimal("500.00"));
        nursing.setMinAge(60);
        nursing.setRequireLowIncome(false);
        nursing.setRequireDisability(true);
        nursing.setActive(true);
        subsidyTypeRepository.save(nursing);

        SubsidyType difficulty = new SubsidyType();
        difficulty.setSubsidyCode("DIFFICULTY");
        difficulty.setSubsidyName("困难补贴");
        difficulty.setDescription("低保家庭老年人享受的困难补贴");
        difficulty.setMonthlyAmount(new BigDecimal("250.00"));
        difficulty.setMinAge(60);
        difficulty.setRequireLowIncome(true);
        difficulty.setRequireDisability(false);
        difficulty.setActive(true);
        subsidyTypeRepository.save(difficulty);
    }

    private void initElders() {
        Elder elder1 = createElder("110101194001011234", "张桂兰", "女",
                LocalDate.of(1940, 1, 1), "东城区和平里社区", "北京市东城区和平里街道",
                "110101001", "和平里社区", "6222021234567890123", "中国工商银行",
                "13800138001", DisabilityLevel.NONE, true, ElderStatus.ACTIVE);
        elderRepository.save(elder1);

        Elder elder2 = createElder("110101194505152345", "李建国", "男",
                LocalDate.of(1945, 5, 15), "东城区朝阳门社区", "北京市东城区朝阳门街道",
                "110101002", "朝阳门社区", "6222021234567890456", "中国建设银行",
                "13800138002", DisabilityLevel.MODERATE, false, ElderStatus.ACTIVE);
        elderRepository.save(elder2);

        Elder elder3 = createElder("110101195008203456", "王秀珍", "女",
                LocalDate.of(1950, 8, 20), "东城区交道口社区", "北京市东城区交道口街道",
                "110101003", "交道口社区", "6222021234567890789", "中国农业银行",
                "13800138003", DisabilityLevel.SEVERE, true, ElderStatus.ACTIVE);
        elderRepository.save(elder3);

        Elder elder4 = createElder("110101193812104567", "赵文明", "男",
                LocalDate.of(1938, 12, 10), "东城区安定门社区", "北京市东城区安定门街道",
                "110101004", "安定门社区", "6222021234567890012", "中国银行",
                "13800138004", DisabilityLevel.MILD, false, ElderStatus.ACTIVE);
        elderRepository.save(elder4);

        Elder elder5 = createElder("110101194203255678", "陈淑华", "女",
                LocalDate.of(1942, 3, 25), "东城区北新桥社区", "北京市东城区北新桥街道",
                "110101005", "北新桥社区", "6222021234567890345", "中国邮政储蓄银行",
                "13800138005", DisabilityLevel.NONE, true, ElderStatus.ACTIVE);
        elderRepository.save(elder5);

        SubsidyType highAge = subsidyTypeRepository.findBySubsidyCode("HIGH_AGE").orElse(null);
        SubsidyType nursing = subsidyTypeRepository.findBySubsidyCode("NURSING").orElse(null);
        SubsidyType difficulty = subsidyTypeRepository.findBySubsidyCode("DIFFICULTY").orElse(null);

        if (highAge != null && nursing != null && difficulty != null) {
            createElderSubsidy(elder1.getId(), highAge.getId(), highAge.getSubsidyCode(),
                    highAge.getMonthlyAmount(), SubsidyStatus.ACTIVE);
            createElderSubsidy(elder1.getId(), difficulty.getId(), difficulty.getSubsidyCode(),
                    difficulty.getMonthlyAmount(), SubsidyStatus.ACTIVE);

            createElderSubsidy(elder2.getId(), highAge.getId(), highAge.getSubsidyCode(),
                    highAge.getMonthlyAmount(), SubsidyStatus.ACTIVE);
            createElderSubsidy(elder2.getId(), nursing.getId(), nursing.getSubsidyCode(),
                    nursing.getMonthlyAmount(), SubsidyStatus.UNVERIFIED);

            createElderSubsidy(elder3.getId(), nursing.getId(), nursing.getSubsidyCode(),
                    nursing.getMonthlyAmount(), SubsidyStatus.ACTIVE);
            createElderSubsidy(elder3.getId(), difficulty.getId(), difficulty.getSubsidyCode(),
                    difficulty.getMonthlyAmount(), SubsidyStatus.ACTIVE);

            createElderSubsidy(elder4.getId(), highAge.getId(), highAge.getSubsidyCode(),
                    highAge.getMonthlyAmount(), SubsidyStatus.ACTIVE);

            createElderSubsidy(elder5.getId(), highAge.getId(), highAge.getSubsidyCode(),
                    highAge.getMonthlyAmount(), SubsidyStatus.ACTIVE);
            createElderSubsidy(elder5.getId(), difficulty.getId(), difficulty.getSubsidyCode(),
                    difficulty.getMonthlyAmount(), SubsidyStatus.UNVERIFIED);
        }
    }

    private Elder createElder(String idCard, String name, String gender, LocalDate birthDate,
                              String residentialAddress, String householdRegister,
                              String communityCode, String communityName,
                              String bankCard, String bankName, String contactPhone,
                              DisabilityLevel disabilityLevel, boolean isLowIncome, ElderStatus status) {
        Elder elder = new Elder();
        elder.setIdCard(idCard);
        elder.setName(name);
        elder.setGender(gender);
        elder.setBirthDate(birthDate);
        elder.setAge(LocalDate.now().getYear() - birthDate.getYear());
        elder.setResidentialAddress(residentialAddress);
        elder.setHouseholdRegister(householdRegister);
        elder.setCommunityCode(communityCode);
        elder.setCommunityName(communityName);
        elder.setBankCard(bankCard);
        elder.setBankName(bankName);
        elder.setContactPhone(contactPhone);
        elder.setDisabilityLevel(disabilityLevel);
        elder.setLowIncome(isLowIncome);
        elder.setStatus(status);
        return elder;
    }

    private void createElderSubsidy(Long elderId, Long subsidyTypeId, String subsidyCode,
                                     BigDecimal monthlyAmount, SubsidyStatus status) {
        ElderSubsidy subsidy = new ElderSubsidy();
        subsidy.setElderId(elderId);
        subsidy.setSubsidyTypeId(subsidyTypeId);
        subsidy.setSubsidyCode(subsidyCode);
        subsidy.setMonthlyAmount(monthlyAmount);
        subsidy.setStatus(status);
        subsidy.setStartDate(LocalDate.now().minusMonths(6));
        subsidy.setApprovedBy("系统初始化");
        subsidy.setApproveTime(java.time.LocalDateTime.now());
        elderSubsidyRepository.save(subsidy);
    }
}
