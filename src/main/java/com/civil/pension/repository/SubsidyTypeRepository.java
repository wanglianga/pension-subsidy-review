package com.civil.pension.repository;

import com.civil.pension.entity.SubsidyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SubsidyTypeRepository extends JpaRepository<SubsidyType, Long>, JpaSpecificationExecutor<SubsidyType> {

    Optional<SubsidyType> findBySubsidyCode(String subsidyCode);

    List<SubsidyType> findByIsActiveTrue();
}
