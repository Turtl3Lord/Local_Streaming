package com.StreamingServer.server.controllers;

import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseInformationsController {

    MoviesRepository moviesRepository;

    @Autowired
    public DatabaseInformationsController(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    @GetMapping("/movies")
    public ResponseEntity<List<Movies>> getAllMovies() {
        // Implementation to retrieve all movies from the database
        List<Movies> movies = moviesRepository.findAll();
        return ResponseEntity.ok(movies);
    }
}
