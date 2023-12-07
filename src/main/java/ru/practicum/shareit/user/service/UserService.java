package ru.practicum.shareit.user.service;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    @NonNull
    UserDto createUser(@NonNull CreatedUserDto createdUserDto);

    UserDto updateUser(@NonNull long id, UpdatedUserDto updatedUserDto);

    UserDto getUserById(long id);

    @NonNull
    void removeUserById(long id);

    @NonNull
    List<UserDto> getUserList();
}
