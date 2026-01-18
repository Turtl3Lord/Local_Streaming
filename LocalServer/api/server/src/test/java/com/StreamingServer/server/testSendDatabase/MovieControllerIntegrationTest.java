package com.StreamingServer.server.testSendDatabase;

import com.StreamingServer.server.dto.MovieDTO;
import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerIntegrationTest {

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



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MoviesRepository moviesRepository;


    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        // Executar migrações do Flyway
        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                )
                .load();
        flyway.migrate();
    }

    @Test
    void shouldCreateMovieWithTesteMp4File() throws Exception {
        // Obter o caminho absoluto do arquivo teste.mp4
        Path videoPath = Paths.get("src", "test", "resources", "video", "origin", "teste.mp4");
        File videoFile = videoPath.toAbsolutePath().toFile();
        
        // Verificar se o arquivo existe
        assertTrue(videoFile.exists(), "Arquivo teste.mp4 deve existir em src/test/resources/video/origin/");
        
        String videoUrl = videoFile.getAbsolutePath();
        
        // Criar MovieDTO com todos os atributos do JSON fornecido
        MovieDTO movieDTO = MovieDTO.builder()
                .title("Filme Exemplo")
                .description("Descrição do filme")
                .coverUrl("https://example.com/capa.jpg")

                .durationMinutes(120)
                .releaseYear(2023)
                .build();

        // Converter DTO para JSON
        String movieJson = objectMapper.writeValueAsString(movieDTO);

        // Enviar requisição POST para o controller
        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Filme Exemplo"))
                .andExpect(jsonPath("$.description").value("Descrição do filme"))
                .andExpect(jsonPath("$.coverUrl").value("https://example.com/capa.jpg"))
                .andExpect(jsonPath("$.durationMinutes").value(120))
                .andExpect(jsonPath("$.releaseYear").value(2023))
                .andExpect(jsonPath("$.videoUrl").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verificar se o filme foi salvo no banco de dados
        Movies savedMovie = moviesRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        assertNotNull(savedMovie, "Filme deve ser salvo no banco de dados");
        assertEquals("Filme Exemplo", savedMovie.getTitle());
        assertEquals("Descrição do filme", savedMovie.getDescription());
        assertEquals("https://example.com/capa.jpg", savedMovie.getCoverUrl());
        assertEquals(120, savedMovie.getDurationMinutes());
        assertEquals(2023, savedMovie.getReleaseYear());
        assertNotNull(savedMovie.getVideoUrl(), "videoUrl não deve ser nulo");
        assertTrue(savedMovie.getVideoUrl().contains("movies/teste/teste.m3u8"), 
                "videoUrl deve conter o caminho relativo do nginx");
        assertNotNull(savedMovie.getId(), "ID deve ser gerado automaticamente");
    }
}
