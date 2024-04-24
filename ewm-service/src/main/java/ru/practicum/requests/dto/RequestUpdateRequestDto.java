package ru.practicum.requests.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.enums.Status;

import java.util.List;

@Data
@Builder
public class RequestUpdateRequestDto {
    private List<Long> requestIds;
    private Status status;
}
