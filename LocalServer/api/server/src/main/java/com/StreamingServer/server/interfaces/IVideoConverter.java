package com.StreamingServer.server.interfaces;

import java.io.IOException;

public interface IVideoConverter {

    String[] convert(String source, String destination) throws IOException;
}
