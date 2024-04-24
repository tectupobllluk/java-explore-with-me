package ru.practicum.compilations.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.events.dto.EventShortDto;

import java.util.Set;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private Set<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
