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
import ru.practicum.category.model.Category;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.StateEvent;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.UpdateEventRequest;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.InvalidRequestException;
import ru.practicum.exception.model.NotFoundException;
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
        PageRequest page = pagination(from, size);

        LocalDateTime startTime;
        LocalDateTime endTime;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart == null) {
            startTime = LocalDateTime.now();
        } else {
            startTime = LocalDateTime.parse(rangeStart, format);
        }
        if (rangeStart == null) {
            endTime = LocalDateTime.now().plusYears(20);
        } else {
            endTime = LocalDateTime.parse(rangeEnd, format);
        }

        List<StateEvent> stateEvents = null;
        if (states != null) {
            stateEvents = states.stream()
                    .map(this::mapToState)
                    .collect(Collectors.toList());
        }

        QEvent event = QEvent.event;

        List<Predicate> predicates = new ArrayList<>();
        if (users != null) {
            predicates.add(event.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicates.add(event.state.in(stateEvents));
        }
        if (categories != null && !categories.isEmpty()) {
            predicates.add(event.category.id.in(categories));
        }
        if (rangeStart != null) {
            predicates.add(event.eventDate.after(startTime));
        }
        if (rangeEnd != null) {
            predicates.add(event.eventDate.before(endTime));
        }
        Predicate param = allOf(predicates);
        Page<Event> events;
        if (param != null) {
            events = eventRepository.findAll(param, page);
        } else {
            events = eventRepository.findAllByEventDate(startTime, endTime, page);
        }
        log.info("Получены события с фильтрацией");
        return events.stream()
                .map(e -> EventMapper.toEventFullDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED),
                        statsClient.getViews(e.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullOutDto update(int eventId, UpdateEventRequest updateEvent) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        if (updateEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found.",
                            updateEvent.getCategory())));
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
        return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()),
                requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED),
                statsClient.getViews(event.getId()));
    }

    @Override
    @Transactional
    public EventFullOutDto publishEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));

        if (event.getState().equals(StateEvent.PENDING)) {
            if (!event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                event.setState(StateEvent.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                eventRepository.save(event);
                log.info("Событие с id = {} опубликовано", eventId);
                return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                        UserMapper.toUserShortDto(event.getInitiator()),
                        requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED),
                        statsClient.getViews(event.getId()));
            }
            throw new ConditionsNotMet("Start time of event must be at least 1 hour from now");
        }
        throw new ConditionsNotMet("Only pending events can be changed");
    }

    @Override
    @Transactional
    public EventFullOutDto rejectEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));

        if (event.getState().equals(StateEvent.PENDING)) {
            if (!event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                event.setState(StateEvent.CANCELED);
                eventRepository.save(event);
                log.info("Событие с id = {} отклонено", eventId);
                return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                        UserMapper.toUserShortDto(event.getInitiator()),
                        requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED),
                        statsClient.getViews(event.getId()));
            }
            throw new ConditionsNotMet("The start date of the event should be no earlier than one hour " +
                    "after the moment of publication");
        }
        throw new ConditionsNotMet("Only pending events can be changed");
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }

    private StateEvent mapToState(String state) {
        try {
            return StateEvent.valueOf(state);
        } catch (InvalidRequestException e) {
            throw new InvalidRequestException(String.format("State is unsupported: %s", state));
        }
    }
}
