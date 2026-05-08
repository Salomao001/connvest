package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "niche_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NicheConfig {
    @Id
    @Column(name = "niche_key")
    private String key;

    private String label;
    private Integer sortOrder;

    @Column(columnDefinition = "TEXT")
    private String fieldsJson;
}
