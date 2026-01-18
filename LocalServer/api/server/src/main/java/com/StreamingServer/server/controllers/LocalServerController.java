package com.StreamingServer.server.controllers;

import com.StreamingServer.server.dto.MovieDTO;
import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import com.StreamingServer.server.services.MovieProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LocalServerController {

    private final MoviesRepository moviesRepository;
    private final MovieProcessingService movieProcessingService;

    @Autowired
    public LocalServerController(MoviesRepository moviesRepository, MovieProcessingService movieProcessingService) {
        this.moviesRepository = moviesRepository;
        this.movieProcessingService = movieProcessingService;
    }

    // Create a new movie
    @PostMapping(value = "/movies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMovie(
            @RequestPart("file") MultipartFile file,
            @RequestPart("title") String title,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "coverUrl", required = false) String coverUrl,
            @RequestPart(value = "durationMinutes", required = false) Integer durationMinutes,
            @RequestPart(value = "releaseYear", required = false) Integer releaseYear) {
        try {
            // Validar se o arquivo foi enviado
            if (file == null || file.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Erro de validação");
                errorResponse.put("message", "Arquivo de vídeo é obrigatório");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validar se é um arquivo de vídeo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Erro de validação");
                errorResponse.put("message", "O arquivo deve ser um vídeo");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Criar pasta pending se não existir
            String pendingDir = "src" + File.separator + "pending";
            File pendingDirectory = new File(pendingDir);
            if (!pendingDirectory.exists()) {
                pendingDirectory.mkdirs();
            }

            // Gerar nome único para o arquivo (timestamp + nome original)
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                originalFilename = "video_" + System.currentTimeMillis();
            }
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            String pendingFilePath = pendingDir + File.separator + uniqueFilename;
            Path pendingPath = Paths.get(pendingFilePath);

            // Salvar arquivo na pasta pending
            Files.copy(file.getInputStream(), pendingPath);

            // Criar MovieDTO com os dados recebidos
            MovieDTO movieDTO = MovieDTO.builder()
                    .title(title)
                    .description(description)
                    .coverUrl(coverUrl)
                    .durationMinutes(durationMinutes)
                    .releaseYear(releaseYear)
                    .build();

            // Processar filme completo: conversão, cópia e salvamento no DB
            // Passar o caminho relativo do arquivo salvo
            Movies saved = movieProcessingService.processMovie(movieDTO, pendingFilePath);

            // Remover arquivo da pasta pending após processamento bem-sucedido
            try {
                Files.deleteIfExists(pendingPath);
            } catch (IOException e) {
                // Log do erro, mas não interrompe o fluxo
                System.err.println("Erro ao remover arquivo da pasta pending: " + pendingFilePath);
            }

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
                .durationMinutes(movie.getDurationMinutes())
                .releaseYear(movie.getReleaseYear())
                .build();
    }
}

