package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cap_table_entries")
@Data
@NoArgsConstructor
public class CapTableEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startupId;
    private String name;
    private String type;
    private Double percentage;
    private Integer sortOrder;
}
