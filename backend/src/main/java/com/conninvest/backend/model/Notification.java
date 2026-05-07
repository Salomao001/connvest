package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;
    private String type; // LIKE, COMMENT, FOLLOW, PROPOSAL, MESSAGE, INVITE
    private String title;

    @Column(length = 500)
    private String body;

    private Long relatedId;
    private String relatedType; // post, user, startup, proposal
    private Boolean read = false;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.read == null) this.read = false;
    }
}
