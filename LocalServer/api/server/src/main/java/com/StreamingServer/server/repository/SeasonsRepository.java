package com.StreamingServer.server.repository;

import com.StreamingServer.server.models.Seasons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonsRepository extends JpaRepository<Seasons, Long> {
    List<Seasons> findBySeriesId(Long seriesId);
}
