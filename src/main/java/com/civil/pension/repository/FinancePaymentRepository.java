package com.civil.pension.repository;

import com.civil.pension.entity.FinancePayment;
import com.civil.pension.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancePaymentRepository extends JpaRepository<FinancePayment, Long>, JpaSpecificationExecutor<FinancePayment> {

    List<FinancePayment> findByElderId(Long elderId);

    List<FinancePayment> findByPaymentMonth(String paymentMonth);

    List<FinancePayment> findByPaymentMonthAndStatus(String paymentMonth, PaymentStatus status);

    List<FinancePayment> findByBatchNo(String batchNo);

    List<FinancePayment> findByStatus(PaymentStatus status);
}
