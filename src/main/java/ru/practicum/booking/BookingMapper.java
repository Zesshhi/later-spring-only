package ru.practicum.booking;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStartDate(), booking.getEndDate(), booking.getItemId(), booking.isApproved());
    }

    public static List<BookingDto> mapToBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookings.forEach(booking -> bookingDtos.add(mapToBookingDto(booking)));

        return bookingDtos;
    }

    public static Booking mapToNewBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStartDate(bookingDto.getStartDate());
        booking.setEndDate(bookingDto.getEndDate());
        booking.setItemId(bookingDto.getItemId());
        booking.setApproved(bookingDto.isApproved());

        return booking;
    }
}
