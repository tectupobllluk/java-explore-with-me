package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    @Builder.Default
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50, message = "Title must be more than 1 and less than 50")
    private String title;
}
