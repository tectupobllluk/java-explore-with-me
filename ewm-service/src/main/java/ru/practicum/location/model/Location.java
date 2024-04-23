package ru.practicum.location.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lat", nullable = false)
    @EqualsAndHashCode.Exclude
    private Float lat;
    @Column(name = "lon", nullable = false)
    @EqualsAndHashCode.Exclude
    private Float lon;
}
