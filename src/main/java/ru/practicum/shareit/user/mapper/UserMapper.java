package ru.practicum.shareit.user.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User createdUserDtoToUser(CreatedUserDto createdUserDto);
    CreatedUserDto userToCreatedUserDto(User user);

    User updatedUserDtoToUser(UpdatedUserDto updatedUserDto);
    UpdatedUserDto userToUpdatedUserDto(User user);

    User userDtoToUser(UserDto userDto);
    UserDto userToUserDto(User user);


}
