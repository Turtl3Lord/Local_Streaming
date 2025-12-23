package com.StreamingServer.server.utils;


import com.StreamingServer.server.interfaces.ICopyFile;
import com.StreamingServer.server.interfaces.ISendVideoToServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CopyVideo implements ICopyFile {

    @Override
    public void copy(URI sourceUri, URI destinationUri) throws IOException {


        if (!"file".equals(sourceUri.getScheme()) ||
                !"file".equals(destinationUri.getScheme())) {
            throw new IllegalArgumentException(
                    "LocalVideoSender aceita apenas URIs com scheme file://"
            );
        }

        Path source = Paths.get(sourceUri);
        Path destination = Paths.get(destinationUri);

        try (InputStream in = Files.newInputStream(source);
             OutputStream out = Files.newOutputStream(destination)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }
}


