package ru.practicum.category.common.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.utility.PageUtility;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.model.CategoryNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CategoryCommonServiceImpl implements CategoryCommonService {
    final CategoryRepository repository;

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        PageRequest page = PageUtility.pagination(from, size);
        Page<Category> categories = repository.findAll(page);
        log.info("Получены все категории");
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(int catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));
        log.info("Получены данные о категории с id = {}", catId);
        return CategoryMapper.toCategoryDto(category);
    }
}
