package ru.practicum.ExploreWithMe.Stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
@RequiredArgsConstructor
public class StatsClientApp {
    @Value("${stats-server.url}")
    private String statsServerUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void createHit(EndpointHitDto endpointHitDto) {
        String fullUrl = statsServerUrl + "/hit";
        HttpHeaders headers = new HttpHeaders() {{
            setContentType(MediaType.APPLICATION_JSON);
        }};
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.postForEntity(fullUrl, requestEntity, Object.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, Boolean unique) {

        if (startTime == null) {
            startTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.of(2100, 1, 1, 1, 1);
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(statsServerUrl + "/stats")
                .queryParam("start", startTime.format(FORMATTER))
                .queryParam("end", endTime.format(FORMATTER))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriComponentsBuilder.queryParam("uris", uri);
            }
        }

        ResponseEntity<ViewStatsDto[]> responseEntity = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), ViewStatsDto[].class);
        return List.of(Objects.requireNonNull(responseEntity.getBody()));
    }
}
