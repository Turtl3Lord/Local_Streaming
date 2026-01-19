package com.StreamingServer.server.services.storage;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DirectoryService {

    private static final String NGINX_BASE_PATH = "src" + File.separator + "ngix" + 
            File.separator + "midia" + File.separator + "movies";

    /**
     * Cria um diretório temporário para conversão HLS
     */
    public Path createTempConversionDirectory() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempConversionPath = tempDir + File.separator + "hls_conversion_" + System.currentTimeMillis();
        File tempDirFile = new File(tempConversionPath);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }
        return Paths.get(tempConversionPath);
    }

    /**
     * Cria o diretório do filme na pasta do nginx
     */
    public Path createNginxMovieDirectory(String movieName) {
        String movieFolderPath = NGINX_BASE_PATH + File.separator + movieName;
        File movieFolder = new File(movieFolderPath);
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        }
        return Paths.get(movieFolderPath);
    }

    /**
     * Deleta um diretório e todo seu conteúdo recursivamente
     */
    public void deleteRecursively(Path directory) {
        deleteDirectory(directory.toFile());
    }

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
            System.err.println("Erro ao deletar diretório temporário: " + directory.getAbsolutePath());
        }
    }
}
