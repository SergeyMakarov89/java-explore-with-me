package ru.practicum.ExploreWithMe.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ExploreWithMe.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ExploreWithMe.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelParticipationRequest(userId, requestId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getAllUserParticipationRequests(@PathVariable Long userId) {
        return requestService.getAllUserParticipationRequests(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllEventParticipationRequests(@PathVariable Long eventId, @PathVariable Long userId) {
        return requestService.getAllEventParticipationRequests(eventId, userId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequests(@PathVariable Long eventId, @PathVariable Long userId, @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateParticipationRequests(eventId, userId, eventRequestStatusUpdateRequest);
    }
}
