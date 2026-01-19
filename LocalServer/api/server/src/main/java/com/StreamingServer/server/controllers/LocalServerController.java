package com.StreamingServer.server.controllers;

import com.StreamingServer.server.dto.MovieDTO;
import com.StreamingServer.server.mapper.MovieMapper;
import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import com.StreamingServer.server.services.MovieProcessingService;
import com.StreamingServer.server.services.storage.FileStorageService;
import com.StreamingServer.server.services.validation.FileValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LocalServerController {

    private final MoviesRepository moviesRepository;
    private final MovieProcessingService movieProcessingService;
    private final FileStorageService fileStorageService;
    private final FileValidationService fileValidationService;
    private final MovieMapper movieMapper;

    @Autowired
    public LocalServerController(
            MoviesRepository moviesRepository,
            MovieProcessingService movieProcessingService,
            FileStorageService fileStorageService,
            FileValidationService fileValidationService,
            MovieMapper movieMapper) {
        this.moviesRepository = moviesRepository;
        this.movieProcessingService = movieProcessingService;
        this.fileStorageService = fileStorageService;
        this.fileValidationService = fileValidationService;
        this.movieMapper = movieMapper;
    }

    @PostMapping(value = "/movies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieDTO> createMovie(
            @RequestPart("file") MultipartFile file,
            @RequestPart("title") String title,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "coverUrl", required = false) String coverUrl,
            @RequestPart(value = "durationMinutes", required = false) Integer durationMinutes,
            @RequestPart(value = "releaseYear", required = false) Integer releaseYear) throws IOException {

        fileValidationService.validateVideoFile(file);

        Path pendingPath = fileStorageService.saveToPendingDirectory(file);

        try {
            MovieDTO movieDTO = movieMapper.fromMultipartRequest(
                    title, description, coverUrl, durationMinutes, releaseYear
            );

            Movies saved = movieProcessingService.processMovie(movieDTO, pendingPath.toString());

            return ResponseEntity.status(HttpStatus.CREATED).body(movieMapper.toDTO(saved));
        } finally {
            fileStorageService.deletePendingFile(pendingPath);
        }
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @RequestBody MovieDTO movieDTO) {
        Optional<Movies> existing = moviesRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Movies movie = existing.get();
        movieMapper.updateEntityFromDTO(movie, movieDTO);
        Movies saved = moviesRepository.save(movie);
        
        return ResponseEntity.ok(movieMapper.toDTO(saved));
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        Optional<Movies> existing = moviesRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        moviesRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/movies/{id}/send-video")
    public ResponseEntity<String> sendVideoToRequester(@PathVariable Long id) {
        Optional<Movies> existing = moviesRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented yet");
    }
}
