package ru.practicum.requests.service;

import ru.practicum.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getUserRequests(Long userId);

    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

}
