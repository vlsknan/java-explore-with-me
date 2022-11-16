package ru.practicum.compilation.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    @NotNull
    String title; //Заголовок подборки
    boolean pinned; //Закреплена ли подборка на главной странице сайта
    List<Integer> events; //Список идентификаторов событий входящих в подборку
}
