package ru.practicum.ExploreWithMe.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.Stats.EndpointHitDto;
import ru.practicum.ExploreWithMe.Stats.StatsClientApp;
import ru.practicum.ExploreWithMe.Stats.ViewStatsDto;
import ru.practicum.ExploreWithMe.category.Category;
import ru.practicum.ExploreWithMe.enums.*;
import ru.practicum.ExploreWithMe.event.dto.*;
import ru.practicum.ExploreWithMe.exception.ValidationException;
import ru.practicum.ExploreWithMe.category.CategoryMapper;
import ru.practicum.ExploreWithMe.category.CategoryRepository;
import ru.practicum.ExploreWithMe.category.CategoryService;
import ru.practicum.ExploreWithMe.exception.ConflictException;
import ru.practicum.ExploreWithMe.exception.NotFoundException;
import ru.practicum.ExploreWithMe.location.Location;
import ru.practicum.ExploreWithMe.location.LocationMapper;
import ru.practicum.ExploreWithMe.location.LocationRepository;
import ru.practicum.ExploreWithMe.event.dto.UpdateEventAdminRequest;
import ru.practicum.ExploreWithMe.event.dto.UpdateEventUserRequest;
import ru.practicum.ExploreWithMe.request.RequestRepository;
import ru.practicum.ExploreWithMe.user.User;
import ru.practicum.ExploreWithMe.user.UserRepository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.by;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final CategoryService categoryService;
    private final StatsClientApp statsClientApp;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Event newEvent = EventMapper.toEvent(newEventDto);
        newEvent.setInitiator(user);
        newEvent.setState(State.PENDING);
        newEvent.setLocation(locationRepository.save(newEvent.getLocation()));
        if (newEventDto.getCategory() != null) {
            newEvent.setCategory(categoryRepository.findById(newEventDto.getCategory()).orElseThrow());
        }
        newEvent.setConfirmedRequests(0L);
        newEvent.setCreatedOn(LocalDateTime.now());
        return EventMapper.toEventFullDto(eventRepository.save(newEvent));
    }

    @Transactional
    public EventFullDto updateEventByInitiator(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest) {
        if (!eventRepository.existsById(eventId)) {
            throw new ValidationException("Такого события не существует");
        }
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow();

        if (event.getState() != null && event.getState() == State.PUBLISHED) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toNotNewCategory(categoryService.getCategoryById(updateEventUserRequest.getCategory())));
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEventUserRequest.getLocation());
            if (!locationRepository.existsByLatAndLon(location.getLat(), location.getLon())) {
                locationRepository.save(location);
            }
            event.setLocation(location);
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateActionUser.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            }
            if (updateEventUserRequest.getStateAction().equals(StateActionUser.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (!eventRepository.existsById(eventId)) {
            throw new ValidationException("Такого события не существует");
        }

        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toNotNewCategory(categoryService.getCategoryById(updateEventAdminRequest.getCategory())));
        }
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEventAdminRequest.getLocation());
            if (!locationRepository.existsByLatAndLon(location.getLat(), location.getLon())) {
                locationRepository.save(location);
            }
            event.setLocation(location);
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == StateActionAdmin.REJECT_EVENT && event.getState() == State.PUBLISHED) {
                throw new ConflictException("Отменить можно только не опубликованные события");
            }
            if (updateEventAdminRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT && !event.getState().equals(State.PENDING)) {
                throw new ConflictException("Можно публиковать только события в статусе PENDING");
            }
            if (updateEventAdminRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
            }
            if (updateEventAdminRequest.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                event.setState(State.CANCELED);
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    public EventFullDto getEventById(Long eventId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }

        return EventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow());
    }

    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return eventRepository.findAllByInitiatorIdOrderByEventDateDesc(userId, pageable).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {
        if (!eventRepository.existsById(eventId)) {
            throw new ValidationException("Такого события не существует");
        }
        if (eventRepository.findById(eventId).orElseThrow().getState() != State.PUBLISHED) {
            throw new NotFoundException("Такое событие со статусом PUBLISHED не найдено");
        }

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (event.getViews() == null) {
            event.setViews(0L);
        }
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri(httpServletRequest.getRequestURI());
        endpointHitDto.setIp(httpServletRequest.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());
        statsClientApp.createHit(endpointHitDto);

        List<ViewStatsDto> viewStatsDtoList = statsClientApp.getStats(event.getCreatedOn(), LocalDateTime.now(), List.of(httpServletRequest.getRequestURI()), true);

        event.setViews(viewStatsDtoList.getFirst().getHits());

        eventRepository.save(event);

        return EventMapper.toEventFullDto(event, viewStatsDtoList.getFirst().getHits(), requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED));
    }

    public List<EventFullDto> adminSearchEvents(List<Long> users, List<State> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {

        LocalDateTime startTime;
        LocalDateTime endTime;

        if (users == null || users.isEmpty()) {
            users = userRepository.findAll().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
        }

        if (states == null || states.isEmpty()) {
            states = List.of(State.PENDING, State.PUBLISHED, State.CANCELED);
        }

        if (categories  == null || categories.isEmpty()) {
            categories = categoryRepository.findAll().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
        }

        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, FORMATTER);
        } else {
            startTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        }

        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, FORMATTER);
        } else {
            endTime = LocalDateTime.of(2100, 1, 1, 1, 1);
        }

        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return eventRepository.adminSearchEvents(users, states, categories, startTime, endTime, pageable).stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    public List<EventShortDto> publicSearchEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, Sort sort, Integer from, Integer size, HttpServletRequest httpServletRequest) {
        LocalDateTime startTime;
        LocalDateTime endTime;

        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, FORMATTER);
        } else {
            startTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        }

        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, FORMATTER);
        } else {
            endTime = LocalDateTime.of(2100, 1, 1, 1, 1);
        }

        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        if (categories  == null || categories.isEmpty()) {
            categories = categoryRepository.findAll().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
        }

        Pageable pageable;
        if (sort != null) {
            if (sort.toString().equalsIgnoreCase("EVENT_DATE")) {
                pageable = PageRequest.of(from / size, size, by("eventDate").ascending());
            } else if (sort.toString().equalsIgnoreCase("VIEWS")) {
                pageable = PageRequest.of(from / size, size, by("views").descending());
            } else {
                pageable = PageRequest.of(from / size, size, by("id").ascending());
            }
        } else {
            pageable = PageRequest.of(from / size, size, by("id").ascending());
        }

        List<Event> events = eventRepository.publicSearchEvents(text, categories, paid, startTime, endTime, onlyAvailable, State.PUBLISHED, pageable);

        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("ewm-main-service");
        endpointHitDto.setUri(httpServletRequest.getRequestURI());
        endpointHitDto.setIp(httpServletRequest.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());
        statsClientApp.createHit(endpointHitDto);
        if (events != null || !events.isEmpty()) {
            eventRepository.saveAll(events);
        }

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }
}
