package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private String senderName;
    private String senderPhoto;

    private Long receiverId;
    private String receiverName;

    private String type; // INVESTMENT, CO_FOUNDER, ADVISOR

    // Common fields
    private Long startupId;
    private String startupName;

    @Column(length = 2000)
    private String pitch;

    private String status; // PENDING, VIEWED, ACCEPTED, REJECTED, ARCHIVED

    // Investment-specific
    private String seekedValue;
    private String capitalUse;
    private String whyThisInvestor;

    // Co-founder specific
    private String expectedRole;
    private String desiredProfile;
    private String expectedDedication;
    private String equityOffer;

    // Advisor specific
    private String supportArea;
    private String relationshipType;
    private String possibleCompensation;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }
}
