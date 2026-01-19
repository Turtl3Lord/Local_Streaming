package com.StreamingServer.server.utils;

import org.springframework.stereotype.Component;

@Component
public class MediaUrlBuilder {

    /**
     * Constrói a URL relativa do filme para o nginx
     */
    public String buildMovieUrl(String movieName) {
        return "movies/" + movieName + "/" + movieName + ".m3u8";
    }

    /**
     * Constrói a URL relativa de um episódio para o nginx
     */
    public String buildEpisodeUrl(String seriesName, int seasonNumber, int episodeNumber) {
        return "series/" + seriesName + "/season" + seasonNumber + "/episode" + episodeNumber + ".m3u8";
    }
}
