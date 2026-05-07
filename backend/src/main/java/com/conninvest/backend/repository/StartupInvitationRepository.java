package com.conninvest.backend.repository;

import com.conninvest.backend.model.StartupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StartupInvitationRepository extends JpaRepository<StartupInvitation, Long> {
    List<StartupInvitation> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<StartupInvitation> findByStartupIdOrderByCreatedAtDesc(Long startupId);
    Optional<StartupInvitation> findByStartupIdAndReceiverIdAndStatus(Long startupId, Long receiverId, String status);
}
