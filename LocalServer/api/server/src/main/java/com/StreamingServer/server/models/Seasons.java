package com.StreamingServer.server.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "seasons",
        uniqueConstraints = @UniqueConstraint(columnNames = {"series_id", "season_number"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seasons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @Column(name = "season_number", nullable = false)
    private Integer seasonNumber;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_year")
    private Integer releaseYear;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relacionamento com Episodes
    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Episodes> episodes = new HashSet<>();
}
