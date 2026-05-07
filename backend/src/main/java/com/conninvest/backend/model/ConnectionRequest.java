package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "connection_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private String senderName;
    private String senderPhotoUrl;
    private String senderRole;

    private Long receiverId;

    private String message;
    private String status; // PENDING, ACCEPTED, REJECTED

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}
