package ru.practicum.shareit.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    CreatedUserDto createdUserDto = CreatedUserDto.builder()
            .name("user")
            .email("user@user.com")
            .build();

    UpdatedUserDto updatedUserDto = UpdatedUserDto.builder()
            .name("update")
            .email("update@user.com")
            .build();

    UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();

    UserDto userDtoUpd = UserDto.builder()
            .id(1L)
            .name("update")
            .email("update@user.com")
            .build();

    @SneakyThrows
    @Test
    void addUser_whenCreateUser_thenResponseStatusOkWithUserInBody() {
        when(userService.createUser(any(CreatedUserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));
    }

    @SneakyThrows
    @Test
    void updateUser_whenUpdateUser_thenResponseStatusOkWithUpdatedUserInBody() {
        Long validUserId = 1L;
        when(userService.updateUser(anyLong(), any(UpdatedUserDto.class)))
                .thenReturn(userDtoUpd);

        mockMvc.perform(patch("/users/{id}", validUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoUpd.getId()))
                .andExpect(jsonPath("$.name").value(userDtoUpd.getName()));
    }

    @SneakyThrows
    @Test
    void getUserById_whenGetUserById_thenResponseStatusOkWithUserInBody() {
        Long validUserId = 1L;
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", validUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));
    }

    @SneakyThrows
    @Test
    void getUserList_whenGetUserList_thenReturnStatusOkWithUserListInBody() {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@user.com")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user2r@user.com")
                .build();

        List<UserDto> userList = Arrays.asList(userDto1, userDto2);

        when(userService.getUserList())
                .thenReturn(userList);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(userList.size()))
                .andExpect(jsonPath("$[0].id").value(userDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(userDto2.getId()));
    }
}


