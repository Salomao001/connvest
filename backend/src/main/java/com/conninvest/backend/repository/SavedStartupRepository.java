package com.conninvest.backend.repository;

import com.conninvest.backend.model.SavedStartup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedStartupRepository extends JpaRepository<SavedStartup, Long> {
    List<SavedStartup> findByUserId(Long userId);
    Optional<SavedStartup> findByUserIdAndStartupId(Long userId, Long startupId);
    void deleteByUserIdAndStartupId(Long userId, Long startupId);
}
