package ru.practicum.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.StateEvent;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @ManyToOne
    @Column(name = "event_id")
    Event event;
    LocalDateTime created;
    @ManyToOne
    @Column(name = "requester_id")
    User requester;
    @Enumerated(EnumType.STRING)
    StatusRequest status;
}
