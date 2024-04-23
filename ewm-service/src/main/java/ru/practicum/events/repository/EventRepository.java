package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiatorId(Long initiatorId, Pageable page);

    List<Event> findByCategoryId(Long categoryId);

    Event findByInitiatorIdAndId(Long initiatorId, Long eventId);

    Set<Event> findByIdIn(Set<Long> eventIds);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

    @Query("select e from Event as e " +
            "where (e.state = 'PUBLISHED') " +
            "and (?1 is null) " +
            "or (lower(e.annotation) like lower(concat('%', ?1, '%'))) " +
            "or (lower(e.description) like lower(concat('%', ?1, '%'))) " +
            "or (lower(e.title) like lower(concat('%', ?1, '%'))) " +
            "and (?2 is null or e.category.id in ?2) " +
            "and (?3 is null or e.paid = ?3) " +
            "or (cast(?4 as date) is null and cast(?4 as date) is null) " +
            "or (cast(?4 as date) is null and e.eventDate < cast(?5 as date)) " +
            "or (cast(?5 as date) is null and e.eventDate > cast(?4 as date)) " +
            "and (e.confirmedRequests < e.participantLimit or ?6 = false) " +
            "group by e.id")
    List<Event> findEventsByPublicFromParam(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            Boolean onlyAvailable, String sort, Pageable page);

}
