package ru.practicum.item;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.request.ItemRequest;
import ru.practicum.user.User;

@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private boolean available;
}