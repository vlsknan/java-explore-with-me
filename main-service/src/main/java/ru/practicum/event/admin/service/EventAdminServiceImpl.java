package ru.practicum.event.admin.service;

import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.utility.PageUtility;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.Status;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.UpdateEventRequest;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.*;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventAdminServiceImpl implements EventAdminService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final CategoryRepository categoryRepository;
    final StatsClient statsClient;

    /* Поиск событий с условиями
    users - список id пользователей, чьи события нужно найти
    states - список состояний в которых находятся искомые события
    categories - список id категорий в которых будет вестись поиск
    rangeStart - дата и время не раньше которых должно произойти событие
    rangeEnd - дата и время не позже которых должно произойти событие
    from - количество событий, которые нужно пропустить для формирования текущего набора
    size - количество событий в наборе
    */
    @Override
    public List<EventFullOutDto> searchEvents(List<Integer> users, List<String> states, List<Integer> categories, String rangeStart,
                                              String rangeEnd, Integer from, Integer size) {
        PageRequest page = PageUtility.pagination(from, size);

        LocalDateTime startTime;
        LocalDateTime endTime;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart == null) {
            startTime = LocalDateTime.now();
        } else {
            startTime = LocalDateTime.parse(rangeStart, format);
        }
        if (rangeEnd == null) {
            endTime = LocalDateTime.now().plusYears(20);
        } else {
            endTime = LocalDateTime.parse(rangeEnd, format);
        }

        List<Status> status = null;
        if (states != null) {
            status = states.stream()
                    .map(this::mapToStatus)
                    .collect(Collectors.toList());
        }

        QEvent event = QEvent.event;

        List<Predicate> predicates = new ArrayList<>();
        if (users != null) {
            predicates.add(event.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicates.add(event.state.in(status));
        }
        if (categories != null && !categories.isEmpty()) {
            predicates.add(event.category.id.in(categories));
        }
        predicates.add(event.eventDate.after(startTime));
        predicates.add(event.eventDate.before(endTime));
        Predicate param = allOf(predicates);

        Page<Event> events = eventRepository.findAll(param, page);
        log.info("Получены события с фильтрацией");
        return events.stream()
                .map(this::getEventFullOutDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullOutDto update(int eventId, UpdateEventRequest updateEvent) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        if (updateEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(updateEvent.getCategory()));
            oldEvent.setCategory(category);
        }
        if (updateEvent.getDescription() != null) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            oldEvent.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getTitle() != null) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        Event event = eventRepository.save(oldEvent);
        log.info("Событие с id = {} изменено", eventId);
        return getEventFullOutDto(event);
    }

    @Override
    @Transactional
    public EventFullOutDto publishEvent(int eventId) {
        Event event = getEventById(eventId);

        if (event.getState().equals(Status.PENDING)) {
            if (!event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                event.setState(Status.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                eventRepository.save(event);
                log.info("Событие с id = {} опубликовано", eventId);
                return getEventFullOutDto(event);
            }
            throw new ConditionsNotMet("Start time of event must be at least 1 hour from now");
        }
        throw new ConditionsNotMet("Only pending events can be changed");
    }

    @Override
    @Transactional
    public EventFullOutDto rejectEvent(int eventId) {
        Event event = getEventById(eventId);

        if (event.getState().equals(Status.PENDING)) {
            if (!event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                event.setState(Status.CANCELED);
                eventRepository.save(event);
                log.info("Событие с id = {} отклонено", eventId);
                return getEventFullOutDto(event);
            }
            throw new ConditionsNotMet("The start date of the event should be no earlier than one hour " +
                    "after the moment of publication");
        }
        throw new ConditionsNotMet("Only pending events can be changed");
    }

    private Status mapToStatus(String status) {
        try {
            return Status.valueOf(status);
        } catch (InvalidRequestException e) {
            throw new InvalidRequestException(String.format("State is unsupported: %s", status));
        }
    }

    private Event getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private EventFullOutDto getEventFullOutDto(Event event) {
        return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()),
                requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED),
                statsClient.getViews(event.getId()));
    }
}
