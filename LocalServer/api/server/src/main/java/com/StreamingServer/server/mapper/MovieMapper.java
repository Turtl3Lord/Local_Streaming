package com.StreamingServer.server.mapper;

import com.StreamingServer.server.dto.MovieDTO;
import com.StreamingServer.server.models.Movies;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    /**
     * Converte uma entidade Movies para MovieDTO
     */
    public MovieDTO toDTO(Movies movie) {
        if (movie == null) return null;
        return MovieDTO.builder()
                .title(movie.getTitle())
                .description(movie.getDescription())
                .coverUrl(movie.getCoverUrl())
                .durationMinutes(movie.getDurationMinutes())
                .releaseYear(movie.getReleaseYear())
                .build();
    }

    /**
     * Converte um MovieDTO para entidade Movies
     */
    public Movies toEntity(MovieDTO dto) {
        if (dto == null) return null;
        Movies movie = new Movies();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setCoverUrl(dto.getCoverUrl());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setReleaseYear(dto.getReleaseYear());
        return movie;
    }

    /**
     * Cria um MovieDTO a partir dos parâmetros de uma requisição multipart
     */
    public MovieDTO fromMultipartRequest(String title, String description, 
                                          String coverUrl, Integer durationMinutes, 
                                          Integer releaseYear) {
        return MovieDTO.builder()
                .title(title)
                .description(description)
                .coverUrl(coverUrl)
                .durationMinutes(durationMinutes)
                .releaseYear(releaseYear)
                .build();
    }

    /**
     * Atualiza uma entidade Movies existente com dados de um MovieDTO
     */
    public void updateEntityFromDTO(Movies movie, MovieDTO dto) {
        if (dto.getTitle() != null) movie.setTitle(dto.getTitle());
        if (dto.getDescription() != null) movie.setDescription(dto.getDescription());
        if (dto.getCoverUrl() != null) movie.setCoverUrl(dto.getCoverUrl());
        if (dto.getDurationMinutes() != null) movie.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getReleaseYear() != null) movie.setReleaseYear(dto.getReleaseYear());
    }
}
