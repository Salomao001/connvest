package com.conninvest.backend.repository;

import com.conninvest.backend.model.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {
    List<ConnectionRequest> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<ConnectionRequest> findBySenderIdOrderByCreatedAtDesc(Long senderId);
}
