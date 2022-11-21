package ru.practicum.event.client.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatDto {
    String app; //Название сервиса
    String uri; //URI сервиса
    int hits; //Количество просмотров
}
