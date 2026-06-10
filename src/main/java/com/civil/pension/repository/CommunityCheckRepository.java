package com.civil.pension.repository;

import com.civil.pension.entity.CommunityCheck;
import com.civil.pension.enums.CheckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCheckRepository extends JpaRepository<CommunityCheck, Long>, JpaSpecificationExecutor<CommunityCheck> {

    List<CommunityCheck> findByElderId(Long elderId);

    List<CommunityCheck> findByCheckMonth(String checkMonth);

    List<CommunityCheck> findByCheckMonthAndCommunityCode(String checkMonth, String communityCode);

    List<CommunityCheck> findByStatus(CheckStatus status);

    boolean existsByElderIdAndCheckMonth(Long elderId, String checkMonth);
}
