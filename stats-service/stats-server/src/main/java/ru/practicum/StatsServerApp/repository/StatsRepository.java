package ru.practicum.StatsServerApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.StatsServerApp.model.EndpointHit;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) FROM EndpointHit AS h WHERE h.timestamp BETWEEN :startTime AND :endTime AND (:uris IS NULL OR h.uri IN (:uris)) GROUP BY h.app, h.uri ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> getUniqueStats(@Param("startTime") LocalDateTime start, @Param("endTime") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(h.app, h.uri, COUNT(h.ip)) FROM EndpointHit AS h WHERE h.timestamp BETWEEN :startTime AND :endTime AND (:uris IS NULL OR h.uri IN (:uris)) GROUP BY h.app, h.uri ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> getStats(@Param("startTime") LocalDateTime start, @Param("endTime") LocalDateTime end, @Param("uris") List<String> uris);
}
