package com.conninvest.backend.repository;

import com.conninvest.backend.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<Proposal> findBySenderIdOrderByCreatedAtDesc(Long senderId);
}
