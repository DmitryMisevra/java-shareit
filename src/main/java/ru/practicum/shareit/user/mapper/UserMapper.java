package ru.practicum.shareit.user.mapper;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


/**
 * UserMapper для маппинга Dto
 */

@Component
public class UserMapper {

    public User createdUserDtoToUser(@NonNull CreatedUserDto createdUserDto) {
        return User.builder()
                .name(createdUserDto.getName())
                .email(createdUserDto.getEmail())
                .build();
    }

    public User updatedUserDtoToUser(@NonNull UpdatedUserDto updatedUserDto) {
        return User.builder()
                .name(updatedUserDto.getName())
                .email(updatedUserDto.getEmail())
                .build();
    }

    public UserDto userToUserDto(@NonNull User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
