package ru.practicum.users.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    @EqualsAndHashCode.Exclude
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
