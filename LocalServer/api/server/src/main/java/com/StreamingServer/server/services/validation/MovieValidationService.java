package com.StreamingServer.server.services.validation;

import com.StreamingServer.server.dto.MovieDTO;
import org.springframework.stereotype.Service;

@Service
public class MovieValidationService {

    /**
     * Valida todos os campos do MovieDTO
     */
    public void validate(MovieDTO movieDTO) {
        if (movieDTO == null) {
            throw new IllegalArgumentException("MovieDTO não pode ser nulo");
        }
        validateTitle(movieDTO.getTitle());
        validateDuration(movieDTO.getDurationMinutes());
        validateReleaseYear(movieDTO.getReleaseYear());
    }

    /**
     * Valida o título do filme
     */
    public void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Título do filme é obrigatório");
        }
    }

    /**
     * Valida a duração do filme
     */
    public void validateDuration(Integer duration) {
        if (duration != null && duration < 0) {
            throw new IllegalArgumentException("Duração do filme deve ser um valor positivo");
        }
    }

    /**
     * Valida o ano de lançamento
     */
    public void validateReleaseYear(Integer year) {
        if (year != null && year < 1888) {
            throw new IllegalArgumentException("Ano de lançamento inválido");
        }
    }
}
