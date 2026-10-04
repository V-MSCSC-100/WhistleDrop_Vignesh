package com.whistledrop.repository;

import com.whistledrop.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {
    Optional<Report> findByCaseCodeHash(String hash);
    long countByStatus(ReportStatus status);
}
