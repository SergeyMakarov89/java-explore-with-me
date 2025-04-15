package ru.practicum.ExploreWithMe.Stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.Stats.exception.ValidationException;
import ru.practicum.ExploreWithMe.Stats.repository.StatsRepository;
import ru.practicum.ExploreWithMe.Stats.model.EndpointHit;
import ru.practicum.ExploreWithMe.Stats.EndpointHitDto;
import ru.practicum.ExploreWithMe.Stats.ViewStatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

        LocalDateTime formattedStartTime;
        LocalDateTime formattedEndTime;

        if (startTime == null || startTime.isBlank()) {
            formattedStartTime = LocalDateTime.now().minusYears(1);
        } else {
            formattedStartTime = LocalDateTime.parse(URLDecoder.decode(startTime, StandardCharsets.UTF_8), FORMATTER);
        }

        if (endTime == null || endTime.isBlank()) {
            formattedEndTime = LocalDateTime.now();
        } else {
            formattedEndTime = LocalDateTime.parse(URLDecoder.decode(endTime, StandardCharsets.UTF_8), FORMATTER);
        }

        if (formattedStartTime.isAfter(formattedEndTime)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        if (unique != null && unique) {
            return statsRepository.getUniqueStats(formattedStartTime, formattedEndTime, uris);
        } else {
            return statsRepository.getStats(formattedStartTime, formattedEndTime, uris);
        }
    }
}
