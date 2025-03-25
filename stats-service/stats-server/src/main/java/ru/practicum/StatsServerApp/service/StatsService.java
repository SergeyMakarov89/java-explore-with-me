package ru.practicum.StatsServerApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsServerApp.model.EndpointHit;
import ru.practicum.StatsServerApp.repository.StatsRepository;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatsService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;

    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit(null, endpointHitDto.getApp(), endpointHitDto.getUri(), endpointHitDto.getIp(), endpointHitDto.getTimestamp());
        statsRepository.save(endpointHit);
    }

    public List<ViewStatsDto> getStats(String startTime, String endTime, List<String> uris, Boolean unique) {
        LocalDateTime formattedStartTime = LocalDateTime.parse(startTime, FORMATTER);
        LocalDateTime formattedEndTime = LocalDateTime.parse(endTime, FORMATTER);

        if (unique) {
            return statsRepository.getUniqueStats(formattedStartTime, formattedEndTime, uris);
        } else {
            return statsRepository.getStats(formattedStartTime, formattedEndTime, uris);
        }
    }
}
