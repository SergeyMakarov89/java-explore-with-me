package ru.practicum.ExploreWithMe.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ExploreWithMe.enums.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findByIdAndRequestorId(Long requestId, Long requestorId);

    List<Request> findAllByRequestorId(Long requestorId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndIdInAndStatus(Long eventId, List<Long> requestIds, RequestStatus status);

    Boolean existsByRequestorIdAndEventId(Long requestorId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);
}
