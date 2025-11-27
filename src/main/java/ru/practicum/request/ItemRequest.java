package ru.practicum.request;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    private String description;

    @Column
    private LocalDateTime created;
}
