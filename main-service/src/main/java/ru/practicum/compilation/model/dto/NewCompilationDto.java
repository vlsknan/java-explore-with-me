package ru.practicum.compilation.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    String title; //Заголовок подборки
    Boolean pinned; //Закреплена ли подборка на главной странице сайта
    List<Integer> events; //Список идентификаторов событий входящих в подборку
}
