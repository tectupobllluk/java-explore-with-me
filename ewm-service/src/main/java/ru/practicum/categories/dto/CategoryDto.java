package ru.practicum.categories.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class CategoryDto {
    private Long id;
    @Size(min = 1, max = 50, message = "Name size must be more than 1 and less than 50")
    @NotBlank
    private String name;
}
