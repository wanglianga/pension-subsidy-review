package com.civil.pension.service;

import com.civil.pension.entity.Elder;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.entity.SubsidyType;
import com.civil.pension.enums.ElderStatus;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.ElderRepository;
import com.civil.pension.repository.ElderSubsidyRepository;
import com.civil.pension.repository.SubsidyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
public class ElderSubsidyService {

    private final ElderSubsidyRepository elderSubsidyRepository;
    private final ElderRepository elderRepository;
    private final SubsidyTypeRepository subsidyTypeRepository;

    @Autowired
    public ElderSubsidyService(ElderSubsidyRepository elderSubsidyRepository, ElderRepository elderRepository, SubsidyTypeRepository subsidyTypeRepository) {
        this.elderSubsidyRepository = elderSubsidyRepository;
        this.elderRepository = elderRepository;
        this.subsidyTypeRepository = subsidyTypeRepository;
    }


    @Transactional
    public ElderSubsidy apply(Long elderId, Long subsidyTypeId, String approvedBy, String remark) {
        Elder elder = elderRepository.findById(elderId)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        if (elder.getStatus() != ElderStatus.ACTIVE) {
            throw new BusinessException("老人状态异常，无法申请补贴");
        }

        SubsidyType subsidyType = subsidyTypeRepository.findById(subsidyTypeId)
                .orElseThrow(() -> new BusinessException("补贴类型不存在"));

        if (!subsidyType.isActive()) {
            throw new BusinessException("该补贴类型已停用");
        }

        if (elderSubsidyRepository.findByElderIdAndSubsidyTypeId(elderId, subsidyTypeId).isPresent()) {
            throw new BusinessException("该老人已申请过此补贴");
        }

        int age = elder.getAge() != null ? elder.getAge() :
                (elder.getBirthDate() != null ? Period.between(elder.getBirthDate(), LocalDate.now()).getYears() : 0);

        if (subsidyType.getMinAge() != null && age < subsidyType.getMinAge()) {
            throw new BusinessException("年龄不符合补贴要求，需要年满" + subsidyType.getMinAge() + "岁");
        }

        if (Boolean.TRUE.equals(subsidyType.isRequireLowIncome()) && !Boolean.TRUE.equals(elder.isLowIncome())) {
            throw new BusinessException("需要低保资格才能申请此补贴");
        }

        ElderSubsidy elderSubsidy = new ElderSubsidy();
        elderSubsidy.setElderId(elderId);
        elderSubsidy.setSubsidyTypeId(subsidyTypeId);
        elderSubsidy.setSubsidyCode(subsidyType.getSubsidyCode());
        elderSubsidy.setStatus(SubsidyStatus.UNVERIFIED);
        elderSubsidy.setMonthlyAmount(subsidyType.getMonthlyAmount());
        elderSubsidy.setStartDate(LocalDate.now());
        elderSubsidy.setApprovedBy(approvedBy);
        elderSubsidy.setApproveTime(LocalDateTime.now());
        elderSubsidy.setRemark(remark);

        return elderSubsidyRepository.save(elderSubsidy);
    }

    @Transactional
    public ElderSubsidy approve(Long id, String approvedBy) {
        ElderSubsidy subsidy = getById(id);
        if (subsidy.getStatus() != SubsidyStatus.UNVERIFIED) {
            throw new BusinessException("补贴状态不是未核实，无法审核");
        }
        subsidy.setStatus(SubsidyStatus.ACTIVE);
        subsidy.setApprovedBy(approvedBy);
        subsidy.setApproveTime(LocalDateTime.now());
        return elderSubsidyRepository.save(subsidy);
    }

    @Transactional
    public ElderSubsidy suspend(Long id, String reason, String operator) {
        ElderSubsidy subsidy = getById(id);
        if (subsidy.getStatus() != SubsidyStatus.ACTIVE) {
            throw new BusinessException("补贴状态不是有效状态，无法停发");
        }
        subsidy.setStatus(SubsidyStatus.SUSPENDED);
        subsidy.setEndDate(LocalDate.now());
        subsidy.setRemark(reason);
        return elderSubsidyRepository.save(subsidy);
    }

    @Transactional
    public ElderSubsidy reactivate(Long id, String reason, String operator) {
        ElderSubsidy subsidy = getById(id);
        if (subsidy.getStatus() != SubsidyStatus.SUSPENDED) {
            throw new BusinessException("补贴状态不是停发状态，无法恢复");
        }
        subsidy.setStatus(SubsidyStatus.ACTIVE);
        subsidy.setEndDate(null);
        subsidy.setRemark(reason);
        return elderSubsidyRepository.save(subsidy);
    }

    @Transactional
    public ElderSubsidy cancel(Long id, String reason) {
        ElderSubsidy subsidy = getById(id);
        subsidy.setStatus(SubsidyStatus.CANCELLED);
        subsidy.setEndDate(LocalDate.now());
        subsidy.setRemark(reason);
        return elderSubsidyRepository.save(subsidy);
    }

    public ElderSubsidy getById(Long id) {
        return elderSubsidyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("补贴记录不存在"));
    }

    public List<ElderSubsidy> getByElderId(Long elderId) {
        return elderSubsidyRepository.findByElderId(elderId);
    }

    public Page<ElderSubsidy> list(Long elderId, Long subsidyTypeId, SubsidyStatus status, String subsidyCode, Pageable pageable) {
        Specification<ElderSubsidy> spec = Specification.where(null);

        if (elderId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("elderId"), elderId));
        }
        if (subsidyTypeId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("subsidyTypeId"), subsidyTypeId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (StringUtils.hasText(subsidyCode)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("subsidyCode"), subsidyCode));
        }

        return elderSubsidyRepository.findAll(spec, pageable);
    }

    public List<ElderSubsidy> getActiveSubsidies() {
        return elderSubsidyRepository.findByStatus(SubsidyStatus.ACTIVE);
    }

    public List<ElderSubsidy> getUnverifiedSubsidies() {
        return elderSubsidyRepository.findByStatus(SubsidyStatus.UNVERIFIED);
    }
}
