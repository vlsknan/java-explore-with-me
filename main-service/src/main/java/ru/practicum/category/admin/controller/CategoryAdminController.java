package ru.practicum.category.admin.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.admin.service.CategoryAdminService;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/admin/categories")
@Validated
public class CategoryAdminController {
    final CategoryAdminService service;

    //Изменение категории
    @PatchMapping
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto category) {
        log.info("Изменить категорию с id = {} (CategoryAdminController)", category.getId());
        return service.update(category);
    }

    //Добавление новой категории
    @PostMapping
    public CategoryDto saveCategory(@Valid @RequestBody NewCategoryDto category) {
        log.info("Добавить категорию с именем {} (CategoryAdminController)", category.getName());
        return service.save(category);
    }

    //Удаление категории
    @DeleteMapping("/{catId}")
    public ResponseEntity<HttpStatus> deleteCategoryById(@PathVariable int catId) {
        service.delete(catId);
        log.info("Удалить категорию с id = {} (CategoryAdminController)", catId);
        return ResponseEntity.ok().build();
    }
}
