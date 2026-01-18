package com.StreamingServer.server.services;

import com.StreamingServer.server.dto.MovieDTO;
import com.StreamingServer.server.models.Movies;
import com.StreamingServer.server.repository.MoviesRepository;
import com.StreamingServer.server.services.converter.HLSConverter;
import com.StreamingServer.server.utils.CopyVideo;
import com.StreamingServer.server.utils.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Service
public class MovieProcessingService {

    private final MoviesRepository moviesRepository;
    private final HLSConverter hlsConverter;
    private final CopyVideo copyVideo;

    @Autowired
    public MovieProcessingService(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
        this.hlsConverter = new HLSConverter();
        this.copyVideo = new CopyVideo();
    }

    /**
     * Processa um filme completo: converte para HLS, copia para nginx e salva no banco
     *
     * @param movieDTO DTO com todos os dados do filme
     * @param videoFilePath Caminho relativo do arquivo de vídeo na pasta pending
     * @return Entidade Movies salva no banco
     * @throws IOException se houver erro na conversão ou cópia de arquivos
     * @throws IllegalArgumentException se os dados estiverem inválidos
     */
    @Transactional
    public Movies processMovie(MovieDTO movieDTO, String videoFilePath) throws IOException {
        // Validação dos dados de entrada
        validateMovieDTO(movieDTO);

        // Validar se o caminho do arquivo foi fornecido
        if (videoFilePath == null || videoFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho do arquivo de vídeo é obrigatório");
        }

        // Extrair nome do arquivo original (sem extensão) do caminho relativo
        String movieName = PathUtils.getOutputFileName(videoFilePath);

        // Validar se o arquivo de origem existe
        File sourceFile = new File(videoFilePath);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new IllegalArgumentException("Arquivo de vídeo não encontrado: " + videoFilePath);
        }

        // Criar pasta temporária para conversão
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempConversionPath = tempDir + File.separator + "hls_conversion_" + System.currentTimeMillis();
        File tempDirFile = new File(tempConversionPath);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }

        try {
            // Converter vídeo para HLS usando o caminho relativo
            String[] generatedFiles = hlsConverter.convert(videoFilePath, tempConversionPath);

            if (generatedFiles == null || generatedFiles.length == 0) {
                throw new IOException("Nenhum arquivo foi gerado na conversão HLS");
            }

            // Criar pasta de destino no nginx
            String nginxBasePath = "src" + File.separator + "ngix" + File.separator + "midia" + File.separator + "movies";
            String movieFolderPath = nginxBasePath + File.separator + movieName;
            File movieFolder = new File(movieFolderPath);
            if (!movieFolder.exists()) {
                movieFolder.mkdirs();
            }

            // Copiar todos os arquivos gerados para a pasta do nginx
            for (String generatedFile : generatedFiles) {
                File sourceFileToCopy = new File(generatedFile);
                if (!sourceFileToCopy.exists()) {
                    continue; // Pula arquivos que não existem
                }

                String fileName = sourceFileToCopy.getName();
                String destinationPath = movieFolderPath + File.separator + fileName;
                File destinationFile = new File(destinationPath);

                // Garantir que o diretório pai existe
                destinationFile.getParentFile().mkdirs();

                // Copiar arquivo
                URI sourceUri = sourceFileToCopy.toURI();
                URI destinationUri = destinationFile.toURI();
                try {
                    copyVideo.copy(sourceUri, destinationUri);
                } catch (Exception e) {
                    throw new IOException("Erro ao copiar arquivo " + fileName + ": " + e.getMessage(), e);
                }
            }

            // Converter DTO para Entity e atualizar videoUrl com caminho relativo do nginx
            Movies movieEntity = convertDTOToEntity(movieDTO, movieName);

            // Salvar no banco de dados
            return moviesRepository.save(movieEntity);

        } finally {
            // Limpar pasta temporária
            deleteDirectory(tempDirFile);
        }
    }

    /**
     * Converte MovieDTO para Movies (entidade)
     * Atualiza o videoUrl para o caminho relativo do nginx
     *
     * @param movieDTO DTO com todos os dados do filme
     * @param movieName Nome do filme (sem extensão) usado para criar a pasta
     * @return Entidade Movies pronta para ser salva
     */
    private Movies convertDTOToEntity(MovieDTO movieDTO, String movieName) {
        Movies movie = new Movies();
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setCoverUrl(movieDTO.getCoverUrl());
        movie.setDurationMinutes(movieDTO.getDurationMinutes());
        movie.setReleaseYear(movieDTO.getReleaseYear());

        // Atualizar videoUrl com caminho relativo do nginx
        // Formato: movies/[nome-do-filme]/[nome-do-filme].m3u8
        String relativeVideoUrl = "movies/" + movieName + "/" + movieName + ".m3u8";
        movie.setVideoUrl(relativeVideoUrl);

        return movie;
    }

    /**
     * Valida os dados do MovieDTO
     *
     * @param movieDTO DTO a ser validado
     * @throws IllegalArgumentException se os dados estiverem inválidos
     */
    private void validateMovieDTO(MovieDTO movieDTO) {
        if (movieDTO == null) {
            throw new IllegalArgumentException("MovieDTO não pode ser nulo");
        }
        if (movieDTO.getTitle() == null || movieDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Título do filme é obrigatório");
        }
        if (movieDTO.getDurationMinutes() != null && movieDTO.getDurationMinutes() < 0) {
            throw new IllegalArgumentException("Duração do filme deve ser um valor positivo");
        }
        if (movieDTO.getReleaseYear() != null && movieDTO.getReleaseYear() < 1888) {
            throw new IllegalArgumentException("Ano de lançamento inválido");
        }
    }

    /**
     * Deleta um diretório e todo seu conteúdo recursivamente
     *
     * @param directory Diretório a ser deletado
     */
    private void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }

        try {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        deleteDirectory(file);
                    }
                }
            }
            directory.delete();
        } catch (Exception e) {
            // Log do erro, mas não interrompe o fluxo
            System.err.println("Erro ao deletar diretório temporário: " + directory.getAbsolutePath());
        }
    }
}
