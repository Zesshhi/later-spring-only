package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsersReturnsList() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "first@email.com", "First"),
                new UserDto(2L, "second@email.com", "Second")
        );
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].email").value("second@email.com"));
    }

    @Test
    void saveNewUserReturnsSavedUser() throws Exception {
        UserDto request = new UserDto(null, "new@email.com", "New");
        UserDto response = new UserDto(10L, "new@email.com", "New");
        when(userService.saveUser(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void updateUserReturnsUpdatedUser() throws Exception {
        UserDto update = new UserDto(null, "updated@email.com", "Updated");
        UserDto response = new UserDto(5L, "updated@email.com", "Updated");
        when(userService.updateUser(eq(5L), any(UserDto.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    @Test
    void getUserReturnsUser() throws Exception {
        UserDto response = new UserDto(7L, "user@email.com", "User");
        when(userService.getUser(7L)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"));
    }

    @Test
    void deleteUserReturnsOk() throws Exception {
        doNothing().when(userService).deleteUser(3L);

        mockMvc.perform(delete("/users/{id}", 3L))
                .andExpect(status().isOk());
    }
}
