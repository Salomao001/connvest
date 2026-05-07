package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "startup_memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long startupId;
    private String role; // Founder, Co-founder, Advisor, Membro
}
