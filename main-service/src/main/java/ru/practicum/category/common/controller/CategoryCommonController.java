package ru.practicum.category.common.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.common.service.CategoryCommonService;
import ru.practicum.category.model.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCommonController {
    final CategoryCommonService service;

    //Получение категорий
    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Получить все категории (CategoryCommonController)");
        return service.getAll(from, size);
    }

    //Получение информации о категории по ее id
    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable int catId) {
        log.info("Получить данные о категориис id = {} (CategoryCommonController)", catId);
        return service.getById(catId);
    }
}
