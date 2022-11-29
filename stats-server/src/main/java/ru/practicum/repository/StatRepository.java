package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Integer> {
    @Query("select distinct (e.ip), e.uri, e.app, e.id, e.timestamp from EndpointHit e " +
            "where e.timestamp between :startTime and :endTime and e.uri in (:uris) ")
    List<EndpointHit> findAllUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, String[] uris);

    @Query("select distinct (e.ip), e.uri, e.app, e.id, e.timestamp from EndpointHit e " +
            "where e.timestamp between :startTime and :endTime ")
    List<EndpointHit> findAllUnique(LocalDateTime startTime, LocalDateTime endTime);

    @Query("select e from EndpointHit e " +
            "where e.timestamp between :startTime and :endTime and e.uri in (:uris) ")
    List<EndpointHit> findAllNoUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, String[] uris);

    @Query("select e from EndpointHit e " +
            "where e.timestamp between :startTime and :endTime ")
    List<EndpointHit> findAllNoUnique(LocalDateTime startTime, LocalDateTime endTime);
}
