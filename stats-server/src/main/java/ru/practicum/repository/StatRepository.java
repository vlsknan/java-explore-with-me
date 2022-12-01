package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Integer> {
    List<EndpointHit> findAllByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT count(ip) FROM EndpointHit " +
            "WHERE uri = ?1")
    Integer findHitCountByUri(String uri);

    @Query("SELECT count(DISTINCT ip) FROM EndpointHit " +
            "WHERE uri = ?1")
    Integer findHitCountByUriWithUniqueIp(String uri);
}
