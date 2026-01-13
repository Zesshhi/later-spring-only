package ru.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.dto.ItemRequestAnswerDto;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequestReturnsCreatedRequest() throws Exception {
        ItemRequestCreateDto request = new ItemRequestCreateDto("Need a drill");
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                1L,
                "Need a drill",
                LocalDateTime.of(2024, 1, 1, 10, 0),
                List.of()
        );
        when(itemRequestService.createRequest(any(ItemRequestCreateDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserRequestsReturnsList() throws Exception {
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                2L,
                "Need a bike",
                LocalDateTime.of(2024, 1, 2, 10, 0),
                List.of()
        );
        when(itemRequestService.getUserRequests(4L)).thenReturn(List.of(response));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Need a bike"));
    }

    @Test
    void getOtherUsersRequestsReturnsList() throws Exception {
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                3L,
                "Need a ladder",
                LocalDateTime.of(2024, 1, 3, 10, 0),
                List.of(new ItemRequestAnswerDto(8L, "Ladder", 2L, 3L))
        );
        when(itemRequestService.getOtherUsersRequests(5L, 0, 10)).thenReturn(List.of(response));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 5L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].items[0].name").value("Ladder"));
    }

    @Test
    void getRequestByIdReturnsRequest() throws Exception {
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                4L,
                "Need a saw",
                LocalDateTime.of(2024, 1, 4, 10, 0),
                List.of()
        );
        when(itemRequestService.getRequestById(6L, 4L)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", 4L)
                        .header(USER_HEADER, 6L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L));
    }
}
