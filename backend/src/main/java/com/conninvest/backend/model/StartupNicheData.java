package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "startup_niche_data",
       uniqueConstraints = @UniqueConstraint(columnNames = {"startup_id", "niche_key"}))
@Data
@NoArgsConstructor
public class StartupNicheData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "startup_id")
    private Long startupId;

    @Column(name = "niche_key")
    private String nicheKey;

    @Column(columnDefinition = "TEXT")
    private String fieldValuesJson;
}
