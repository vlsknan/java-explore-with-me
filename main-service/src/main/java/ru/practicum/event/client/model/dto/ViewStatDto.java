package ru.practicum.event.client.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatDto {
    String app;
    String uri;
    int hits;
}
