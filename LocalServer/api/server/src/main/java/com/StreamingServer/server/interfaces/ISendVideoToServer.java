package com.StreamingServer.server.interfaces;

import java.io.IOException;
import java.net.URI;

public interface ISendVideoToServer {
    void send(URI source, URI destination) throws IOException;
}
