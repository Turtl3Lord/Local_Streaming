package com.StreamingServer.server.utils;

import com.StreamingServer.server.enums.OutputType;
import com.StreamingServer.server.interfaces.IVideoConverter;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HLSConverter implements IVideoConverter {
    // Default values for HLS parameters
    private static final int AUDIO_BIT_RATE = 128000;
    private static final int AUDIO_CHANEL = 2;
    private static final int AUDIO_SAMPLE_RATE = 44100;
    private static final int DEFAULT_SEGMENT_DURATION = 10;
    private static final int DEFAULT_PLAYLIST_SIZE =100;

    @Override
    public String[] convert(String source, String destination)
            throws IOException {

        String outputFileName = PathUtils.getOutputFileName(source);
        OutputType outputType = OutputType.VIDEO_MP4; // default format

        PathUtils.validatePaths(source, destination);
        String baseName = destination + File.separator + outputFileName;
        String m3u8Path = baseName + ".m3u8";
        String segmentPattern = baseName + "_%03d.ts";

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(source)) {
            grabber.start();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                    m3u8Path,
                    grabber.getImageWidth(),
                    grabber.getImageHeight(),
                    grabber.getAudioChannels())) {

                // Configure HLS parameters
                recorder.setFormat("hls");
                recorder.setOption("hls_time", String.valueOf(DEFAULT_SEGMENT_DURATION));
                recorder.setOption("hls_list_size", String.valueOf(DEFAULT_PLAYLIST_SIZE));
                recorder.setOption("hls_segment_filename", segmentPattern);
                recorder.setVideoOption("preset", "slow");   // melhor compressão
                recorder.setVideoOption("profile:v", "high");

//  define keyframes interval to match segment duration
                double fps = grabber.getFrameRate();
                if (fps <= 0) fps = 60; // fallback, não default
                int gop = (int) fps * DEFAULT_SEGMENT_DURATION;
                recorder.setFrameRate(fps);
                recorder.setVideoOption("g", String.valueOf(gop));
                recorder.setVideoOption("keyint_min", String.valueOf(gop));
                recorder.setVideoOption("sc_threshold", "0");

                // Set codecs (use parameters if provided, otherwise fallback to OutputType)
                HLSUtils.configureCodecs(recorder, outputType, null, null);

                recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                recorder.setAudioChannels(AUDIO_CHANEL);        // nunca confie no grabber
                recorder.setSampleRate(AUDIO_SAMPLE_RATE);       // padrão HLS
                recorder.setAudioBitrate(AUDIO_BIT_RATE);

                recorder.start();

                while (true) {
                    org.bytedeco.javacv.Frame frame = grabber.grab();
                    if (frame == null)
                        break;
                    recorder.record(frame);
                }
            }
        }

        return HLSUtils.collectSegments(destination, outputFileName);
    }




}