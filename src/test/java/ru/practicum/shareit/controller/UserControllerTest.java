package ru.practicum.shareit.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    private CreatedUserDto getCreatedUserDto() {
        return CreatedUserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
    }


    @Test
    public void createUserTest() throws Exception {
        User user = getUser();
        CreatedUserDto createdUserDto = getCreatedUserDto();

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(createdUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getUserDto().getId())))
                .andExpect(jsonPath("$.name", is(getUserDto().getName())))
                .andExpect(jsonPath("$.email", is(getUserDto().getEmail())));
    }

    @Test
    public void updateUserTest() throws Exception {
        when(userMapper.userToUserDto(any(User.class))).thenReturn(getUserDto());

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(getCreatedUserDto()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getUserDto().getId())))
                .andExpect(jsonPath("$.name", is(getUserDto().getName())))
                .andExpect(jsonPath("$.email", is(getUserDto().getEmail())));
    }
}
