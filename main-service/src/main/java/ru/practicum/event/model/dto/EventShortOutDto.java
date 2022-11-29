package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.user.model.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortOutDto {
    int id;
    @NotNull
    String annotation; //Краткое описание
    @NotNull
    CategoryDto category;
    int confirmedRequests; //Количество одобренных заявок на участие в данном событии
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    UserShortDto initiator; //Пользователь (краткая информация)
    @NotNull
    boolean paid; //Нужно ли оплачивать участие
    @NotNull
    String title; //Заголовок
    int views;
}
