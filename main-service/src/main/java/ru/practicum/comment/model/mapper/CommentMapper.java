package ru.practicum.comment.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.dto.CommentDtoIn;
import ru.practicum.comment.model.dto.CommentDtoOut;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.dto.UserShortDto;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(CommentDtoIn commentInDto, User user, Event event) {
        return Comment.builder()
                .text(commentInDto.getText())
                .event(event)
                .user(user)
                .build();
    }

    public static CommentDtoOut toCommentDto(Comment comment, EventShortOutDto event, UserShortDto user) {
        return CommentDtoOut.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(event)
                .user(user)
                .publishedOn(comment.getPublishedOn())
                .status(comment.getStatus())
                .build();
    }
}
