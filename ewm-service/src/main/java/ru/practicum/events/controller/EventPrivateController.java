package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventRequestDto;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestUpdateRequestDto;
import ru.practicum.requests.dto.RequestUpdateResultDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        log.info("Registered user with id - {} create event - {}", userId, eventDto.getTitle());
        return eventService.createEvent(userId, eventDto);
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Registered user with id - {} get events from - {} size - {}", userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        log.info("Registered user with id - {} get event with id - {}", userId, eventId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@Valid @RequestBody UpdateEventRequestDto eventDto,
                                        @PathVariable Long userId,
                                        @PathVariable Long eventId) {
        log.info("Registered user with id - {} update event - {}", eventId, eventDto.getTitle());
        return eventService.updateUserEvent(eventDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getUserRequestsOnEvent(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        log.info("Registered user with id - {} get requests on event with id - {}", userId, eventId);
        return eventService.getUserRequestsOnEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateResultDto updateUserRequestsStatus(@PathVariable Long userId,
                                                           @PathVariable Long eventId,
                                                           @RequestBody RequestUpdateRequestDto requestDto) {
        log.info("Registered user with id - {} update status of " +
                "request on event with id - {}", userId, eventId);
        return eventService.updateUserRequestsStatus(requestDto, eventId, userId);
    }
}
