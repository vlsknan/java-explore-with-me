package ru.practicum.comment.admin.service;

import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.QComment;
import ru.practicum.comment.model.dto.CommentDtoOut;
import ru.practicum.comment.model.mapper.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.enums.Status;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.*;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.model.dto.UserShortDto;
import ru.practicum.user.model.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utility.PageUtility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentAdminServiceImpl implements CommentAdminService {
    final CommentRepository commentRepository;
    final UserRepository userRepository;
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final StatsClient statsClient;

    @Override
    public List<CommentDtoOut> getAllCommentsByEvent(int eventId, List<Integer> users, List<String> status,
                                                     String start, String end, int from, int size) {
        Event event = getEventById(eventId);
        List<User> userList = users.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());

        PageRequest page = PageUtility.pagination(from, size);

        LocalDateTime startTime;
        LocalDateTime endTime;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (start == null) {
            startTime = LocalDateTime.now();
        } else {
            startTime = LocalDateTime.parse(start, format);
        }
        if (end == null) {
            endTime = LocalDateTime.now().plusYears(20);
        } else {
            endTime = LocalDateTime.parse(end, format);
        }

        List<Status> commentStatus = null;
        if (status != null) {
            commentStatus = status.stream()
                    .map(this::mapToStatus)
                    .collect(Collectors.toList());
        }
        QComment comment = QComment.comment;
        List<Predicate> predicates = new ArrayList<>();
        if (users != null) {
            predicates.add(comment.user.id.in(users));
        }
        if (status != null) {
            predicates.add(comment.status.in(commentStatus));
        }
        predicates.add(comment.publishedOn.after(startTime));
        predicates.add(comment.publishedOn.before(endTime));
        Predicate param = allOf(predicates);

        Page<Comment> comments = commentRepository.findAll(param, page);
        log.info("Получены все комментарии к событию с id = {}", eventId);
        return comments.stream()
                .map(c -> getCommentDto(c, event))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoOut publishComment(int eventId, int comId) {
        Event event = getEventById(eventId);
        Comment comment = getCommentById(comId);
        if (comment.getEvent() != event) {
            throw new ConditionsNotMet(String.format("Comment with id = %s not related to event with id = %s",
                    comId, eventId));
        }
        if (comment.getStatus().equals(Status.PENDING)) {
            comment.setStatus(Status.PUBLISHED);
            comment.setPublishedOn(LocalDateTime.now());
            commentRepository.save(comment);
            log.info("Комментарий с id = {} опубликован", comId);
            return getCommentDto(comment, event);
        }
        throw new ConditionsNotMet(String.format("Comment already has status %s", comment.getStatus()));
    }

    @Override
    public CommentDtoOut rejectComment(int eventId, int comId) {
        Event event = getEventById(eventId);
        Comment comment = getCommentById(comId);

        if (comment.getEvent() != event) {
            throw new ConditionsNotMet(String.format("Comment with id = %s not related to event with id = %s",
                    comId, eventId));
        }
        if (comment.getStatus().equals(Status.PENDING)) {
            comment.setStatus(Status.CANCELED);
            commentRepository.save(comment);
            log.info("Комментарий с id = {} отклонен", comId);
            return getCommentDto(comment, event);
        }
        throw new ConditionsNotMet(String.format("Comment already has status %s", comment.getStatus()));

    }

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Event getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Comment getCommentById(int comId) {
        return commentRepository.findById(comId)
                .orElseThrow(() -> new CommentNotFoundException(comId));
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

    private Status mapToStatus(String status) {
        try {
            return Status.valueOf(status);
        } catch (InvalidRequestException e) {
            throw new InvalidRequestException(String.format("Status is unsupported: %s", status));
        }
    }
}
