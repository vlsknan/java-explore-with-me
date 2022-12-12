package ru.practicum.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.enums.Status;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "text")
    String text; //Текст комментария
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @Column(name = "created")
    @CreationTimestamp
    LocalDateTime createdOn; //Дата создания комментария
    @OneToOne
    @JoinColumn(name = "user_id")
    User user; //Пользователь оставивший комментарий
    @Enumerated(EnumType.STRING)
    Status status; //Статус комментария
    @Column(name = "published")
    LocalDateTime publishedOn;
}
