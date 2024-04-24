package ru.practicum.events.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.categories.model.Category;
import ru.practicum.enums.State;
import ru.practicum.location.model.Location;
import ru.practicum.users.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 20, max = 2000)
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @Size(min = 3, max = 120)
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "confirmed_request")
    private Long confirmedRequests;
    @Column(name = "created_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @Column(name = "published_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Size(min = 20, max = 7000)
    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    @Column(name = "paid")
    private Boolean paid = false;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "participant_limit")
    private Long participantLimit;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    private State state;
    @Column(name = "views")
    private Long views;
}
