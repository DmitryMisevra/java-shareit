package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    CreatedUserDto createdUserDto = CreatedUserDto.builder()
            .name("user")
            .email("user@user.com")
            .build();

    User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();

    UpdatedUserDto updatedUserDto = UpdatedUserDto.builder()
            .name("update")
            .email("update@user.com")
            .build();

    User updatedUser = User.builder()
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


    @Test
    void createUser_whenValidCreatedUserDto_thenValidReturnUserDto() {
        User createduser = User.builder()
                .name("user")
                .email("user@user.com")
                .build();

        when(userMapper.createdUserDtoToUser(any(CreatedUserDto.class)))
                .thenReturn(createduser);
        when(userMapper.userToUserDto(any(User.class)))
                .thenReturn(userDto);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.createUser(createdUserDto);

        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_whenEmailAlreadyExists_thenThrowsEmailAlreadyExistsException() {
        when(userMapper.createdUserDtoToUser(any(CreatedUserDto.class)))
                .thenReturn(user);
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(createdUserDto));
    }

    @Test
    void updateUser_whenUserUpdated_thenReturnsUpdatedUserDto() {
        long userId = 1L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userMapper.updatedUserDtoToUser(any(UpdatedUserDto.class)))
                .thenReturn(updatedUser);
        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);
        when(userMapper.userToUserDto(any(User.class)))
                .thenReturn(userDtoUpd);

        UserDto result = userService.updateUser(userId, updatedUserDto);

        assertNotNull(result);
        assertEquals(userDtoUpd, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowsNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updatedUserDto));
    }

    @Test
    void updateUser_whenSaveThrowsDataIntegrityViolationException_thenThrowsEmailAlreadyExistsException() {
        long userId = 1L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userMapper.updatedUserDtoToUser(any(UpdatedUserDto.class)))
                .thenReturn(updatedUser);
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(userId, updatedUserDto));
    }

    @Test
    void getUserById_whenGetUserById_thenReturnsUserDto() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userDto, result);
    }


    @Test
    void getUserById_whenGetUserByIdAndUserNotFound_thenThrowsNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void removeUserById_whenRemoveUserById_thenUserIsDeleted() {
        long userId = 1L;

        userService.removeUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void whenGetUserList_thenReturnsListOfUserDtos() {
        User user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@user.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2r@user.com")
                .build();
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

        List<User> users = Arrays.asList(user1, user2);
        List<UserDto> userDtos = Arrays.asList(userDto1, userDto2);

        when(userRepository.findAll()).thenReturn(users);
        for (int i = 0; i < users.size(); i++) {
            when(userMapper.userToUserDto(users.get(i))).thenReturn(userDtos.get(i));
        }

        List<UserDto> result = userService.getUserList();

        assertNotNull(result);
        assertEquals(userDtos.size(), result.size());
        for (int i = 0; i < userDtos.size(); i++) {
            assertEquals(userDtos.get(i), result.get(i));
        }
    }
}
