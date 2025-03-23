package ru.practicum.StatsClientApp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsClientApp {
    @Value("$stats-server.url")
    private String statsServerUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public void createHit(EndpointHitDto endpointHitDto) {
        String fullUrl = statsServerUrl + "/hit";
        HttpHeaders headers = new HttpHeaders() {{
            setContentType(MediaType.APPLICATION_JSON);
        }};
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.postForEntity(fullUrl, requestEntity, Object.class);
    }

    public ResponseEntity<Object> getStats(LocalDateTime startTime, LocalDateTime endTime, List<String> uris, Boolean unique) {
        HashMap<String, Object> prms = new HashMap<>(Map.of("start", startTime, "end", endTime, "uris", uris, "unique", unique));

        ResponseEntity<Object> responseEntity = restTemplate.getForEntity(statsServerUrl + "/stats", Object.class, prms);
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(responseEntity.getStatusCode());

        if (responseEntity.hasBody()) {
            return responseBuilder.body(responseEntity.getBody());
        } else {
            return responseBuilder.build();
        }
    }
}
