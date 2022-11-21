package ru.practicum.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.category.model.Category;
import ru.practicum.enums.StateEvent;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;
    String title;
    @Column(name = "created_on")
    @CreationTimestamp
    LocalDateTime createdOn;
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    boolean paid;
    @Column(name = "participant_limit")
    int participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    StateEvent state;
    @Column(name = "location_latitude")
    float locationLatitude;
    @Column(name = "location_longitude")
    float locationLongitude;
    @Column(name = "request_moderation")
    boolean requestModeration;
    int view;
}
