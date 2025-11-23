package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.BookingInItemDto;
import ru.practicum.item.comment.dto.CommentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    BookingInItemDto lastBooking;
    BookingInItemDto nextBooking;
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<CommentDto> comments;
}
