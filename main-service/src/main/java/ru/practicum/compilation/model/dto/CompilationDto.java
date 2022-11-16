package ru.practicum.compilation.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.dto.EventShortOutDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    @NotNull
    int id;
    @NotNull
    boolean penned; //Закреплена ли подборка на главной странице сайта
    @NotNull
    String title; //Заголовок подборки
    List<EventShortOutDto> events; //Список событий входящих в подборку
}
