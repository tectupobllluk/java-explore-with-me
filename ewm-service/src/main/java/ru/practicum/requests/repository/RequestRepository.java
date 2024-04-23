package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.Status;
import ru.practicum.requests.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, Status status);

}
