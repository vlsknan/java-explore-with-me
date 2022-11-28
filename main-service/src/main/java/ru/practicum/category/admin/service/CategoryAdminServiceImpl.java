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
import ru.practicum.exception.model.CategoryNotFoundException;
import ru.practicum.exception.model.ConflictException;

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
        checkNameInRepository(updateCategory.getName());

        Category oldCategory = getCategoryById(updateCategory.getId());
        oldCategory.setName(updateCategory.getName());
        repository.save(oldCategory);
        log.info("Название категории с id = {} изменено на {}", oldCategory.getId(), oldCategory.getName());
        return CategoryMapper.toCategoryDto(oldCategory);
    }

    @Override
    @Transactional
    public CategoryDto save(NewCategoryDto newCategory) {
        checkNameInRepository(newCategory.getName());
        Category category = repository.save(CategoryMapper.toCategory(newCategory));
        log.info("Добавлена категория с названием {}", newCategory.getName());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void delete(int catId) {
        Category category = getCategoryById(catId);
        repository.delete(category);
        log.info("Категория с id = {} удалена", catId);
    }

    private void checkNameInRepository(String name) {
        if (repository.existsByName(name)) {
            throw new ConflictException(String.format("Category name = '%s' already exists ", name));
        }
    }

    private Category getCategoryById(int catId) {
        return repository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));
    }
}
