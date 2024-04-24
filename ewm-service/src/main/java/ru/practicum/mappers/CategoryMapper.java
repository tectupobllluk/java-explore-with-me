package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.dto.CategoryDto;

@UtilityClass
public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

}
