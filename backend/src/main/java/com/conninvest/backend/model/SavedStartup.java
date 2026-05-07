package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_startups", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "startup_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedStartup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "startup_id")
    private Long startupId;

    // Pipeline stage: INTERESTING, IN_CONVERSATION, EVALUATING, ARCHIVED
    private String pipelineStage = "INTERESTING";

    private LocalDateTime savedAt;

    @PrePersist
    public void prePersist() {
        this.savedAt = LocalDateTime.now();
        if (this.pipelineStage == null) this.pipelineStage = "INTERESTING";
    }
}
