package ru.practicum.event.common.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.enums.Status;
import ru.practicum.utility.PageUtility;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.enums.EventSorting;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.EventNotFoundException;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.dto.UserShortDto;
import ru.practicum.user.model.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCommonServiceImpl implements EventCommonService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final StatsClient statsClient;

    //Поиск событий с возможностью фильтраций
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
    public List<EventShortOutDto> getFilteredEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                                    String rangeEnd, boolean onlyAvailable, EventSorting sort,
                                                    int from, int size) {
        PageRequest page = PageUtility.pagination(from, size, sort);

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

        QEvent event = QEvent.event;
        List<Predicate> predicates = new ArrayList<>();
        if (text != null) {
            predicates.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            predicates.add(event.category.id.in(categories));
        }
        if (paid != null) {
            predicates.add(paid ? event.paid.isTrue() : event.paid.isFalse());
        }
        if (onlyAvailable) {
            QRequest request = QRequest.request;
            BooleanExpression ifLimitIsZero = event.participantLimit.eq(0);
            BooleanExpression ifRequestModerationFalse = event.requestModeration.isFalse()
                    .and(event.participantLimit.goe(request.count()));
            BooleanExpression ifRequestModerationTrue = event.requestModeration.isTrue()
                    .and(event.participantLimit.goe(request.status.eq(StatusRequest.CONFIRMED).count()));
            predicates.add(ifLimitIsZero.or(ifRequestModerationFalse).or(ifRequestModerationTrue));
        }
        predicates.add(event.eventDate.after(startTime));
        predicates.add(event.eventDate.before(endTime));
        Predicate param = allOf(predicates);

        Page<Event> events = eventRepository.findAll(param, page);
        log.info("Получены события с фильтрацией");
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED),
                        statsClient.getViews(e.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullOutDto getEventById(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (event.getState().equals(Status.PUBLISHED)) {
            CategoryDto category = CategoryMapper.toCategoryDto(event.getCategory());
            UserShortDto initiator = UserMapper.toUserShortDto(event.getInitiator());
            log.info("Получена полная информация о событии с id = {}", id);
            return EventMapper.toEventFullDto(event, category, initiator,
                    requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED),
                    statsClient.getViews(event.getId()));
        }
        throw new ConditionsNotMet("There are no rights to view the event with id=%s" +
                " because it has not been published yet");
    }
}
