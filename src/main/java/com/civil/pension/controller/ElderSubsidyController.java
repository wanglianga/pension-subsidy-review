package com.civil.pension.controller;

import com.civil.pension.common.PageResult;
import com.civil.pension.common.Result;
import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.service.ElderSubsidyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/elder-subsidies")
public class ElderSubsidyController {

    private final ElderSubsidyService elderSubsidyService;

    @Autowired
    public ElderSubsidyController(ElderSubsidyService elderSubsidyService) {
        this.elderSubsidyService = elderSubsidyService;
    }

    @PostMapping("/apply")
    public Result<ElderSubsidy> apply(@RequestBody Map<String, Object> params) {
        Long elderId = Long.valueOf(params.get("elderId").toString());
        Long subsidyTypeId = Long.valueOf(params.get("subsidyTypeId").toString());
        String approvedBy = params.get("approvedBy") != null ? params.get("approvedBy").toString() : null;
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        return Result.success(elderSubsidyService.apply(elderId, subsidyTypeId, approvedBy, remark));
    }

    @PostMapping("/{id}/approve")
    public Result<ElderSubsidy> approve(
            @PathVariable Long id,
            @RequestParam String approvedBy) {
        return Result.success(elderSubsidyService.approve(id, approvedBy));
    }

    @PostMapping("/{id}/suspend")
    public Result<ElderSubsidy> suspend(
            @PathVariable Long id,
            @RequestBody Map<String, String> params) {
        String reason = params.get("reason");
        String operator = params.get("operator");
        return Result.success(elderSubsidyService.suspend(id, reason, operator));
    }

    @PostMapping("/{id}/reactivate")
    public Result<ElderSubsidy> reactivate(
            @PathVariable Long id,
            @RequestBody Map<String, String> params) {
        String reason = params.get("reason");
        String operator = params.get("operator");
        return Result.success(elderSubsidyService.reactivate(id, reason, operator));
    }

    @PostMapping("/{id}/cancel")
    public Result<ElderSubsidy> cancel(
            @PathVariable Long id,
            @RequestParam String reason) {
        return Result.success(elderSubsidyService.cancel(id, reason));
    }

    @GetMapping("/{id}")
    public Result<ElderSubsidy> getById(@PathVariable Long id) {
        return Result.success(elderSubsidyService.getById(id));
    }

    @GetMapping("/elder/{elderId}")
    public Result<List<ElderSubsidy>> getByElderId(@PathVariable Long elderId) {
        return Result.success(elderSubsidyService.getByElderId(elderId));
    }

    @GetMapping
    public Result<PageResult<Page<ElderSubsidy>>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) Long subsidyTypeId,
            @RequestParam(required = false) SubsidyStatus status,
            @RequestParam(required = false) String subsidyCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ElderSubsidy> page = elderSubsidyService.list(elderId, subsidyTypeId, status, subsidyCode, pageable);
        return Result.success(PageResult.of(page.getTotalElements(), pageNum, pageSize, page));
    }

    @GetMapping("/active")
    public Result<List<ElderSubsidy>> getActiveSubsidies() {
        return Result.success(elderSubsidyService.getActiveSubsidies());
    }

    @GetMapping("/unverified")
    public Result<List<ElderSubsidy>> getUnverifiedSubsidies() {
        return Result.success(elderSubsidyService.getUnverifiedSubsidies());
    }
}
