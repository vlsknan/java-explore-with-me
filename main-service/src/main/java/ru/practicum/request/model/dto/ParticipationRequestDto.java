package ru.practicum.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.StatusRequest;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    int id; //Идентификатор заявки
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created; //Дата и время создания заявки
    int event; //Идентификатор события
    int requester; //Идентификатор пользователя, отправившего заявку
    @Enumerated(EnumType.STRING)
    StatusRequest status; //Статус заявки
}
