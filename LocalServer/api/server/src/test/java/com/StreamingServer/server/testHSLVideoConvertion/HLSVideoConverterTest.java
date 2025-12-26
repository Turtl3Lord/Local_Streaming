package com.StreamingServer.server.testHSLVideoConvertion;

import com.StreamingServer.server.enums.OutputType;
import com.StreamingServer.server.interfaces.IVideoConverter;
import com.StreamingServer.server.services.converter.HLSConverter;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HLSVideoConverterTest {

    private final IVideoConverter hlsConverter = new HLSConverter();

    private final String sourcePath = "src/test/resources/video/origin/teste.mp4";
    private final String destinationPath = "src/test/videosDestiny";
    private final String outputName = "teste";
    private final OutputType outputType = OutputType.VIDEO_MP4; // codec, não formato

    @Test
    void shouldConvertVideoToHLSFormat() throws Exception {
        // Act
        String[] generatedFiles = hlsConverter.convert(
                sourcePath,
                destinationPath
        );

        // Assert
        assertNotNull(generatedFiles);
        assertTrue(generatedFiles.length > 1, "HLS should generate playlist + segments");

        Path playlist = Path.of(destinationPath, outputName + ".m3u8");
        assertTrue(Files.exists(playlist), "Playlist (.m3u8) should exist");

        boolean hasTsSegment = Files.list(Path.of(destinationPath))
                .anyMatch(p -> p.getFileName().toString().endsWith(".ts"));

        assertTrue(hasTsSegment, "At least one .ts segment should be generated");
    }
}
