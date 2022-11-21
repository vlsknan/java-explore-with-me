package ru.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("select e from Event e " +
            "where lower(e.annotation) like lower(concat('%', ?1, '%')) or " +
            "lower(e.description) like lower(concat('%', ?1, '%')) " +
            "and e.category in (?2) and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 ")
    List<Event> findEventWithCategoriesWithPaid(String text, int[] categories, Boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page);

    @Query("select e from Event e " +
            "where lower(e.annotation) like lower(concat('%', ?1, '%')) or " +
            "lower(e.description) like lower(concat('%', ?1, '%')) " +
            "and e.category in (?2) " +
            "and e.eventDate between ?3 and ?4 ")
    List<Event> findEventWithCategoriesWithoutPaid(String text, int[] categories, LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd, PageRequest page);

    @Query("select e from Event e " +
            "where lower(e.annotation) like lower(concat('%', ?1, '%')) or " +
            "lower(e.description) like lower(concat('%', ?1, '%')) " +
            "and e.paid = ?2 " +
            "and e.eventDate between ?3 and ?4 ")
    List<Event> findEventWithoutCategoriesWithPaid(String text, Boolean paid, LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd, PageRequest page);

    @Query("select e from Event e " +
            "where lower(e.annotation) like lower(concat('%', ?1, '%')) or " +
            "lower(e.description) like lower(concat('%', ?1, '%')) " +
            "and e.eventDate between ?2 and ?3 ")
    List<Event> findEventWithoutCategoriesWithoutPaid(String text, LocalDateTime rangeStart,
                                                      LocalDateTime rangeEnd, PageRequest page);

    @Query("select e from Event e " +
            "where e.initiator in (:users) and e.state in (:states) and e.category in (:categories) " +
            "and e.eventDate between :rangeStart and :rangeEnd ")
    List<Event> findAllByParam(int[] users, String[] states, int[] categories, LocalDateTime rangeStart,
                               LocalDateTime rangeEnd, PageRequest page);

//    int countByIdAndStatus(int id, StatusRequest status);

    @Query("select e from Event e " +
            "where e.state in (:states) and e.category in (:categories) " +
            "and e.eventDate between :rangeStart and :rangeEnd")
    List<Event> findAllWithoutUsers(String[] states, int[] categories, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, PageRequest page);

    @Query("select e from Event e " +
            "where e.initiator in (:users) and e.category in (:categories) " +
            "and e.eventDate between :rangeStart and :rangeEnd")
    List<Event> findAllWithoutState(int[] users, int[] categories, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, PageRequest page);

    @Query("select e from Event e " +
            "where e.initiator in (:users) and e.state in (:states) and " +
            "e.eventDate between :rangeStart and :rangeEnd")
    List<Event> findAllWithoutCategory(int[] users, String[] states, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, PageRequest page);
}
