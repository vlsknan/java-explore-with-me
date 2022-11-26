package ru.practicum.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStats {
    String app; //Название сервиса
    String uri; //URI сервиса
    int hits; //Количество просмотров
}
