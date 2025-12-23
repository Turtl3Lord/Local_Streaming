package com.StreamingServer.server.enums;

public enum OutputType {
    VIDEO_MP4("mp4", "libx264", "aac"),
    VIDEO_WEBM("webm", "vp9", "libopus"),
    AUDIO_AAC("aac", null, "aac"),
    AUDIO_MP3("mp3", null, "libmp3lame"),
    AUDIO_OPUS("opus", null, "libopus");

    private final String format;
    private final String videoCodec;
    private final String audioCodec;

    public String getFormat() {
        return format;
    }
    public String getVideoCodec() {
        return videoCodec;
    }
    public String getAudioCodec() {
        return audioCodec;
    }
    OutputType(String mp4, String libx264, String aac) {
        this.format = mp4;
        this.videoCodec = libx264;
        this.audioCodec = aac;
    }
}