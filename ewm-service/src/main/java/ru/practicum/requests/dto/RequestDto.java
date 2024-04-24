package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.enums.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestDto {
    private Long id;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @NotNull
    private Long event;
    @NotNull
    private Long requester;
    @NotNull
    private Status status;
}
