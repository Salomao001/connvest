package com.conninvest.backend.repository;

import com.conninvest.backend.model.StartupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StartupMembershipRepository extends JpaRepository<StartupMembership, Long> {
    List<StartupMembership> findByUserId(Long userId);
    List<StartupMembership> findByStartupId(Long startupId);
    Optional<StartupMembership> findByStartupIdAndUserId(Long startupId, Long userId);
}
