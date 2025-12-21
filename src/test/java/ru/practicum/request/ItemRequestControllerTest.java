package ru.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_returnsCreatedRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(
                1L,
                "Need a drill",
                LocalDateTime.now(),
                Collections.emptyList()
        );

        when(itemRequestService.createRequest(eq(createDto), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/request")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void createRequest_returnsBadRequestWhenDescriptionBlank() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("");

        mockMvc.perform(post("/request")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_returnsList() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(
                1L,
                "Need a drill",
                LocalDateTime.now(),
                Collections.emptyList()
        );

        when(itemRequestService.getUserRequests(1L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/request").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOtherUsersRequests_returnsList() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(
                2L,
                "Need ladder",
                LocalDateTime.now(),
                Collections.emptyList()
        );

        when(itemRequestService.getOtherUsersRequests(1L, 0, 10)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void getRequestById_returnsSingle() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(
                3L,
                "Need something",
                LocalDateTime.now(),
                Collections.emptyList()
        );

        when(itemRequestService.getRequestById(1L, 3L)).thenReturn(responseDto);

        mockMvc.perform(get("/request/3").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }
}
