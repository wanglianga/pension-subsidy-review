package com.civil.pension.repository;

import com.civil.pension.entity.SubsidyAdjustment;
import com.civil.pension.enums.SubsidyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubsidyAdjustmentRepository extends JpaRepository<SubsidyAdjustment, Long>, JpaSpecificationExecutor<SubsidyAdjustment> {

    List<SubsidyAdjustment> findByElderId(Long elderId);

    List<SubsidyAdjustment> findByAdjustMonth(String adjustMonth);

    List<SubsidyAdjustment> findByStatus(SubsidyStatus status);

    List<SubsidyAdjustment> findByAdjustType(String adjustType);
}
