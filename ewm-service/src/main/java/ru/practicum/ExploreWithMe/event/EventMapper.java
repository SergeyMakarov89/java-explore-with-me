package ru.practicum.ExploreWithMe.event;

import lombok.experimental.UtilityClass;
import ru.practicum.ExploreWithMe.category.CategoryMapper;
import ru.practicum.ExploreWithMe.event.dto.EventFullDto;
import ru.practicum.ExploreWithMe.event.dto.EventShortDto;
import ru.practicum.ExploreWithMe.event.dto.NewEventDto;
import ru.practicum.ExploreWithMe.location.LocationMapper;
import ru.practicum.ExploreWithMe.user.UserMapper;

@UtilityClass
public class EventMapper {
    public EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setViews(event.getViews());

        return eventShortDto;
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setLocation(LocationMapper.toLocationDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setState(event.getState());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }

    public EventFullDto toEventFullDto(Event event, Long views, Long confirmedRequests) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setConfirmedRequests(confirmedRequests);
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setLocation(LocationMapper.toLocationDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setState(event.getState());
        eventFullDto.setViews(views);

        return eventFullDto;
    }

    public Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(LocationMapper.toLocation(newEventDto.getLocation()));
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());

        return event;
    }
}
