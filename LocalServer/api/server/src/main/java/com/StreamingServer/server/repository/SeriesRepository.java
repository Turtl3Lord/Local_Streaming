package com.StreamingServer.server.repository;

import com.StreamingServer.server.models.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findById(Long id);
    List<Series> findAll();

}