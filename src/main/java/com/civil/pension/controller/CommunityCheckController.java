package com.civil.pension.controller;

import com.civil.pension.common.PageResult;
import com.civil.pension.common.Result;
import com.civil.pension.entity.CommunityCheck;
import com.civil.pension.enums.CheckStatus;
import com.civil.pension.service.CommunityCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/community-checks")
public class CommunityCheckController {

    private final CommunityCheckService communityCheckService;

    @Autowired
    public CommunityCheckController(CommunityCheckService communityCheckService) {
        this.communityCheckService = communityCheckService;
    }


    @PostMapping
    public Result<CommunityCheck> create(@RequestBody CommunityCheck check) {
        return Result.success(communityCheckService.create(check));
    }

    @PutMapping("/{id}")
    public Result<CommunityCheck> update(@PathVariable Long id, @RequestBody CommunityCheck check) {
        return Result.success(communityCheckService.update(id, check));
    }

    @PostMapping("/{id}/audit")
    public Result<CommunityCheck> audit(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        boolean pass = Boolean.TRUE.equals(params.get("pass"));
        String auditBy = params.get("auditBy") != null ? params.get("auditBy").toString() : null;
        String auditRemark = params.get("auditRemark") != null ? params.get("auditRemark").toString() : null;
        return Result.success(communityCheckService.audit(id, pass, auditBy, auditRemark));
    }

    @GetMapping("/{id}")
    public Result<CommunityCheck> getById(@PathVariable Long id) {
        return Result.success(communityCheckService.getById(id));
    }

    @GetMapping("/elder/{elderId}")
    public Result<List<CommunityCheck>> getByElderId(@PathVariable Long elderId) {
        return Result.success(communityCheckService.getByElderId(elderId));
    }

    @GetMapping
    public Result<PageResult<Page<CommunityCheck>>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String checkMonth,
            @RequestParam(required = false) String communityCode,
            @RequestParam(required = false) CheckStatus status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CommunityCheck> page = communityCheckService.list(elderId, checkMonth, communityCode, status, pageable);
        return Result.success(PageResult.of(page.getTotalElements(), pageNum, pageSize, page));
    }

    @GetMapping("/pending")
    public Result<List<CommunityCheck>> getPendingChecks() {
        return Result.success(communityCheckService.getPendingChecks());
    }

    @GetMapping("/month/{checkMonth}")
    public Result<List<CommunityCheck>> getByMonth(@PathVariable String checkMonth) {
        return Result.success(communityCheckService.getByMonth(checkMonth));
    }
}
