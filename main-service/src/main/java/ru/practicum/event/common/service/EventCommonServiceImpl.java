package ru.practicum.event.common.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.enums.EventSorting;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.dto.UserShortDto;
import ru.practicum.user.model.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCommonServiceImpl implements EventCommonService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;

    //Получение событий с возможностью фильтраций
    /* text - текст для поиска в содержимом аннотации и подробном описании события
    categories - список идентификаторов категорий в которых будет вестись поиск
    paid - поиск только платных/бесплатных событий
    rangeStart - дата и время не раньше которых должно произойти событие
    rangeEnd - дата и время не позже которых должно произойти событие
    onlyAvailable - только события у которых не исчерпан лимит запросов на участие
    sort - Вариант сортировки: по дате события или по количеству просмотров (EVENT_DATE, VIEWS)
    from - количество событий, которые нужно пропустить для формирования текущего набора
    size - количество событий в наборе
     */
    @Override
    public List<EventShortOutDto> findEvents(String text, int[] categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, boolean onlyAvailable, EventSorting sort,
                                             int from, int size) {
        PageRequest page = pagination(from, size, sort);

        Optional<BooleanExpression> conditions = getFinalCondition(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable);

        log.info("Получены события с фильтрацией");
        return conditions
                .map(c -> eventRepository.findAll(c, page).getContent())
                .orElseGet(() -> eventRepository.findAll(page).getContent()).stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullOutDto findEventById(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", id)));
        if (event.getState().equals("PUBLISHED")) {
            CategoryDto category = CategoryMapper.toCategoryDto(event.getCategory());
            UserShortDto initiator = UserMapper.toUserShortDto(event.getInitiator());
            log.info("Получена полная информация о событии с id = {}", id);
            return EventMapper.toEventFullDto(event, category, initiator,
                    requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED));
        }
        throw new ConditionsNotMet("There are no rights to view the event with id=%s because it has not been published yet");//событие не опубликовано
    }

    //Добавить просмотр всех событий
    @Override
    public void addViewsForEvents(List<EventShortOutDto> eventsShortDtos) {
        for (EventShortOutDto eventShortDto : eventsShortDtos) {
            Event event = eventRepository.findById(eventShortDto.getId())
                    .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventShortDto.getId())));
            event.setView(event.getView() + 1);
            eventRepository.save(event);
        }
    }

    //Добавить просмотр одного события
    @Override
    public void addViewForEvent(EventFullOutDto eventFullOutDto) {
        Event event = eventRepository.findById(eventFullOutDto.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventFullOutDto.getId())));
        event.setView(event.getView() + 1);
        eventRepository.save(event);
    }

    private PageRequest pagination(int from, int size, EventSorting sort) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.by(String.valueOf(sort)).descending());
    }

    //Параметры для фильтрации
    private Optional<BooleanExpression> getFinalCondition(String text, int[] categories, Boolean paid, LocalDateTime rangeStart,
                                                          LocalDateTime rangeEnd, boolean onlyAvailable) {
        List<BooleanExpression> conditions = new ArrayList<>();
        QEvent event = QEvent.event;

        if (text != null) {
            conditions.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            List<Integer> catIds = Arrays.stream(categories)
                    .mapToObj(Integer::valueOf)
                    .collect(Collectors.toList());
            conditions.add(event.category.id.in(catIds));
        }
        if (paid != null) {
            conditions.add(paid ? event.paid.isTrue() : event.paid.isFalse());
        }
        if (rangeStart != null) {
            conditions.add(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.before(rangeEnd));
        }
        if (onlyAvailable) {
            QRequest request = QRequest.request;
            BooleanExpression ifLimitIsZero = event.participantLimit.eq(0);
            BooleanExpression ifRequestModerationFalse = event.requestModeration.isFalse()
                    .and(event.participantLimit.goe(request.count()));
            BooleanExpression ifRequestModerationTrue = event.requestModeration.isTrue()
                    .and(event.participantLimit.goe(request.status.eq(StatusRequest.CONFIRMED).count()));
            conditions.add(ifLimitIsZero.or(ifRequestModerationFalse).or(ifRequestModerationTrue));
        }
        return conditions.stream()
                .reduce(BooleanExpression::and);
    }

}
