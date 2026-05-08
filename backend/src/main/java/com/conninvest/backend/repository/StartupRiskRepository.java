package com.conninvest.backend.repository;

import com.conninvest.backend.model.StartupRisk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartupRiskRepository extends JpaRepository<StartupRisk, Long> {
    List<StartupRisk> findByStartupIdOrderBySortOrderAsc(Long startupId);
    void deleteByStartupId(Long startupId);
}
