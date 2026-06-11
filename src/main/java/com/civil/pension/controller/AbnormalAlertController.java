package com.civil.pension.controller;

import com.civil.pension.common.PageResult;
import com.civil.pension.common.Result;
import com.civil.pension.entity.AbnormalAlert;
import com.civil.pension.enums.AlertType;
import com.civil.pension.enums.CheckStatus;
import com.civil.pension.service.AbnormalAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/abnormal-alerts")
public class AbnormalAlertController {

    private final AbnormalAlertService abnormalAlertService;

    @Autowired
    public AbnormalAlertController(AbnormalAlertService abnormalAlertService) {
        this.abnormalAlertService = abnormalAlertService;
    }

    @PostMapping
    public Result<AbnormalAlert> create(@RequestBody AbnormalAlert alert) {
        return Result.success(abnormalAlertService.createAlert(
                alert.getElderId(),
                alert.getRelatedCheckId(),
                alert.getAlertType(),
                alert.getAlertMonth(),
                alert.getAlertReason(),
                alert.getCommunityCode()
        ));
    }

    @PostMapping("/{id}/handle")
    public Result<AbnormalAlert> handleAlert(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        boolean approve = Boolean.TRUE.equals(params.get("approve"));
        String handledBy = params.get("handledBy") != null ? params.get("handledBy").toString() : null;
        String handleResult = params.get("handleResult") != null ? params.get("handleResult").toString() : null;
        return Result.success(abnormalAlertService.handleAlert(id, approve, handledBy, handleResult));
    }

    @PostMapping("/generate-unverified")
    public Result<Void> generateUnverifiedAlerts() {
        abnormalAlertService.generateUnverifiedAlerts();
        return Result.success("已生成未核实信息预警", null);
    }

    @GetMapping("/{id}")
    public Result<AbnormalAlert> getById(@PathVariable Long id) {
        return Result.success(abnormalAlertService.getById(id));
    }

    @GetMapping("/elder/{elderId}")
    public Result<List<AbnormalAlert>> getByElderId(@PathVariable Long elderId) {
        return Result.success(abnormalAlertService.getByElderId(elderId));
    }

    @GetMapping
    public Result<PageResult<Page<AbnormalAlert>>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) AlertType alertType,
            @RequestParam(required = false) CheckStatus status,
            @RequestParam(required = false) String alertMonth,
            @RequestParam(required = false) String communityCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<AbnormalAlert> page = abnormalAlertService.list(elderId, alertType, status, alertMonth, communityCode, pageable);
        return Result.success(PageResult.of(page.getTotalElements(), pageNum, pageSize, page));
    }

    @GetMapping("/pending")
    public Result<List<AbnormalAlert>> getPendingAlerts() {
        return Result.success(abnormalAlertService.getPendingAlerts());
    }

    @GetMapping("/type/{alertType}")
    public Result<List<AbnormalAlert>> getByType(@PathVariable AlertType alertType) {
        return Result.success(abnormalAlertService.getByType(alertType));
    }
}
