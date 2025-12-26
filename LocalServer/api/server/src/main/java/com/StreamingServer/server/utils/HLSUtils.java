package com.StreamingServer.server.utils;

import com.StreamingServer.server.enums.OutputType;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HLSUtils {



   public static void configureCodecs(FFmpegFrameRecorder recorder,
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

    private static int getAvCodec(String codecName) {
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

   public static String[] collectSegments(String outputPath, String baseName) {
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
}
