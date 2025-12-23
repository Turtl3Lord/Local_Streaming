package com.StreamingServer.server.utils;

import com.StreamingServer.server.enums.OutputType;
import com.StreamingServer.server.interfaces.IVideoConverter;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HLSConverter implements IVideoConverter {
    // Default values for HLS parameters
    private static final int AUDIO_BIT_RATE = 128000;
    private static final int AUDIO_CHANEL = 2;
    private static final int AUDIO_SAMPLE_RATE = 48000;
    private static final int DEFAULT_SEGMENT_DURATION = 10;
    private static final int DEFAULT_PLAYLIST_SIZE =100;



    public String[] convert(String source, String destination)
            throws IOException {

        String outputFileName = getOutputFileName(source);
        OutputType outputType = OutputType.VIDEO_MP4; // default format

        validatePaths(source, destination);
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

//  define keyframes interval to match segment duration
                double fps = grabber.getFrameRate();
                if (fps <= 0) fps = 60; // fallback, não default
                int gop = (int) fps * DEFAULT_SEGMENT_DURATION;
                recorder.setFrameRate(fps);
                recorder.setVideoOption("g", String.valueOf(gop));
                recorder.setVideoOption("keyint_min", String.valueOf(gop));
                recorder.setVideoOption("sc_threshold", "0");

                // Set codecs (use parameters if provided, otherwise fallback to OutputType)
                configureCodecs(recorder, outputType, null, null);

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

        return collectSegments(destination, outputFileName);
    }

    private void configureCodecs(FFmpegFrameRecorder recorder,
                                 OutputType outputType,
                                 String videoCodecParam,
                                 String audioCodecParam) {

        // Determine final codecs to use
        String videoCodec = (videoCodecParam != null) ? videoCodecParam : outputType.getVideoCodec();
        String audioCodec = (audioCodecParam != null) ? audioCodecParam : outputType.getAudioCodec();

        // Set video codec if applicable
        if (videoCodec != null) {
            int codecId = getAvCodec(videoCodec);
            if (codecId == avcodec.AV_CODEC_ID_NONE) {
                throw new IllegalArgumentException("Unsupported video codec: " + videoCodec);
            }
            recorder.setVideoCodec(codecId);
        }

        // Set audio codec
        if (audioCodec != null) {
            int codecId = getAvCodec(audioCodec);
            if (codecId == avcodec.AV_CODEC_ID_NONE) {
                throw new IllegalArgumentException("Unsupported audio codec: " + audioCodec);
            }
            recorder.setAudioCodec(codecId);
        }
    }

    private int getAvCodec(String codecName) {
        switch (codecName.toLowerCase()) {
            case "libx264":
            case "h264":
                return avcodec.AV_CODEC_ID_H264;
            case "libx265":
            case "h265":
                return avcodec.AV_CODEC_ID_HEVC;
            case "aac":
                return avcodec.AV_CODEC_ID_AAC;
            case "libmp3lame":
            case "mp3":
                return avcodec.AV_CODEC_ID_MP3;
            case "vp9":
                return avcodec.AV_CODEC_ID_VP9;
            default:
                return avcodec.AV_CODEC_ID_NONE;
        }
    }

    private String[] collectSegments(String outputPath, String baseName) {
        File dir = new File(outputPath);
        List<String> segments = new ArrayList<>();
        segments.add(outputPath + File.separator + baseName + ".m3u8");

        File[] tsFiles = dir.listFiles((d, name) -> name.startsWith(baseName) && name.endsWith(".ts"));

        if (tsFiles != null) {
            for (File ts : tsFiles) {
                segments.add(ts.getAbsolutePath());
            }
        }
        return segments.toArray(new String[0]);
    }

    private void validatePaths(String inputPath, String outputPath) throws IOException {
        if (!Files.exists(Path.of(inputPath))) {
            throw new IOException("Input file not found: " + inputPath);
        }
        if (!Files.isDirectory(Path.of(outputPath))) {
            throw new IOException("Output path is not a directory: " + outputPath);
        }
    }

    String getOutputFileName(String inputPath) {
        Path inputFilePath = Path.of(inputPath);
        String fileName = inputFilePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }


}