package com.conninvest.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;
    private Long startupId;
    private String authorName;
    private String authorType; // Founder, Startup, etc.
    private String authorPhoto;

    private String type; // STRUCTURED_UPDATE, FREE_TEXT

    @Column(length = 2000)
    private String content;

    // Structured Update Fields
    @ElementCollection
    @CollectionTable(name = "post_metrics", joinColumns = @JoinColumn(name = "post_id"))
    private java.util.List<Metric> metrics = new java.util.ArrayList<>();
    
    private String badge;

    private LocalDateTime createdAt;

    private Integer likesCount = 0;
    private Integer commentsCount = 0;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
