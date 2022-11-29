package ru.practicum.category.common.service;

import ru.practicum.category.model.dto.CategoryDto;

import java.util.List;

public interface CategoryCommonService {
    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(int catId);
}
