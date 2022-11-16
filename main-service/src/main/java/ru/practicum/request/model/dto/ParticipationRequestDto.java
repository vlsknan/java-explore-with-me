package ru.practicum.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.State;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

//Заявка на участие в событии
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    int id; //Идентификатор заявки
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created; //Дата и время создания заявки
    int event; //Идентификатор события
    int requester; //Идентификатор пользователя, отправившего заявку
    @Enumerated(EnumType.STRING)
    State status; //Статус заявки
}
