package ru.practicum.event.closed.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.enums.StateEvent;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.request.model.dto.NewEventInDto;
import ru.practicum.request.model.dto.UpdateEventRequest;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.dto.RequestDto;
import ru.practicum.request.model.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.model.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventClosedServiceImpl implements EventClosedService {
    final EventRepository eventRepository;
    final UserRepository userRepository;
    final RequestRepository requestRepository;

    @Override
    public List<EventShortOutDto> findEventByUser(int userId, int from, int size) {
        PageRequest page = pagination(from, size);
        User initiator = findUserById(userId);

        List<Event> events = eventRepository.findEventByInitiator(initiator, page);
        log.info("Получены события созданные пользователем с id = {}", userId);
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullOutDto update(int userId, UpdateEventRequest updateRequest) {
        User initiator = findUserById(userId);
        Event oldEvent = findEventById(updateRequest.getEventId());
        if (oldEvent.getInitiator() != initiator) {
            throw new ConditionsNotMet(String.format("You are not the initiator of the event c id = %s",
                    oldEvent.getId()));
        }
        if (oldEvent.getState().equals(StateEvent.CANCELED)) {
            oldEvent.setState(StateEvent.PENDING);
        }
        if (oldEvent.getState().equals(StateEvent.PENDING)) {
            if (updateRequest.getAnnotation() != null) {
                oldEvent.setAnnotation(updateRequest.getAnnotation());
            }
            if (updateRequest.getDescription() != null) {
                oldEvent.setDescription(updateRequest.getDescription());
            }
            if (updateRequest.getCategory() != null) {
                oldEvent.setCategory(CategoryMapper.toCategory(updateRequest.getCategory()));
            }
            if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                oldEvent.setEventDate(updateRequest.getEventDate());
            } else {
                throw new ConditionsNotMet("Event start time in less than 2 hours");
            }
            if (updateRequest.getPaid() != null) {
                oldEvent.setPaid(updateRequest.getPaid());
            }
            if (updateRequest.getParticipantLimit() != null) {
                oldEvent.setParticipantLimit(updateRequest.getParticipantLimit());
            }
            if (updateRequest.getTitle() != null) {
                oldEvent.setTitle(updateRequest.getTitle());
            }
        }
        Event updateEvent = eventRepository.save(oldEvent);
        log.info("Данные о событии с id = {} изменены", updateEvent.getId());
        return EventMapper.toEventFullDto(updateEvent, CategoryMapper.toCategoryDto(updateEvent.getCategory()),
                UserMapper.toUserShortDto(updateEvent.getInitiator()),
                requestRepository.countByEventIdAndStatus(updateEvent.getId(), StatusRequest.CONFIRMED));
    }

    @Override
    public EventFullOutDto create(int userId, NewEventInDto newEvent) {
        User initiator = findUserById(userId);
        if (newEvent.getEventDate() != null && newEvent.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            Event event = eventRepository.save(EventMapper.toEvent(newEvent,
                    CategoryMapper.toCategory(newEvent.getCategory()), initiator));
            log.info("Событие с заголовком '{}' создано", newEvent.getTitle());
            return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.toUserShortDto(event.getInitiator()),
                    requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED));
        }
        throw new ConditionsNotMet("Event start time in less than 2 hours");
    }

    @Override
    public EventFullOutDto findEventById(int userId, int eventId) {
        User initiator = findUserById(userId);
        Event event = findEventById(eventId);

        if (event.getInitiator() == initiator) {
            log.info("Получены данные события с id = {}", eventId);
            return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.toUserShortDto(event.getInitiator()),
                    requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED));
        }
        throw new ConditionsNotMet(String.format("You are not the initiator of the event with id=%s", eventId));
    }

    @Override
    public EventFullOutDto cancelEvent(int userId, int eventId) {
        User initiator = findUserById(userId);
        Event event = findEventById(eventId);
        if (event.getInitiator() != initiator) {
            throw new ConditionsNotMet(String.format("You are not the initiator of the event c id = %s",
                    event.getId()));
        }

        if (event.getState().equals(StateEvent.PENDING)) {
            event.setState(StateEvent.CANCELED);
            Event newEvent = eventRepository.save(event);
            log.info("Событие с id = {} отменено", eventId);
            return EventMapper.toEventFullDto(newEvent, CategoryMapper.toCategoryDto(newEvent.getCategory()),
                    UserMapper.toUserShortDto(newEvent.getInitiator()),
                    requestRepository.countByEventIdAndStatus(newEvent.getId(), StatusRequest.CONFIRMED));
        }
        throw new ConditionsNotMet(String.format("The event cannot be canceled because its status %s", event.getState()));

    }

    @Override
    public List<RequestDto> findRequestsByUser(int userId, int eventId) {
        User initiator = findUserById(userId);
        Event event = findEventById(eventId);

        List<Request> requests = requestRepository.findRequestByEvent(event);
        log.info("Получены запросы на событие с id = {}", eventId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto confirmRequest(int userId, int eventId, int reqId) {
        User initiator = findUserById(userId);
        Event event = findEventById(eventId);
        Request request = findRequestById(reqId);

        if (event.getParticipantLimit() != 0 && event.isRequestModeration()) {
            int limit = event.getParticipantLimit();
            int participantRequest = requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED);
            if (limit > participantRequest) {
                request.setStatus(StatusRequest.CONFIRMED);
                requestRepository.save(request);
                if (limit == ++participantRequest) {
                    requestRepository.findRequestByEvent(event).stream()
                            .filter(r -> r.getStatus() == StatusRequest.PENDING)
                            .forEach(r -> {
                                r.setStatus(StatusRequest.REJECTED);
                                requestRepository.save(r);
                            });
                }
            }
            throw new ConditionsNotMet(String.format("The event with id=%s has already reached " +
                    "the request limit", event.getId()));
        }
        log.info("Заявку с id = {} на участие в событии с id = {} подтверждена", reqId, eventId);
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public RequestDto rejectRequest(int userId, int eventId, int reqId) {
        User initiator = findUserById(userId);
        Event event = findEventById(eventId);
        Request request = findRequestById(reqId);

        if (event.getInitiator() == initiator) {
            request.setStatus(StatusRequest.REJECTED);
            requestRepository.save(request);
            log.info("Заявку с id = {} на участие в событии с id = {} отклонена", reqId, eventId);
            return RequestMapper.toRequestDto(request);
        }
        throw new ConditionsNotMet(String.format("You are not the initiator of the event with id=%s", eventId));
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }

    private User findUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found.", userId)));
    }

    private Event findEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
    }

    private Request findRequestById(int reqId) {
        return requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%s was not found.", reqId)));
    }
}
