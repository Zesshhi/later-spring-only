package ru.practicum.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long itemId;
    private boolean isApproved;
}
