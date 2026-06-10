package com.civil.pension.service;

import com.civil.pension.entity.SubsidyType;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.SubsidyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SubsidyTypeService {

    private final SubsidyTypeRepository subsidyTypeRepository;

    @Autowired
    public SubsidyTypeService(SubsidyTypeRepository subsidyTypeRepository) {
        this.subsidyTypeRepository = subsidyTypeRepository;
    }


    @Transactional
    public SubsidyType create(SubsidyType subsidyType) {
        if (subsidyTypeRepository.findBySubsidyCode(subsidyType.getSubsidyCode()).isPresent()) {
            throw new BusinessException("补贴编码已存在");
        }
        if (subsidyType.isActive() == null) {
            subsidyType.setActive(true);
        }
        return subsidyTypeRepository.save(subsidyType);
    }

    @Transactional
    public SubsidyType update(Long id, SubsidyType subsidyType) {
        SubsidyType existing = subsidyTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("补贴类型不存在"));

        if (subsidyType.getSubsidyCode() != null &&
                !subsidyType.getSubsidyCode().equals(existing.getSubsidyCode())) {
            if (subsidyTypeRepository.findBySubsidyCode(subsidyType.getSubsidyCode()).isPresent()) {
                throw new BusinessException("补贴编码已存在");
            }
        }

        subsidyType.setId(id);
        return subsidyTypeRepository.save(subsidyType);
    }

    @Transactional
    public void delete(Long id) {
        if (!subsidyTypeRepository.existsById(id)) {
            throw new BusinessException("补贴类型不存在");
        }
        subsidyTypeRepository.deleteById(id);
    }

    public SubsidyType getById(Long id) {
        return subsidyTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("补贴类型不存在"));
    }

    public SubsidyType getByCode(String subsidyCode) {
        return subsidyTypeRepository.findBySubsidyCode(subsidyCode)
                .orElseThrow(() -> new BusinessException("补贴类型不存在"));
    }

    public Page<SubsidyType> list(String subsidyCode, String subsidyName, Boolean isActive, Pageable pageable) {
        Specification<SubsidyType> spec = Specification.where(null);

        if (StringUtils.hasText(subsidyCode)) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("subsidyCode"), "%" + subsidyCode + "%"));
        }
        if (StringUtils.hasText(subsidyName)) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("subsidyName"), "%" + subsidyName + "%"));
        }
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        return subsidyTypeRepository.findAll(spec, pageable);
    }

    public List<SubsidyType> listActive() {
        return subsidyTypeRepository.findByIsActiveTrue();
    }
}
