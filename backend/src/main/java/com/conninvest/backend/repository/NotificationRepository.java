package com.conninvest.backend.repository;

import com.conninvest.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    List<Notification> findByRecipientIdAndTypeNotOrderByCreatedAtDesc(Long recipientId, String type);
    long countByRecipientIdAndReadFalse(Long recipientId);
    long countByRecipientIdAndReadFalseAndTypeNot(Long recipientId, String type);
}
