package ru.practicum.category.common.service;

import ru.practicum.category.model.dto.CategoryDto;

import java.util.List;

public interface CategoryCommonService {
    List<CategoryDto> findAll(int from, int size);
    CategoryDto findById(int catId);
}
