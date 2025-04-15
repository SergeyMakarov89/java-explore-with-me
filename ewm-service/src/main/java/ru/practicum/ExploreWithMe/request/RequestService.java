package ru.practicum.ExploreWithMe.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.enums.RequestStatus;
import ru.practicum.ExploreWithMe.enums.State;
import ru.practicum.ExploreWithMe.event.Event;
import ru.practicum.ExploreWithMe.event.EventRepository;
import ru.practicum.ExploreWithMe.exception.ConflictException;
import ru.practicum.ExploreWithMe.exception.NotFoundException;
import ru.practicum.ExploreWithMe.exception.ValidationException;
import ru.practicum.ExploreWithMe.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ExploreWithMe.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ExploreWithMe.request.dto.ParticipationRequestDto;
import ru.practicum.ExploreWithMe.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }
        if (requestRepository.existsByRequestorIdAndEventId(userId, eventId)) {
            throw new ConflictException("Такой запрос уже существует");
        }
        if (eventRepository.findById(eventId).orElseThrow().getInitiator().getId().equals(userId)) {
            throw new ConflictException("Организатор события не может подать заявку на участие в своем же событии");
        }
        if (eventRepository.findById(eventId).orElseThrow().getState() != State.PUBLISHED) {
            throw new ConflictException("Подать заявку на участие можно только в опубликованном событии");
        }

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (event.getConfirmedRequests() != 0 && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("У этого события нет свободных мест на участие");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequestor(userRepository.findById(userId).orElseThrow());
        request.setCreated(LocalDateTime.now());

        if (event.getParticipantLimit() != 0 && event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        if (!requestRepository.findById(requestId).orElseThrow().getRequestor().getId().equals(userId)) {
            throw new ConflictException("У этого запроса другой инициатор");
        }

        Request request = requestRepository.findByIdAndRequestorId(requestId, userId);
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequests(Long eventId, Long userId, EventRequestStatusUpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("У этого события другой организатор");
        }

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("У этого события нет свободных мест на участие");
        }

        List<Request> requestList = requestRepository.findAllByEventIdAndIdInAndStatus(eventId, request.getRequestIds(), RequestStatus.PENDING);
        Long freePoolOfRequests = event.getParticipantLimit() - confirmedRequests;
        for (Request request1 : requestList) {
            if (!request1.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Статус должен быть PENDING");
            } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
                request1.setStatus(RequestStatus.REJECTED);
            } else if (!event.getRequestModeration()) {
                if (event.getParticipantLimit() == 0) {
                    request1.setStatus(RequestStatus.CONFIRMED);
                }
            } else if ((event.getParticipantLimit() - confirmedRequests) == 0) {
                request1.setStatus(RequestStatus.REJECTED);
            } else {
                request1.setStatus(RequestStatus.CONFIRMED);
                freePoolOfRequests--;
            }
            requestRepository.save(request1);
        }

        if (event.getParticipantLimit() != 0) {
            event.setConfirmedRequests(event.getParticipantLimit() - freePoolOfRequests);
        } else {
            event.setConfirmedRequests(confirmedRequests);
        }

        eventRepository.save(event);

        EventRequestStatusUpdateResult requestStatusUpdateResult = new EventRequestStatusUpdateResult();

        requestStatusUpdateResult.setRejectedRequests(requestRepository.findAllByEventIdAndIdInAndStatus(eventId, request.getRequestIds(), RequestStatus.REJECTED).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList());
        requestStatusUpdateResult.setConfirmedRequests(requestRepository.findAllByEventIdAndIdInAndStatus(eventId, request.getRequestIds(), RequestStatus.CONFIRMED).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList());

        return requestStatusUpdateResult;
    }

    public List<ParticipationRequestDto> getAllUserParticipationRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }

        return requestRepository.findAllByRequestorId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    public List<ParticipationRequestDto> getAllEventParticipationRequests(Long eventId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("У этого события другой организатор");
        }

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }
}
