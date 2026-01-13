package ru.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.dto.BookingCreateDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBookingReturnsCreatedBooking() throws Exception {
        BookingCreateDto request = new BookingCreateDto(1L, 2L,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0));
        BookingResponseDto response = new BookingResponseDto(
                1L,
                "2024-01-01T10:00:00",
                "2024-01-02T10:00:00",
                BookingStatus.WAITING,
                new UserDto(3L, "booker@email.com", "Booker"),
                new ItemDto(2L, "Drill", "Cordless", true, null)
        );
        when(bookingService.createBooking(any(BookingCreateDto.class), eq(3L))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void updateBookingItemStatusReturnsBooking() throws Exception {
        BookingResponseDto response = new BookingResponseDto(
                5L,
                "2024-01-03T10:00:00",
                "2024-01-04T10:00:00",
                BookingStatus.APPROVED,
                new UserDto(4L, "booker@email.com", "Booker"),
                new ItemDto(7L, "Hammer", "Metal", true, null)
        );
        when(bookingService.updateBookingItemStatusByOwner(1L, 5L, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", 5L)
                        .header(USER_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingByIdReturnsBooking() throws Exception {
        BookingResponseDto response = new BookingResponseDto(
                8L,
                "2024-01-05T10:00:00",
                "2024-01-06T10:00:00",
                BookingStatus.WAITING,
                new UserDto(2L, "booker@email.com", "Booker"),
                new ItemDto(9L, "Saw", "Sharp", true, null)
        );
        when(bookingService.getBooking(2L, 8L)).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", 8L)
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L));
    }

    @Test
    void getAllBookingsReturnsList() throws Exception {
        BookingResponseDto response = new BookingResponseDto(
                11L,
                "2024-01-07T10:00:00",
                "2024-01-08T10:00:00",
                BookingStatus.WAITING,
                new UserDto(3L, "booker@email.com", "Booker"),
                new ItemDto(10L, "Wrench", "Steel", true, null)
        );
        when(bookingService.getBookingsByBooker(3L, "ALL")).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 3L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11L));
    }

    @Test
    void getAllOwnerBookingsReturnsList() throws Exception {
        BookingResponseDto response = new BookingResponseDto(
                12L,
                "2024-01-09T10:00:00",
                "2024-01-10T10:00:00",
                BookingStatus.WAITING,
                new UserDto(3L, "booker@email.com", "Booker"),
                new ItemDto(10L, "Wrench", "Steel", true, null)
        );
        when(bookingService.getBookingsByOwner(3L, "ALL")).thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 3L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12L));
    }
}
