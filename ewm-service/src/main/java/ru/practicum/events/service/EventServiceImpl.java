package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.StatsHttpClient;
import ru.practicum.categories.model.Category;
import ru.practicum.checks.EntityCheck;
import ru.practicum.dto.StatsDto;
import ru.practicum.enums.State;
import ru.practicum.enums.StateAction;
import ru.practicum.enums.Status;
import ru.practicum.events.model.Event;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventRequestDto;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DataValidationException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.LocationMapper;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestUpdateRequestDto;
import ru.practicum.requests.dto.RequestUpdateResultDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventsRepository;
    private final EntityCheck checkEntity;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsHttpClient statsClient;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        checkEntity.checkUser(userId);
        Pageable pageRequest = PageRequest.of(from / size, size);
        return eventsRepository.findByInitiatorId(userId, pageRequest).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DataValidationException("Should be date that has not yet occurred");
        }
        User user = checkEntity.getUserOrNotFound(userId);
        Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id - %s was not found", newEventDto.getCategory())));
        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setLocation(location);
        return EventMapper.toEventFullDto(eventsRepository.save(event));
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        checkEntity.checkUser(userId);
        checkEntity.checkEvent(eventId);
        return EventMapper.toEventFullDto(eventsRepository.findByInitiatorIdAndId(userId, eventId));
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(UpdateEventRequestDto eventUpdateDto, Long userId, Long eventId) {
        User user = checkEntity.getUserOrNotFound(userId);
        Event event = checkEntity.getEventOrNotFound(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Only event creator can update its event");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only PENDING or CANCELED events can be changed");
        }
        return EventMapper.toEventFullDto(updateEventFields(event, eventUpdateDto));
    }

    @Override
    public List<RequestDto> getUserRequestsOnEvent(Long userId, Long eventId) {
        User user = checkEntity.getUserOrNotFound(userId);
        Event event = checkEntity.getEventOrNotFound(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format(
                    "User with id: %s not creator of event id: %s",userId, eventId));
        }
        return requestRepository.findByEventId(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestUpdateResultDto updateUserRequestsStatus(RequestUpdateRequestDto requestDto, Long eventId, Long userId) {
        User user = checkEntity.getUserOrNotFound(userId);
        Event event = checkEntity.getEventOrNotFound(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Only event creator can update this event");
        }
        RequestUpdateResultDto result = RequestUpdateResultDto.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(Collections.emptyList())
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Event's participant limit is full");
        }

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        List<Request> requests = requestRepository.findAllById(requestDto.getRequestIds());

        for (Request request : requests) {
            if (requestDto.getStatus().equals(Status.CONFIRMED)) {
                if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
                    request.setStatus(Status.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toRequestDto(request));
                } else if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(Status.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    confirmedRequests.add(RequestMapper.toRequestDto(request));
                } else {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(RequestMapper.toRequestDto(request));
                }
            } else {
                request.setStatus(Status.REJECTED);
                rejectedRequests.add(RequestMapper.toRequestDto(request));
            }
        }

        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        eventsRepository.save(event);
        requestRepository.saveAll(requests);

        return result;
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states,
                                               List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new ConflictException("Start time can't be after End time");
            }
        }

        Pageable pageable = PageRequest.of(from / size, size);
        Specification<Event> spec = Specification.where(null);

        if (rangeStart != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        if (users != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }

        if (categories != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("category").in(categories));
        }

        return eventsRepository.findAll(spec, pageable).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(UpdateEventRequestDto eventUpdateDto, Long eventId) {
        Event event = checkEntity.getEventOrNotFound(eventId);

        if (eventUpdateDto.getEventDate() != null) {
            LocalDateTime actualEventTime = event.getEventDate();
            if (actualEventTime.plusHours(1).isAfter(eventUpdateDto.getEventDate()) ||
                    actualEventTime.plusHours(1) != eventUpdateDto.getEventDate())
                event.setEventDate(eventUpdateDto.getEventDate());

            else
                throw new ConflictException("Event date cannot be earlier than one hour from the date of publication");
        }

        if (eventUpdateDto.getStateAction() != null) {
            if (event.getState() == State.PENDING) {
                if (eventUpdateDto.getStateAction() == StateAction.PUBLISH_EVENT) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else if (eventUpdateDto.getStateAction() == StateAction.REJECT_EVENT) {
                    event.setState(State.CANCELED);
                }
            } else {
                throw new ConflictException(String.format(
                        "Cannot publish event not in PENDING state: %S", event.getState()));
            }
        }

        return EventMapper.toEventFullDto(updateEventFields(event, eventUpdateDto));
    }

    @Override
    public List<EventShortDto> getEventList(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime startTime, LocalDateTime endTime, Boolean onlyAvailable,
                                            String sort, Integer from, Integer size,
                                            HttpServletRequest request) {
        statsClient.createHit("ewm-service", request);
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (endTime != null && Objects.requireNonNull(startTime).isAfter(endTime)) {
            throw new DataValidationException("Start time can't be after End time");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventsRepository.findEventsByPublicFromParam(
                text, categories, paid, startTime, endTime, onlyAvailable, sort, pageRequest);

        for (Event event : events) {
            event.setViews(getEventViewsById(event));
        }
        eventsRepository.saveAll(events);

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        statsClient.createHit("ewm-service", request);
        Event event = checkEntity.getEventOrNotFound(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event must be PUBLISHED");
        }
        event.setViews(getEventViewsById(event));
        eventsRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    private Event updateEventFields(Event event, UpdateEventRequestDto eventUpdateDto) {
        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isBlank()) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isBlank()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(eventUpdateDto.getCategory()));
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(eventUpdateDto.getLocation()));
            locationRepository.save(event.getLocation());
        }
        if (eventUpdateDto.getEventDate() != null) {
            event.setEventDate(eventUpdateDto.getEventDate());
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }

        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventUpdateDto.getStateAction() == StateAction.REJECT_EVENT ||
                    eventUpdateDto.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            } else if (eventUpdateDto.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            }
        }
        return eventsRepository.save(event);
    }

    private Long getEventViewsById(Event event) {
        if (event.getId() != null) {
            List<StatsDto> eventRequests = statsClient.getStatistics(
                    LocalDateTime.now().minusYears(1),
                    LocalDateTime.now().plusYears(1),
                    List.of("/events/" + event.getId()),
                    true);

            if (!eventRequests.isEmpty())
                return eventRequests.get(0).getHits();
        }

        return 0L;
    }

}
