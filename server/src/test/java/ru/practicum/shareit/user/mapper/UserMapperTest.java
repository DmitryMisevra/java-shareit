package ru.practicum.shareit.user.mapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {


    private UserMapper mapper;
    private User user;
    private CreatedUserDto createdUserDto;
    private UpdatedUserDto updatedUserDto;

    @BeforeEach
    public void setUp() {
        mapper = new UserMapper();

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        createdUserDto = CreatedUserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();

        updatedUserDto = UpdatedUserDto.builder()
                .name("update")
                .email("update@user.com")
                .build();
    }

    @Test
    void createdUserDtoToUser_whenMappingValidCreatedUserDto_thenReturnUser() {
        User convertedUser = mapper.createdUserDtoToUser(createdUserDto);

        assertNotNull(convertedUser);
        assertEquals(createdUserDto.getName(), convertedUser.getName());
        assertEquals(createdUserDto.getEmail(), convertedUser.getEmail());
        assertNull(convertedUser.getId());
    }

    @Test
    void updatedUserDtoToUser_whenMappingValidUpdatedUserDto_thenReturnUser() {
        User convertedUser = mapper.updatedUserDtoToUser(updatedUserDto);

        assertNotNull(convertedUser);
        assertEquals(updatedUserDto.getName(), convertedUser.getName());
        assertEquals(updatedUserDto.getEmail(), convertedUser.getEmail());
        assertNull(convertedUser.getId());
    }

    @Test
    void userToUserDto_whenMappingValidUser_thenReturnUserDto() {
        UserDto convertedUserDto = mapper.userToUserDto(user);

        assertNotNull(convertedUserDto);
        assertEquals(user.getId(), convertedUserDto.getId());
        assertEquals(user.getName(), convertedUserDto.getName());
        assertEquals(user.getEmail(), convertedUserDto.getEmail());
    }
}
