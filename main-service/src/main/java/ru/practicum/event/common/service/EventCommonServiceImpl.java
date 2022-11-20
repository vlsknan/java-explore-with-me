package ru.practicum.event.common.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.enums.EventSorting;
import ru.practicum.event.client.StatClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.user.model.dto.UserShortDto;
import ru.practicum.user.model.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCommonServiceImpl implements EventCommonService {
    final EventRepository repository;
    final StatClient statClient;
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        List<Event> events;
        PageRequest page = pagination(from, size, sort);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.parse(LocalDateTime.now().format(formatter));
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.parse(LocalDateTime.now().plusYears(10).format(formatter));
        }
        if (categories != null) {
            if (paid != null) {
                events = repository.findEventWithCategoriesWithPaid(text, categories, paid, rangeStart, rangeEnd, page);
            } else {
                events = repository.findEventWithCategoriesWithoutPaid(text, categories, rangeStart, rangeEnd, page);
            }
        } else {
            if (paid != null) {
                events = repository.findEventWithoutCategoriesWithPaid(text, paid, rangeStart, rangeEnd, page);
            } else {
                events = repository.findEventWithoutCategoriesWithoutPaid(text, rangeStart, rangeEnd, page);
            }
        }
        //добавить из статистики confirmedRequests и views
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()), ))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullOutDto findEventById(int id) {
        Event event = repository.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", id)));
        if (event.getState().equals("PUBLISHED")) {
            CategoryDto category = CategoryMapper.toCategoryDto(event.getCategory());
            UserShortDto initiator = UserMapper.toUserShortDto(event.getInitiator());
            //добавить из статистики confirmedRequests и views
            return EventMapper.toEventFullDto(event, category, initiator, );
        }
        throw new ConditionsNotMet("There are no rights to view the event with id=%s because it has not been published yet");//событие не опубликовано
    }

    private PageRequest pagination(int from, int size, EventSorting sort) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.by(String.valueOf(sort)).descending());
    }


}
