package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "startups")
@Data
@NoArgsConstructor
public class Startup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Identidade ---
    private String name;
    private String logo;
    private String shortDescription;
    private String pitch;

    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String fullDescription;

    @Column(length = 2000)
    private String problemDescription;

    @Column(length = 2000)
    private String solutionDescription;

    @Column(length = 2000)
    private String competitiveDifferential;

    private String sector;
    private String stage;
    private String location;
    private String websiteUrl;
    private String pitchDeckUrl;
    private Integer foundingYear;

    // --- Mercado ---
    private String tam;
    private String sam;
    private String som;
    private String marketSource;

    @Column(length = 1000)
    private String marketTiming;

    // --- Financeiro ---
    private String mainMetrics;
    private String monthlyRevenue;
    private String annualRevenue;
    private String mrr;
    private String arr;
    private String cac;
    private String ltv;
    private String grossMargin;
    private String burnRate;
    private String runway;
    private Integer usersCount;
    private String growthPercent;
    private Integer clientsCount;
    private String churn;
    private Boolean metricsPublic;

    // --- Rodada atual ---
    private String roundStatus;
    private String roundAmountRaised;
    private String roundValuation;
    private String roundStructure;
    private String roundPercentCommitted;

    @Column(length = 1000)
    private String roundCapitalUse;

    // --- Objetivo e meta ---
    private String currentObjective;
    private String ranking;
    private String badges;

    // --- Nichos selecionados ---
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "startup_selected_niches", joinColumns = @JoinColumn(name = "startup_id"))
    @Column(name = "niche_key")
    private List<String> selectedNiches = new ArrayList<>();

    // --- Visibilidade por campo (JSON: {"mrr": false, "cac": false}) ---
    @Column(columnDefinition = "TEXT")
    private String fieldVisibilityJson = "{}";

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.metricsPublic == null) this.metricsPublic = true;
        if (this.fieldVisibilityJson == null) this.fieldVisibilityJson = "{}";
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
