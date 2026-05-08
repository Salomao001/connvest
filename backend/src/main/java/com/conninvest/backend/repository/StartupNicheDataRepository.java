package com.conninvest.backend.repository;

import com.conninvest.backend.model.StartupNicheData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StartupNicheDataRepository extends JpaRepository<StartupNicheData, Long> {
    List<StartupNicheData> findByStartupId(Long startupId);
    Optional<StartupNicheData> findByStartupIdAndNicheKey(Long startupId, String nicheKey);
    void deleteByStartupIdAndNicheKey(Long startupId, String nicheKey);
}
