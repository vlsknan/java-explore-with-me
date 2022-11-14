package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {
    int id; //Идентификатор записи
    String app; //Идентификатор сервиса для которого записывается информация
    String uri; //URI для которого был осуществлен запрос
    String ip; //IP-адрес пользователя, осуществившего запрос
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp; //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
