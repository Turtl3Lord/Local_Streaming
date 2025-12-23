package com.StreamingServer.server.testVideoSender;

import com.StreamingServer.server.utils.CopyVideo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
class CopyVideoTest {

    private final CopyVideo service = new CopyVideo();

    @Test
    void shouldCopyVideoDirectlyToDestinyFolder() throws Exception {
        // Arrange
        URI source = getSourceVideoFile();
        URI destination = prepareDestinationFile();


        // Act
        service.copy(source, destination);

        // Assert
        assertVideoWasCopiedCorrectly(source, destination);

        // Cleanup
        cleanupDestinationFile(destination);
    }

    private URI getSourceVideoFile() throws Exception {
        var resource = Objects.requireNonNull(
                getClass()
                        .getClassLoader()
                        .getResource("video/origin/teste.mp4"),
                "Source video not found in classpath"
        );

        return resource.toURI();
    }

    private URI prepareDestinationFile() throws IOException {
        Path destinyDir = Path.of("src/test/videosDestiny");
        Files.createDirectories(destinyDir);

        Path destinationPath = destinyDir.resolve("teste.mp4");
        return destinationPath.toUri();
    }


    private void assertVideoWasCopiedCorrectly(URI source, URI destination) throws IOException {

        Path sourcePath = Path.of(source);
        Path destinationPath = Path.of(destination);

        assertTrue(Files.exists(destinationPath), "Destination file should exist");

        assertEquals(
                Files.size(sourcePath),
                Files.size(destinationPath),
                "File sizes should match"
        );

        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        byte[] destinationBytes = Files.readAllBytes(destinationPath);

        assertArrayEquals(
                sourceBytes,
                destinationBytes,
                "File contents should be identical"
        );
    }


    private void cleanupDestinationFile(URI destination) throws IOException {
        Files.deleteIfExists(Path.of(destination));
    }
}


