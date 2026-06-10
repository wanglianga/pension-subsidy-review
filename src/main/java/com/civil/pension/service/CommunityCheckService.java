package com.civil.pension.service;

import com.civil.pension.entity.CommunityCheck;
import com.civil.pension.entity.Elder;
import com.civil.pension.enums.CheckStatus;
import com.civil.pension.enums.ElderStatus;
import com.civil.pension.exception.BusinessException;
import com.civil.pension.repository.CommunityCheckRepository;
import com.civil.pension.repository.ElderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CommunityCheckService {

    private final CommunityCheckRepository communityCheckRepository;
    private final ElderRepository elderRepository;
    private final AbnormalAlertService abnormalAlertService;

    @Autowired
    public CommunityCheckService(CommunityCheckRepository communityCheckRepository, ElderRepository elderRepository, AbnormalAlertService abnormalAlertService) {
        this.communityCheckRepository = communityCheckRepository;
        this.elderRepository = elderRepository;
        this.abnormalAlertService = abnormalAlertService;
    }


    @Transactional
    public CommunityCheck create(CommunityCheck check) {
        Elder elder = elderRepository.findById(check.getElderId())
                .orElseThrow(() -> new BusinessException("老人档案不存在"));

        String checkMonth = check.getCheckMonth();
        if (!StringUtils.hasText(checkMonth)) {
            checkMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            check.setCheckMonth(checkMonth);
        }

        if (communityCheckRepository.existsByElderIdAndCheckMonth(check.getElderId(), checkMonth)) {
            throw new BusinessException("该老人本月已核查");
        }

        check.setElderIdCard(elder.getIdCard());
        check.setElderName(elder.getName());
        check.setCommunityCode(elder.getCommunityCode());

        if (check.getStatus() == null) {
            check.setStatus(CheckStatus.PENDING);
        }

        if (check.getCheckDate() == null) {
            check.setCheckDate(LocalDate.now());
        }

        CommunityCheck saved = communityCheckRepository.save(check);

        if (Boolean.FALSE.equals(check.isStillAlive()) ||
            Boolean.FALSE.equals(check.isInCommunity()) ||
            Boolean.FALSE.equals(check.isBankCardValid()) ||
            Boolean.TRUE.equals(check.isStatusChanged())) {
            abnormalAlertService.generateFromCheck(saved);
        }

        return saved;
    }

    @Transactional
    public CommunityCheck update(Long id, CommunityCheck check) {
        CommunityCheck existing = communityCheckRepository.findById(id)
                .orElseThrow(() -> new BusinessException("核查记录不存在"));

        if (existing.getStatus() == CheckStatus.VERIFIED) {
            throw new BusinessException("已审核的核查记录不能修改");
        }

        check.setId(id);
        return communityCheckRepository.save(check);
    }

    @Transactional
    public CommunityCheck audit(Long id, boolean pass, String auditBy, String auditRemark) {
        CommunityCheck check = communityCheckRepository.findById(id)
                .orElseThrow(() -> new BusinessException("核查记录不存在"));

        if (check.getStatus() != CheckStatus.PENDING) {
            throw new BusinessException("核查记录状态不是待审核");
        }

        check.setStatus(pass ? CheckStatus.VERIFIED : CheckStatus.REJECTED);
        check.setAuditBy(auditBy);
        check.setAuditTime(LocalDateTime.now());
        check.setAuditRemark(auditRemark);

        CommunityCheck saved = communityCheckRepository.save(check);

        if (pass) {
            updateElderFromCheck(check);
            abnormalAlertService.handleCheckResult(check);
        }

        return saved;
    }

    @Transactional
    public void updateElderFromCheck(CommunityCheck check) {
        Elder elder = elderRepository.findById(check.getElderId()).orElse(null);
        if (elder == null) return;

        if (Boolean.FALSE.equals(check.isStillAlive())) {
            elder.setStatus(ElderStatus.DECEASED);
            elder.setDeathDate(check.getCheckDate());
            elderRepository.save(elder);
        } else if (Boolean.FALSE.equals(check.isInCommunity())) {
            elder.setStatus(ElderStatus.MOVED_OUT);
            elder.setMoveOutDate(check.getCheckDate());
            if (StringUtils.hasText(check.getChangeReason())) {
                elder.setMoveToPlace(check.getChangeReason());
            }
            elderRepository.save(elder);
        }
    }

    public CommunityCheck getById(Long id) {
        return communityCheckRepository.findById(id)
                .orElseThrow(() -> new BusinessException("核查记录不存在"));
    }

    public List<CommunityCheck> getByElderId(Long elderId) {
        return communityCheckRepository.findByElderId(elderId);
    }

    public Page<CommunityCheck> list(Long elderId, String checkMonth, String communityCode,
                                      CheckStatus status, Pageable pageable) {
        Specification<CommunityCheck> spec = Specification.where(null);

        if (elderId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("elderId"), elderId));
        }
        if (StringUtils.hasText(checkMonth)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("checkMonth"), checkMonth));
        }
        if (StringUtils.hasText(communityCode)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("communityCode"), communityCode));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return communityCheckRepository.findAll(spec, pageable);
    }

    public List<CommunityCheck> getPendingChecks() {
        return communityCheckRepository.findByStatus(CheckStatus.PENDING);
    }

    public List<CommunityCheck> getByMonth(String checkMonth) {
        return communityCheckRepository.findByCheckMonth(checkMonth);
    }
}
