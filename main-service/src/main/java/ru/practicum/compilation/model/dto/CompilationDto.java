package ru.practicum.compilation.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.dto.EventShortOutDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    @NotNull(message = "Id compilation cannot be null")
    int id;
    @NotNull(message = "Pinned cannot be null")
    boolean pinned; //Закреплена ли подборка на главной странице сайта
    @NotNull(message = "Title compilation cannot be null")
    String title; //Заголовок подборки
    List<EventShortOutDto> events; //Список событий входящих в подборку
}
