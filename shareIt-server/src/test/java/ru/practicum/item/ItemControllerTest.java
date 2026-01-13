package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.item.comment.dto.CommentCreateDto;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemResponseDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItemReturnsCreatedItem() throws Exception {
        ItemDto request = new ItemDto(null, "Drill", "Cordless", true, null);
        ItemDto response = new ItemDto(1L, "Drill", "Cordless", true, null);
        when(itemService.saveItem(any(ItemDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateItemReturnsUpdatedItem() throws Exception {
        ItemDto request = new ItemDto(null, "Drill", "Updated", true, null);
        ItemDto response = new ItemDto(2L, "Drill", "Updated", true, null);
        when(itemService.updateItem(eq(2L), any(ItemDto.class), eq(10L))).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", 2L)
                        .header(USER_HEADER, 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    void getItemByIdReturnsItem() throws Exception {
        ItemResponseDto response = new ItemResponseDto(3L, "Saw", "Sharp", true, null, null, null, List.of());
        when(itemService.getItem(eq(5L), eq(3L))).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", 3L)
                        .header(USER_HEADER, 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Saw"));
    }

    @Test
    void getItemsByOwnerReturnsList() throws Exception {
        ItemResponseDto response = new ItemResponseDto(4L, "Wrench", "Steel", true, null, null, null, List.of());
        when(itemService.getItemsByOwner(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4L));
    }

    @Test
    void searchItemsReturnsList() throws Exception {
        ItemDto response = new ItemDto(5L, "Hammer", "Metal", true, null);
        when(itemService.searchItems("ham")).thenReturn(List.of(response));

        mockMvc.perform(get("/items/search")
                        .param("text", "ham"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hammer"));
    }

    @Test
    void addCommentReturnsComment() throws Exception {
        CommentCreateDto request = new CommentCreateDto();
        request.setText("Great item");
        CommentDto response = new CommentDto(10L, "Great item", "Alex", "2024-01-01T10:00:00");
        when(itemService.createComment(eq(7L), eq(6L), any(CommentCreateDto.class))).thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", 6L)
                        .header(USER_HEADER, 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.authorName").value("Alex"));
    }
}
