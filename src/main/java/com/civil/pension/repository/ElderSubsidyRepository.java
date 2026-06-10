package com.civil.pension.repository;

import com.civil.pension.entity.ElderSubsidy;
import com.civil.pension.enums.SubsidyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElderSubsidyRepository extends JpaRepository<ElderSubsidy, Long>, JpaSpecificationExecutor<ElderSubsidy> {

    List<ElderSubsidy> findByElderId(Long elderId);

    Optional<ElderSubsidy> findByElderIdAndSubsidyTypeId(Long elderId, Long subsidyTypeId);

    List<ElderSubsidy> findByElderIdAndStatus(Long elderId, SubsidyStatus status);

    List<ElderSubsidy> findByStatus(SubsidyStatus status);

    List<ElderSubsidy> findByStatusIn(List<SubsidyStatus> statuses);
}
