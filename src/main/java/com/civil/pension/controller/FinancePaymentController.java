package com.civil.pension.controller;

import com.civil.pension.common.PageResult;
import com.civil.pension.common.Result;
import com.civil.pension.entity.FinancePayment;
import com.civil.pension.enums.PaymentStatus;
import com.civil.pension.service.FinancePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/finance-payments")
public class FinancePaymentController {

    private final FinancePaymentService financePaymentService;

    @Autowired
    public FinancePaymentController(FinancePaymentService financePaymentService) {
        this.financePaymentService = financePaymentService;
    }

    @PostMapping("/generate-monthly")
    public Result<List<FinancePayment>> generateMonthlyPayment(
            @RequestParam(required = false) String paymentMonth) {
        return Result.success(financePaymentService.generateMonthlyPayment(paymentMonth));
    }

    @PostMapping("/generate-reissue/{adjustId}")
    public Result<List<FinancePayment>> generateReissuePayment(@PathVariable Long adjustId) {
        return Result.success(financePaymentService.generateReissuePayment(adjustId));
    }

    @PostMapping("/execute")
    public Result<List<FinancePayment>> executePayment(@RequestParam String batchNo) {
        return Result.success(financePaymentService.executePayment(batchNo));
    }

    @PostMapping("/{id}/retry")
    public Result<FinancePayment> retryPayment(@PathVariable Long id) {
        return Result.success(financePaymentService.retryPayment(id));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelPayment(@PathVariable Long id, @RequestParam String reason) {
        financePaymentService.cancelPayment(id, reason);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<FinancePayment> getById(@PathVariable Long id) {
        return Result.success(financePaymentService.getById(id));
    }

    @GetMapping("/elder/{elderId}")
    public Result<List<FinancePayment>> getByElderId(@PathVariable Long elderId) {
        return Result.success(financePaymentService.getByElderId(elderId));
    }

    @GetMapping
    public Result<PageResult<Page<FinancePayment>>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String paymentMonth,
            @RequestParam(required = false) String batchNo,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Boolean isReissue,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<FinancePayment> page = financePaymentService.list(elderId, paymentMonth, batchNo, status, isReissue, pageable);
        return Result.success(PageResult.of(page.getTotalElements(), pageNum, pageSize, page));
    }

    @GetMapping("/month/{paymentMonth}")
    public Result<List<FinancePayment>> getByMonth(@PathVariable String paymentMonth) {
        return Result.success(financePaymentService.getByMonth(paymentMonth));
    }

    @GetMapping("/batch/{batchNo}")
    public Result<List<FinancePayment>> getByBatchNo(@PathVariable String batchNo) {
        return Result.success(financePaymentService.getByBatchNo(batchNo));
    }

    @GetMapping("/pending")
    public Result<List<FinancePayment>> getPendingPayments() {
        return Result.success(financePaymentService.getPendingPayments());
    }
}
