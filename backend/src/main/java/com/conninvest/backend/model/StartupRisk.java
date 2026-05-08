package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "startup_risks")
@Data
@NoArgsConstructor
public class StartupRisk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startupId;
    private String title;
    private String level;

    @Column(length = 1000)
    private String mitigation;

    private Integer sortOrder;
}
