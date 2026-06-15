package com.civil.pension.controller;

import com.civil.pension.common.PageResult;
import com.civil.pension.common.Result;
import com.civil.pension.entity.SubsidyAdjustment;
import com.civil.pension.enums.SubsidyStatus;
import com.civil.pension.service.SubsidyAdjustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subsidy-adjustments")
public class SubsidyAdjustmentController {

    private final SubsidyAdjustmentService subsidyAdjustmentService;

    @Autowired
    public SubsidyAdjustmentController(SubsidyAdjustmentService subsidyAdjustmentService) {
        this.subsidyAdjustmentService = subsidyAdjustmentService;
    }

    @PostMapping("/suspend")
    public Result<SubsidyAdjustment> createSuspend(@RequestBody Map<String, Object> params) {
        Long elderId = Long.valueOf(params.get("elderId").toString());
        Long subsidyTypeId = Long.valueOf(params.get("subsidyTypeId").toString());
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        String approvedBy = params.get("approvedBy") != null ? params.get("approvedBy").toString() : null;
        return Result.success(subsidyAdjustmentService.createSuspend(elderId, subsidyTypeId, reason, approvedBy));
    }

    @PostMapping("/reactivate")
    public Result<SubsidyAdjustment> createReactivate(@RequestBody Map<String, Object> params) {
        Long elderId = Long.valueOf(params.get("elderId").toString());
        Long subsidyTypeId = Long.valueOf(params.get("subsidyTypeId").toString());
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        String approvedBy = params.get("approvedBy") != null ? params.get("approvedBy").toString() : null;
        return Result.success(subsidyAdjustmentService.createReactivate(elderId, subsidyTypeId, reason, approvedBy));
    }

    @PostMapping("/reissue")
    public Result<SubsidyAdjustment> createReissue(@RequestBody Map<String, Object> params) {
        Long elderId = Long.valueOf(params.get("elderId").toString());
        Long subsidyTypeId = Long.valueOf(params.get("subsidyTypeId").toString());
        String startMonth = params.get("startMonth") != null ? params.get("startMonth").toString() : null;
        String endMonth = params.get("endMonth") != null ? params.get("endMonth").toString() : null;
        BigDecimal amount = params.get("amount") != null ? new BigDecimal(params.get("amount").toString()) : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        String approvedBy = params.get("approvedBy") != null ? params.get("approvedBy").toString() : null;
        return Result.success(subsidyAdjustmentService.createReissue(elderId, subsidyTypeId, startMonth, endMonth, amount, reason, approvedBy));
    }

    @PostMapping("/disability-level-change")
    public Result<SubsidyAdjustment> createDisabilityLevelChange(@RequestBody Map<String, Object> params) {
        Long elderId = Long.valueOf(params.get("elderId").toString());
        Long subsidyTypeId = Long.valueOf(params.get("subsidyTypeId").toString());
        String fromLevel = params.get("fromLevel") != null ? params.get("fromLevel").toString() : null;
        String toLevel = params.get("toLevel") != null ? params.get("toLevel").toString() : null;
        BigDecimal fromAmount = params.get("fromAmount") != null ? new BigDecimal(params.get("fromAmount").toString()) : null;
        BigDecimal toAmount = params.get("toAmount") != null ? new BigDecimal(params.get("toAmount").toString()) : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        String approvedBy = params.get("approvedBy") != null ? params.get("approvedBy").toString() : null;
        return Result.success(subsidyAdjustmentService.createDisabilityLevelChange(
                elderId, subsidyTypeId, fromLevel, toLevel, fromAmount, toAmount, reason, approvedBy));
    }

    @GetMapping("/{id}")
    public Result<SubsidyAdjustment> getById(@PathVariable Long id) {
        return Result.success(subsidyAdjustmentService.getById(id));
    }

    @GetMapping("/elder/{elderId}")
    public Result<List<SubsidyAdjustment>> getByElderId(@PathVariable Long elderId) {
        return Result.success(subsidyAdjustmentService.getByElderId(elderId));
    }

    @GetMapping
    public Result<PageResult<Page<SubsidyAdjustment>>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String adjustType,
            @RequestParam(required = false) SubsidyStatus status,
            @RequestParam(required = false) String adjustMonth,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SubsidyAdjustment> page = subsidyAdjustmentService.list(elderId, adjustType, status, adjustMonth, pageable);
        return Result.success(PageResult.of(page.getTotalElements(), pageNum, pageSize, page));
    }

    @GetMapping("/month/{adjustMonth}")
    public Result<List<SubsidyAdjustment>> getByMonth(@PathVariable String adjustMonth) {
        return Result.success(subsidyAdjustmentService.getByMonth(adjustMonth));
    }

    @GetMapping("/type/{adjustType}")
    public Result<List<SubsidyAdjustment>> getByType(@PathVariable String adjustType) {
        return Result.success(subsidyAdjustmentService.getByType(adjustType));
    }
}
