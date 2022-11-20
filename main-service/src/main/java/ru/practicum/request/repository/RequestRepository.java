package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.StatusRequest;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    boolean existsByRequesterIdAndEventId(int requesterId, int eventId);

    int countByEventIdAndStatus(int evenId, StatusRequest status);

    List<Request> findAllByRequesterId(int requesterId);
}
