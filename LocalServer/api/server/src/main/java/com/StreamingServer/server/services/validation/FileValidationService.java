package com.StreamingServer.server.services.validation;

import com.StreamingServer.server.exception.FileValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class FileValidationService {

    private static final List<String> ALLOWED_VIDEO_TYPES = List.of(
            "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo",
            "video/x-matroska", "video/webm"
    );

    /**
     * Valida se o arquivo de vídeo é válido (não nulo, não vazio e tipo correto)
     */
    public void validateVideoFile(MultipartFile file) {
        validateFileNotEmpty(file);
        validateContentType(file);
    }

    /**
     * Valida se o arquivo não está vazio
     */
    public void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Arquivo de vídeo é obrigatório");
        }
    }

    /**
     * Valida se o content type é de um vídeo
     */
    public void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new FileValidationException("O arquivo deve ser um vídeo");
        }
    }

    /**
     * Valida se um arquivo de vídeo existe no caminho especificado
     */
    public void validateVideoFileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new FileValidationException("Caminho do arquivo de vídeo é obrigatório");
        }
        
        File sourceFile = new File(filePath);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new FileValidationException("Arquivo de vídeo não encontrado: " + filePath);
        }
    }
}
