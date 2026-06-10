package com.civil.pension.repository;

import com.civil.pension.entity.Elder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElderRepository extends JpaRepository<Elder, Long>, JpaSpecificationExecutor<Elder> {

    Optional<Elder> findByIdCard(String idCard);

    boolean existsByIdCard(String idCard);
}
