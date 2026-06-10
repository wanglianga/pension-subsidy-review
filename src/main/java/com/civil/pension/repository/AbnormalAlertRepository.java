package com.civil.pension.repository;

import com.civil.pension.entity.AbnormalAlert;
import com.civil.pension.enums.AlertType;
import com.civil.pension.enums.CheckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbnormalAlertRepository extends JpaRepository<AbnormalAlert, Long>, JpaSpecificationExecutor<AbnormalAlert> {

    List<AbnormalAlert> findByElderId(Long elderId);

    List<AbnormalAlert> findByAlertMonth(String alertMonth);

    List<AbnormalAlert> findByStatus(CheckStatus status);

    List<AbnormalAlert> findByAlertType(AlertType alertType);

    List<AbnormalAlert> findByAlertTypeAndStatus(AlertType alertType, CheckStatus status);
}
