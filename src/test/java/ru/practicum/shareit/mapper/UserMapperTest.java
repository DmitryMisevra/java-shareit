package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void shouldMapCreatedUserDtoToUser() {
        CreatedUserDto dto = CreatedUserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();

        User user = userMapper.createdUserDtoToUser(dto);


        assertNull(user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    void shouldMapUserToCreatedUserDto() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        CreatedUserDto dto = userMapper.userToCreatedUserDto(user);

        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }



}
