package ru.practicum.category.admin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryAdminServiceImpl implements CategoryAdminService {
    final CategoryRepository repository;

    @Override
    @Transactional
    public CategoryDto update(CategoryDto updateCategory) {
        if (repository.existsByName(updateCategory.getName())) {
            throw new ConflictException(String.format("Category name '%s' already exists", updateCategory.getName()));
        }
        Category oldCategory = repository.findById(updateCategory.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found.", updateCategory.getId())));
        oldCategory.setName(updateCategory.getName());
        log.info("Название категории с id = {} изменено на {}", oldCategory.getId(), oldCategory.getName());
        return CategoryMapper.toCategoryDto(oldCategory);
    }

    @Override
    @Transactional
    public CategoryDto save(NewCategoryDto newCategory) {
        if (repository.existsByName(newCategory.getName())) {
            throw new ConflictException(String.format("Category name '%s' already exists", newCategory.getName()));
        }
        Category category = CategoryMapper.toCategory(newCategory);
        log.info("Добавлена категория с названием {}", newCategory.getName());
        return CategoryMapper.toCategoryDto(repository.save(category));
    }

    @Override
    @Transactional
    public void delete(int catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found.", catId)));
        repository.delete(category);
        log.info("Категория с id = {} удалена", catId);
    }
}
