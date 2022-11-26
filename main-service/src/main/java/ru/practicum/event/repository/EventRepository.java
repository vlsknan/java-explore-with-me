package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {
    List<Event> findEventByInitiator(User initiator, PageRequest page);

    @Query("select e from Event e " +
            "where e.eventDate between :startTime and :endTime ")
    Page<Event> findAllByEventDate(LocalDateTime startTime, LocalDateTime endTime, PageRequest page);
}
