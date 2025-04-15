package ru.practicum.ExploreWithMe.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ExploreWithMe.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorIdOrderByEventDateDesc(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Set<Event> findByIdIn(List<Long> eventIds);

    Boolean existsByCategoryId(Long categoryId);

    @Query("""
        SELECT e FROM Event e WHERE (:users IS NULL OR e.initiator.id IN :users)
        AND (:states IS NULL OR e.state IN :states)
        AND (:categories IS NULL OR e.category.id IN :categories)
        AND (CAST(:rangeStart AS DATE) IS NULL OR e.eventDate >= :rangeStart)
        AND (CAST(:rangeEnd AS DATE) IS NULL OR e.eventDate <= :rangeEnd)
        ORDER BY e.eventDate DESC
    """)
    List<Event> adminSearchEvents(@Param("users") List<Long> users, @Param("states") List<State> states, @Param("categories") List<Long> categories, @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("""
        SELECT e FROM Event e WHERE (:text IS NULL OR (e.annotation ILIKE %:text% OR e.description ILIKE %:text%))
        AND (e.state = :state)
        AND (:categories IS NULL OR e.category.id IN :categories)
        AND (:paid IS NULL OR e.paid = :paid)
        AND (e.eventDate >= :rangeStart)
        AND (CAST(:rangeEnd AS DATE) IS NULL OR e.eventDate <= :rangeEnd)
        AND (:onlyAvailable = false OR e.participantLimit = 0 OR e.confirmedRequests <= e.participantLimit)
        ORDER BY e.eventDate DESC
    """)
    List<Event> publicSearchEvents(@Param("text") String text, @Param("categories") List<Long> categories, @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable, @Param("state") State state, Pageable pageable);
}
