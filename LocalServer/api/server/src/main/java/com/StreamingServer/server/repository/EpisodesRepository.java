package com.StreamingServer.server.repository;

import com.StreamingServer.server.models.Episodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodesRepository extends JpaRepository<Episodes, Long> {
    List<Episodes> findBySeasonId(Long seasonId);
    Optional<Episodes> findBySeasonIdAndEpisodeNumber(Long seasonId, Integer episodeNumber);

}
