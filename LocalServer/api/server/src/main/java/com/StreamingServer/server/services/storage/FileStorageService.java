package com.StreamingServer.server.services.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final String PENDING_DIRECTORY = "src" + File.separator + "pending";

    /**
     * Salva um arquivo na pasta pending e retorna o caminho
     */
    public Path saveToPendingDirectory(MultipartFile file) throws IOException {
        ensureDirectoryExists(Paths.get(PENDING_DIRECTORY));

        String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
        Path pendingPath = Paths.get(PENDING_DIRECTORY, uniqueFilename);

        Files.copy(file.getInputStream(), pendingPath);
        return pendingPath;
    }

    /**
     * Remove um arquivo da pasta pending
     */
    public void deletePendingFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Erro ao remover arquivo da pasta pending: " + path);
        }
    }

    /**
     * Garante que um diretório existe, criando-o se necessário
     */
    public void ensureDirectoryExists(Path path) {
        File directory = path.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Gera um nome de arquivo único usando timestamp
     */
    public String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "video_" + System.currentTimeMillis();
        }
        return System.currentTimeMillis() + "_" + originalFilename;
    }
}
