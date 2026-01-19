package com.StreamingServer.server.services;

import com.StreamingServer.server.dto.MovieDTO;
import com.StreamingServer.server.exception.VideoProcessingException;
import com.StreamingServer.server.interfaces.IVideoConverter;
import com.StreamingServer.server.mapper.MovieMapper;
import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import com.StreamingServer.server.services.converter.HLSConverter;
import com.StreamingServer.server.services.storage.DirectoryService;
import com.StreamingServer.server.services.storage.FileCopyService;
import com.StreamingServer.server.services.validation.FileValidationService;
import com.StreamingServer.server.services.validation.MovieValidationService;
import com.StreamingServer.server.utils.MediaUrlBuilder;
import com.StreamingServer.server.utils.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class MovieProcessingService {

    private final MoviesRepository moviesRepository;
    private final IVideoConverter hlsConverter;
    private final DirectoryService directoryService;
    private final FileCopyService fileCopyService;
    private final FileValidationService fileValidationService;
    private final MovieValidationService movieValidationService;
    private final MediaUrlBuilder urlBuilder;
    private final MovieMapper movieMapper;

    @Autowired
    public MovieProcessingService(
            MoviesRepository moviesRepository,
            DirectoryService directoryService,
            FileCopyService fileCopyService,
            FileValidationService fileValidationService,
            MovieValidationService movieValidationService,
            MediaUrlBuilder urlBuilder,
            MovieMapper movieMapper) {
        this.moviesRepository = moviesRepository;
        this.hlsConverter = new HLSConverter(); // Manter instanciação até HLSConverter ser um @Component
        this.directoryService = directoryService;
        this.fileCopyService = fileCopyService;
        this.fileValidationService = fileValidationService;
        this.movieValidationService = movieValidationService;
        this.urlBuilder = urlBuilder;
        this.movieMapper = movieMapper;
    }

    /**
     * Processa um filme completo: converte para HLS, copia para nginx e salva no banco
     */
    @Transactional
    public Movies processMovie(MovieDTO movieDTO, String videoFilePath) throws IOException {
        // Validações delegadas
        movieValidationService.validate(movieDTO);
        fileValidationService.validateVideoFileExists(videoFilePath);

        String movieName = PathUtils.getOutputFileName(videoFilePath);
        Path tempDir = directoryService.createTempConversionDirectory();

        try {
            // Conversão
            String[] generatedFiles = hlsConverter.convert(videoFilePath, tempDir.toString());

            if (generatedFiles == null || generatedFiles.length == 0) {
                throw new VideoProcessingException("Nenhum arquivo foi gerado na conversão HLS");
            }

            // Cópia para nginx
            Path nginxDir = directoryService.createNginxMovieDirectory(movieName);
            fileCopyService.copyFilesToDirectory(generatedFiles, nginxDir);

            // Conversão e persistência
            Movies movieEntity = movieMapper.toEntity(movieDTO);
            movieEntity.setVideoUrl(urlBuilder.buildMovieUrl(movieName));

            return moviesRepository.save(movieEntity);
        } finally {
            directoryService.deleteRecursively(tempDir);
        }
    }
}
