package com.StreamingServer.server.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "movies")  // ⚠️ Sempre especifique o nome da tabela
@Data
@Builder
@NoArgsConstructor  // ⚠️ JPA precisa de construtor vazio
@AllArgsConstructor
public class Movies {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 255)
        private String title;

        @Column(columnDefinition = "TEXT")
        private String description;

        @Column(name = "cover_url", length = 500)
        private String coverUrl;

        @Column(name = "video_url", nullable = false, length = 500)
        private String videoUrl;

        @Column(name = "duration_minutes")
        private Integer durationMinutes;

        @Column(name = "release_year")
        private Integer releaseYear;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;
    }



