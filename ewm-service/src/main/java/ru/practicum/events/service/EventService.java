package ru.practicum.events.service;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventRequestDto;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestUpdateRequestDto;
import ru.practicum.requests.dto.RequestUpdateResultDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto event);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateUserEvent(UpdateEventRequestDto eventUpdateDto, Long userId, Long eventId);

    List<RequestDto> getUserRequestsOnEvent(Long userId, Long eventId);

    RequestUpdateResultDto updateUserRequestsStatus(RequestUpdateRequestDto requestDto, Long eventId,
                                                    Long userId);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        LocalDateTime startTime, LocalDateTime endTime, Integer from, Integer size);

    EventFullDto updateEventByAdmin(UpdateEventRequestDto eventUpdateDto, Long eventId);

    List<EventShortDto> getEventList(String text, List<Long> categories, Boolean paid, LocalDateTime startTime,
                                  LocalDateTime endTime, Boolean onlyAvailable, String sort, Integer from,
                                  Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

}
