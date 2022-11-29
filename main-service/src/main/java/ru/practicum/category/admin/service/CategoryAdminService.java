package ru.practicum.category.admin.service;

import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;

public interface CategoryAdminService {
    CategoryDto update(CategoryDto category);

    CategoryDto save(NewCategoryDto category);

    void delete(int catId);
}
