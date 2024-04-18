package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select e from EndpointHit e " +
            "where e.timestamp between ?1 and ?2")
    List<EndpointHit> findAllHitsBetweenDates(LocalDateTime start, LocalDateTime end);

    @Query("select e from EndpointHit e " +
            "where e.timestamp between ?1 and ?2 " +
            "and e.uri in ?3")
    List<EndpointHit> findByTimestampAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
