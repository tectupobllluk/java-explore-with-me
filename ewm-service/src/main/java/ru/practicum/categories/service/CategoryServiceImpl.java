package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.categories.model.Category;
import ru.practicum.checks.EntityCheck;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CategoryMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EntityCheck checkService;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto category) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(category)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        checkService.checkCategory(categoryId);
        if (!eventRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException(
                    String.format("Category with id - %s has events and cannot be deleted", categoryId));
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto category, Long catId) {
        Category updatedCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id - %s was not found", catId)));
        updatedCategory.setName(category.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(updatedCategory));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository
                .findAll(PageRequest.of(from / size, size)).toList();

        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Not found Category with id: %s", categoryId))));
    }
}
