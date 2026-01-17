package com.StreamingServer.server.controllers;

import com.StreamingServer.server.dto.MovieDTO;
import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import com.StreamingServer.server.services.MovieProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LocalServerController {

    private final MoviesRepository moviesRepository;
    private final MovieProcessingService movieProcessingService;

    @Autowired
    public LocalServerController(MoviesRepository moviesRepository, MovieProcessingService movieProcessingService) {
        this.moviesRepository = moviesRepository;
        this.movieProcessingService = movieProcessingService;
    }

    // Create a new movie
    @PostMapping("/movies")
    public ResponseEntity<?> createMovie(@RequestBody MovieDTO movieDTO) {
        try {
            // Processar filme completo: conversão, cópia e salvamento no DB
            Movies saved = movieProcessingService.processMovie(movieDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
        } catch (IllegalArgumentException e) {
            // Erro de validação
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro de validação");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IOException e) {
            // Erro na conversão ou cópia de arquivos
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao processar vídeo");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            // Erro genérico
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro interno do servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Update an existing movie
    @PutMapping("/movies/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @RequestBody MovieDTO movieDTO) {
        Optional<Movies> existing = moviesRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Movies movie = existing.get();
        // update fields
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setCoverUrl(movieDTO.getCoverUrl());
        movie.setVideoUrl(movieDTO.getVideoUrl());
        movie.setDurationMinutes(movieDTO.getDurationMinutes());
        movie.setReleaseYear(movieDTO.getReleaseYear());

        Movies saved = moviesRepository.save(movie);
        return ResponseEntity.ok(toDTO(saved));
    }

    // Delete a movie
    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        Optional<Movies> existing = moviesRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        moviesRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Placeholder for sending the video to the requester. Implementation will be provided later.
    @PostMapping("/movies/{id}/send-video")
    public ResponseEntity<String> sendVideoToRequester(@PathVariable Long id) {
        Optional<Movies> existing = moviesRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        // TODO: Implement actual video sending logic
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented yet");
    }

    // Helpers to convert between DTO and entity
    private Movies toEntity(MovieDTO dto) {
        if (dto == null) return null;
        Movies movie = new Movies();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setCoverUrl(dto.getCoverUrl());
        movie.setVideoUrl(dto.getVideoUrl());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setReleaseYear(dto.getReleaseYear());
        return movie;
    }

    private MovieDTO toDTO(Movies movie) {
        if (movie == null) return null;
        return MovieDTO.builder()
                .title(movie.getTitle())
                .description(movie.getDescription())
                .coverUrl(movie.getCoverUrl())
                .videoUrl(movie.getVideoUrl())
                .durationMinutes(movie.getDurationMinutes())
                .releaseYear(movie.getReleaseYear())
                .build();
    }
}

