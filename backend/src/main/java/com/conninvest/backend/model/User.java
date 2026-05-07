package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(unique = true)
    private String email;
    private String password;
    private String photo;
    
    @Column(length = 1000)
    private String bio;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profile_types", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "profile_type")
    private java.util.List<String> profileTypes; // Founder, Investidor, Advisor, Buscando co-founder, Outro
    
    @Column(length = 1000)
    private String pastExperiences;
    private String interests;
    @Column(length = 1000)
    private String externalLinks;
    private Boolean seekingCoFounder;
    private String desiredCoFounderType;
    private String coFounderArea;
    private String coFounderDedication;

    @Column(length = 1000)
    private String coFounderDescription;
    private String mainSkills;
    
    // Investor specific
    private String investorType;
    private String sectorsOfInterest;
    private String stagesOfInterest;
    private String averageTicket;
    private String location;
    
    @Column(length = 1000)
    private String investmentHistory;
    private String valueAdd;
    private String operationStyle;
    private String contactPreference;
}
