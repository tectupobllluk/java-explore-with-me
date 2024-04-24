package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.enums.State;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public static Event toEvent(NewEventDto eventNewDto) {
        return Event.builder()
                .annotation(eventNewDto.getAnnotation())
                .title(eventNewDto.getTitle())
                .description(eventNewDto.getDescription())
                .eventDate(eventNewDto.getEventDate())
                .paid(eventNewDto.getPaid())
                .participantLimit(eventNewDto.getParticipantLimit())
                .requestModeration(eventNewDto.getRequestModeration())
                .createdOn(LocalDateTime.now())
                .views(0L)
                .state(State.PENDING)
                .confirmedRequests(0L)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .title(event.getTitle())
                .description(event.getDescription())
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .participantLimit(event.getParticipantLimit())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .title(event.getTitle())
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

}
