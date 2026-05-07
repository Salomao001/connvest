package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "startups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Startup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String logo;
    private String shortDescription;
    
    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String fullDescription;
    
    private String sector;
    private String stage;
    private String location;
    private String websiteUrl;
    private String mainMetrics;
    private String monthlyRevenue;
    private Integer usersCount;
    private String growthPercent;
    private Integer clientsCount;
    private String mrr;
    private String churn;
    private Boolean metricsPublic;
    private String currentObjective;
    private String ranking;
    private String badges;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.metricsPublic == null) {
            this.metricsPublic = true;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
