package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "startup_invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartupInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startupId;
    private String startupName;

    private Long senderId;
    private String senderName;

    private Long receiverId;
    private String receiverName;

    private String role;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}
