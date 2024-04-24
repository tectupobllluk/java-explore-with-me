package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.StateAction;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequestDto {
    @Size(min = 20, max = 2000, message = "Annotation must be more than 20 and less than 2000")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "Description must be more than 20 and less than 7000")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Long participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    @Size(min = 3, max = 120, message = "Title must be more than 3 and less than 120")
    private String title;
}
