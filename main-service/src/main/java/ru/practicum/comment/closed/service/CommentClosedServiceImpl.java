package ru.practicum.comment.closed.service;

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
import ru.practicum.comment.model.dto.CommentDtoIn;
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
import ru.practicum.request.model.Request;
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

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentClosedServiceImpl implements CommentClosedService {
    final CommentRepository commentRepository;
    final UserRepository userRepository;
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final StatsClient statsClient;

    @Override
    public List<CommentDtoOut> getAllCommentsByUser(int userId, List<Integer> events, List<String> status,
                                                    String start, String end, int from, int size) {
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
        if (events != null) {
            predicates.add(comment.event.id.in(events));
        }
        if (status != null) {
            predicates.add(comment.status.in(commentStatus));
        }
        predicates.add(comment.createdOn.after(startTime));
        predicates.add(comment.createdOn.before(endTime));
        Predicate param = allOf(predicates);

        Page<Comment> comments = commentRepository.findAll(param, page);
        log.info("Получены все комментарии пользователя с id = {}", userId);
        return comments.stream()
                .map(c -> getCommentDto(c, c.getEvent()))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoOut getCommentById(int userId, int comId) {
        User user = getUserById(userId);
        Comment comment = getCommentById(comId);
        if (comment.getUser() != user) {
            throw new ConditionsNotMet("You are not the author of the comment");
        }
        log.info("Получен комментарий с id = {}", comId);
        return getCommentDto(comment, comment.getEvent());
    }

    @Override
    public CommentDtoOut createComment(int userId, int eventId, CommentDtoIn commentDto) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        Request request = requestRepository.findByEventAndRequester(event, user);
        //Событие должно уже пройти
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            //Пользователь должен посетить событие
            if (request.getStatus().equals(StatusRequest.CONFIRMED)) {
                Comment comment = CommentMapper.toComment(commentDto, user, event);
                comment.setStatus(Status.PENDING);

                Comment newComment = commentRepository.save(comment);
                log.info("Комментарий с id = {} создан", newComment.getId());
                return getCommentDto(newComment, event);
            }
            throw new ConditionsNotMet("You did not participate in the event or your application was not approved");
        }
        throw new ConditionsNotMet("The event has not yet passed");
    }

    @Override
    public CommentDtoOut updateComment(int userId, int eventId, int comId, CommentDtoIn commentDto) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        Comment comment = getCommentById(comId);
        if (comment.getUser() != user || comment.getEvent() != event) {
            throw new ConditionsNotMet(String.format("The user or event is not related to the comment " +
                    "with id = %s", comId));
        }

        if (comment.getStatus().equals(Status.PUBLISHED)) {
            //Комментарий можно отредактировать в течении 2 дней после публикации
            if (comment.getPublishedOn().plusDays(2).isBefore(LocalDateTime.now())) {
                comment.setText(commentDto.getText());
                comment.setStatus(Status.PENDING);
                commentRepository.save(comment);
                return getCommentDto(comment, event);
            }
            throw new ConditionsNotMet("Comments can be edited within 2 days after posting");
        }
        throw new ConditionsNotMet("Comment not posted or rejected");
    }

    @Override
    public void deleteComment(int userId, int comId) {
        User user = getUserById(userId);
        Comment comment = getCommentById(comId);
        if (comment.getUser() != user) {
            throw new ConditionsNotMet("You are not the author of the comment");
        }
        commentRepository.delete(comment);
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
