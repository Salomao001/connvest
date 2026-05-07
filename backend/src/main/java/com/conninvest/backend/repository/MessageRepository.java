package com.conninvest.backend.repository;

import com.conninvest.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId OR m.receiverId = :userId) ORDER BY m.createdAt ASC")
    List<Message> findConversationMessages(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE (m.senderId = :u1 AND m.receiverId = :u2) OR (m.senderId = :u2 AND m.receiverId = :u1) ORDER BY m.createdAt ASC")
    List<Message> findBetweenUsers(@Param("u1") Long u1, @Param("u2") Long u2);

    long countByReceiverIdAndReadFalse(Long receiverId);
}
