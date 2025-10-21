package ru.practicum.booking;

import lombok.Data;

import java.time.LocalDate;

@Data

public class Booking {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long itemId;
    private boolean isApproved;
}
