package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Annotation must be more than 20 and less than 2000")
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000, message = "Description must be more than 20 and less than 7000")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @FutureOrPresent
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    @Builder.Default
    private Boolean paid = false;
    @PositiveOrZero
    @Builder.Default
    private Long participantLimit = 0L;
    @Builder.Default
    private Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120, message = "Title must be more than 3 and less than 120")
    private String title;
}
