package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public RequestDto createRequest(@Positive @PathVariable Long userId,
                                    @Positive @RequestParam Long eventId) {
        log.info("Registered user with id - {} make request with id - {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@Positive @PathVariable Long userId) {
        log.info("Registered user with id - {} get requests", userId);
        return requestService.getUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long requestId) {
        log.info("Registered user with id - {} canceled request with id - {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
