package ru.practicum.comment.common.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.dto.CommentDtoOut;
import ru.practicum.comment.model.mapper.CommentMapper;
import ru.practicum.enums.Status;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.CommentNotFoundException;
import ru.practicum.exception.model.EventNotFoundException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.dto.UserShortDto;
import ru.practicum.user.model.mapper.UserMapper;
import ru.practicum.utility.PageUtility;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCommonServiceImpl implements CommentCommonService {
    final CommentRepository commentRepository;
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final StatsClient statsClient;

    @Override
    public List<CommentDtoOut> getAll(int eventId, int from, int size) {
        Event event = getEventById(eventId);
        PageRequest page = PageUtility.pagination(from, size);

        List<Comment> comments = commentRepository.findAllByEventId(eventId, page);
        log.info("Получены все комментарии к событию с id = {}", eventId);
        return comments.stream()
                .filter(c -> c.getStatus().equals(Status.PUBLISHED))
                .map(com -> getCommentDto(com, event))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoOut getCommentById(int eventId, int comId) {
        Event event = getEventById(eventId);
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new CommentNotFoundException(comId));
        log.info("Получен комментарий с id = {}", comId);
        return getCommentDto(comment, event);
    }

    private Event getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private CommentDtoOut getCommentDto(Comment comment, Event event) {
        EventShortOutDto eventShort = EventMapper.toEventShortDto(event,
                CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()),
                requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED),
                statsClient.getViews(event.getId()));
        UserShortDto userShort = UserMapper.toUserShortDto(event.getInitiator());
        return CommentMapper.toCommentDto(comment, eventShort, userShort);
    }
}
