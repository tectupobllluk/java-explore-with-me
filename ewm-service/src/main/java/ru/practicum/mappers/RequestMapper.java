package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;

@UtilityClass
public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

}
