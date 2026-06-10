package com.civil.pension.controller;

import com.civil.pension.common.Result;
import com.civil.pension.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping("/monthly")
    public Result<Void> runMonthlyReview(@RequestParam(required = false) String reviewMonth) {
        reviewService.runMonthlyReview(reviewMonth);
        return Result.success("月度复核已执行", null);
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        return Result.success(reviewService.getDashboardStats());
    }

    @PostMapping("/process-check/{checkId}")
    public Result<Map<String, Object>> processCheckAndAlert(
            @PathVariable Long checkId,
            @RequestBody Map<String, Object> params) {
        boolean pass = Boolean.TRUE.equals(params.get("pass"));
        String operator = params.get("operator") != null ? params.get("operator").toString() : null;
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        return Result.success(reviewService.processCheckAndAlert(checkId, pass, operator, remark));
    }

    @PostMapping("/historical-reissue")
    public Result<Map<String, Object>> createHistoricalReissue(@RequestBody Map<String, Object> params) {
        Long elderId = Long.valueOf(params.get("elderId").toString());
        Long subsidyTypeId = Long.valueOf(params.get("subsidyTypeId").toString());
        String startMonth = params.get("startMonth") != null ? params.get("startMonth").toString() : null;
        String endMonth = params.get("endMonth") != null ? params.get("endMonth").toString() : null;
        BigDecimal amount = params.get("amount") != null ? new BigDecimal(params.get("amount").toString()) : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        String operator = params.get("operator") != null ? params.get("operator").toString() : null;
        return Result.success(reviewService.createHistoricalReissue(elderId, subsidyTypeId, startMonth, endMonth, amount, reason, operator));
    }
}
