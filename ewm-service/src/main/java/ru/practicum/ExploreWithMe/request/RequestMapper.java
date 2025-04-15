package ru.practicum.ExploreWithMe.request;

import lombok.experimental.UtilityClass;
import ru.practicum.ExploreWithMe.request.dto.ParticipationRequestDto;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getRequestor().getId());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setStatus(request.getStatus());

        return participationRequestDto;
    }
}
