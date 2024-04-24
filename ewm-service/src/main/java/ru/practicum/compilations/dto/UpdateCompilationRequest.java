package ru.practicum.compilations.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "Title size is out of bounds")
    private String title;
}
