// Java
package com.StreamingServer.server.testSendDatabase;

import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.StreamingServer.server.dto.MovieDTO;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class SendMovieToDatabaseTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        // Ensure Flyway runs migrations in tests
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Test
    void migrationsMustRun() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                )
                .load();

        flyway.migrate();
    }

    @Autowired
    MoviesRepository movieRepository;

    @Test
    void shouldInsertMoviesFromMockDataWithoutNullColumns() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("mockData/movies.json")) {
            assertNotNull(is, "mockData/movies.json must be present in test resources");

            List<MovieDTO> moviesDto = mapper.readValue(is, new TypeReference<List<MovieDTO>>() {});
            assertFalse(moviesDto.isEmpty(), "movies list should not be empty");

            // Map DTOs to entity and save via JPA repository
            List<Movies> saved = new ArrayList<>();
            for (MovieDTO dto : moviesDto) {
                Movies e = new Movies();
                e.setTitle(dto.getTitle());
                e.setDescription(dto.getDescription());
                e.setCoverUrl(dto.getCoverUrl());
                e.setDurationMinutes(dto.getDurationMinutes());
                e.setReleaseYear(dto.getReleaseYear());
                e.setVideoUrl("something");
                saved.add(movieRepository.save(e));
            }

            List<Movies> all = movieRepository.findAll();
            assertEquals(moviesDto.size(), all.size(), "Inserted row count must match movies list size");

            for (Movies m : all) {
                assertNotNull(m.getId(), "id must not be null after save");
                assertNotNull(m.getTitle(), "title must not be null");
                assertNotNull(m.getVideoUrl(), "video_url must not be null");
            }
        }
    }
}
