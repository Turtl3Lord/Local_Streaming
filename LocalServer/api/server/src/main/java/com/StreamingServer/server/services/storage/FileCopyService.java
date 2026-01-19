package com.StreamingServer.server.services.storage;

import com.StreamingServer.server.interfaces.ICopyFile;
import com.StreamingServer.server.utils.CopyVideo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

@Service
public class FileCopyService {

    private final ICopyFile copyFile;

    public FileCopyService() {
        this.copyFile = new CopyVideo();
    }

    // Construtor para injeção de dependência (testes)
    public FileCopyService(ICopyFile copyFile) {
        this.copyFile = copyFile;
    }

    /**
     * Copia todos os arquivos gerados para o diretório de destino
     */
    public void copyFilesToDirectory(String[] sourceFiles, Path destination) throws IOException {
        for (String sourceFilePath : sourceFiles) {
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.exists()) {
                continue;
            }

            String fileName = sourceFile.getName();
            File destinationFile = new File(destination.toFile(), fileName);

            // Garantir que o diretório pai existe
            destinationFile.getParentFile().mkdirs();

            URI sourceUri = sourceFile.toURI();
            URI destinationUri = destinationFile.toURI();

            try {
                copyFile.copy(sourceUri, destinationUri);
            } catch (Exception e) {
                throw new IOException("Erro ao copiar arquivo " + fileName + ": " + e.getMessage(), e);
            }
        }
    }
}
