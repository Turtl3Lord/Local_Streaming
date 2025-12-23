package com.StreamingServer.server.utils;

import java.nio.file.Path;

public class PathUtils {

    static void validatePaths(String inputPath, String outputPath){
        if (inputPath == null || inputPath.isEmpty()) {
            throw new IllegalArgumentException("Input path cannot be null or empty");
        }
        if (outputPath == null || outputPath.isEmpty()) {
            throw new IllegalArgumentException("Output path cannot be null or empty");
        }
    };

   static  String getOutputFileName(String inputPath) {
        Path inputFilePath = Path.of(inputPath);
        String fileName = inputFilePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}
