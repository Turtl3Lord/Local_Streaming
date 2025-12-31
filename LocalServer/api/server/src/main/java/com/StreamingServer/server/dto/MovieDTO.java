package com.StreamingServer.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {


    private String title;
    private String description;
    private String coverUrl;
    private String videoUrl;
    private Integer durationMinutes;
    private Integer releaseYear;
}

