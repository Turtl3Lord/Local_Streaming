package com.StreamingServer.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for Movie resources used in tests and API payloads.
 * Matches the test/mock JSON in src/test/resources/mockData/movies.json
 * (excludes database fields like id, createdAt, updatedAt).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private String coverUrl;
    private String videoUrl;
    private Integer durationMinutes;
    private Integer releaseYear;
}

