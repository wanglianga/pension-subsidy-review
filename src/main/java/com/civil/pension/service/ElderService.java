package com.civil.pension.service;

import com.civil.pension.entity.Elder;
import com.civil.pension.enums.ElderStatus;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.ElderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;

@Service
public class ElderService {

    private final ElderRepository elderRepository;

    @Autowired
    public ElderService(ElderRepository elderRepository) {
        this.elderRepository = elderRepository;
    }


    @Transactional
    public Elder create(Elder elder) {
        if (elderRepository.existsByIdCard(elder.getIdCard())) {
            throw new BusinessException("该身份证号已存在");
        }
        if (elder.getBirthDate() != null && elder.getAge() == null) {
            elder.setAge(Period.between(elder.getBirthDate(), LocalDate.now()).getYears());
        }
        if (elder.getStatus() == null) {
            elder.setStatus(ElderStatus.ACTIVE);
        }
        return elderRepository.save(elder);
    }

    @Transactional
    public Elder update(Long id, Elder elder) {
        Elder existing = elderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        if (elder.getIdCard() != null && !elder.getIdCard().equals(existing.getIdCard())) {
            if (elderRepository.existsByIdCard(elder.getIdCard())) {
                throw new BusinessException("该身份证号已存在");
            }
        }

        elder.setId(id);
        if (elder.getBirthDate() != null) {
            elder.setAge(Period.between(elder.getBirthDate(), LocalDate.now()).getYears());
        }
        return elderRepository.save(elder);
    }

    @Transactional
    public void delete(Long id) {
        if (!elderRepository.existsById(id)) {
            throw new BusinessException("老人档案不存在");
        }
        elderRepository.deleteById(id);
    }

    public Elder getById(Long id) {
        return elderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));
    }

    public Elder getByIdCard(String idCard) {
        return elderRepository.findByIdCard(idCard)
                .orElseThrow(() -> new BusinessException("老人档案不存在"));
    }

    public Page<Elder> list(String name, String idCard, String communityCode, ElderStatus status, Pageable pageable) {
        Specification<Elder> spec = Specification.where(null);

        if (StringUtils.hasText(name)) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + name + "%"));
        }
        if (StringUtils.hasText(idCard)) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("idCard"), "%" + idCard + "%"));
        }
        if (StringUtils.hasText(communityCode)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("communityCode"), communityCode));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return elderRepository.findAll(spec, pageable);
    }

    @Transactional
    public Elder markDeceased(Long id, LocalDate deathDate) {
        Elder elder = getById(id);
        elder.setStatus(ElderStatus.DECEASED);
        elder.setDeathDate(deathDate);
        return elderRepository.save(elder);
    }

    @Transactional
    public Elder markMovedOut(Long id, LocalDate moveOutDate, String moveToPlace) {
        Elder elder = getById(id);
        elder.setStatus(ElderStatus.MOVED_OUT);
        elder.setMoveOutDate(moveOutDate);
        elder.setMoveToPlace(moveToPlace);
        return elderRepository.save(elder);
    }
}
