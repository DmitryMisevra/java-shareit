package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    @NonNull
    public UserDto createUser(@NonNull CreatedUserDto createdUserDto) {
        User user = Optional.ofNullable(userMapper.createdUserDtoToUser(createdUserDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        return userMapper.userToUserDto(userRepository.createUser(user));
    }

    @Override
    public Optional<UserDto> updateUser(@NonNull long id, UpdatedUserDto updatedUserDto) {
        User user = Optional.ofNullable(userMapper.updatedUserDtoToUser(updatedUserDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        user.setId(id);
        if (isEmailUnique(user)) {
            return userRepository.updateUser(user).map(userMapper::userToUserDto);
        } else {
            throw new EmailAlreadyExistsException("Email найден у другого пользователя");
        }
    }

    @Override
    @NonNull
    public Optional<UserDto> getUserById(long id) {
        return userRepository.getUserById(id).map(userMapper::userToUserDto);
    }

    @Override
    public void removeUserById(long id) {
        userRepository.removeUserById(id);
    }

    @Override
    @NonNull
    public List<UserDto> getUserList() {
        return userRepository.getUserList().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    private boolean isEmailUnique(User user) {
        return getUserList().stream()
                .noneMatch(us -> us.getEmail().equalsIgnoreCase(user.getEmail()) && !us.getId().equals(user.getId()));
    }
}
