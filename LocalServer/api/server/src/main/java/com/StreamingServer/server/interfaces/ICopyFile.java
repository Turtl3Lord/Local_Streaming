package com.StreamingServer.server.interfaces;

import java.net.URI;

public interface ICopyFile {
    void copy(URI sourcePath, URI destinationPath) throws Exception;
}
