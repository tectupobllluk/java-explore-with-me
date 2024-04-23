package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.checks.EntityCheck;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final EntityCheck checkEntity;

    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        checkEntity.checkUser(userId);
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = checkEntity.getUserOrNotFound(userId);
        Event event = checkEntity.getEventOrNotFound(eventId);

        if (event.getParticipantLimit() != 0 &&
                Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
                throw new ConflictException(String.format("The limit of event - %s participation " +
                        "was overflowed", event));
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event creator cannot be its participant");
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException(String.format("Already participate of event: %s", event));
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException(
                    String.format("Event %s is not PUBLISHED", eventId));
        }
        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(Status.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED) + 1);
            eventRepository.save(event);
        }
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        checkEntity.checkUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Not found request with id: %s", requestId)));
        request.setStatus(Status.CANCELED);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}
