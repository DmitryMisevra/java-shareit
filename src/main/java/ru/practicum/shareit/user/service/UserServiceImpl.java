package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional
    @Override
    @NonNull
    public UserDto createUser(@NonNull CreatedUserDto createdUserDto) {
        User user = Optional.ofNullable(userMapper.createdUserDtoToUser(createdUserDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        try {
            return userMapper.userToUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Пользователь с таким Email уже существует");
        }
    }

    @Transactional
    @Override
    public UserDto updateUser(@NonNull long id, UpdatedUserDto updatedUserDto) {
        User updatedUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        User user = Optional.ofNullable(userMapper.updatedUserDtoToUser(updatedUserDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        updatedUser.updateWith(user);
        try {
            return userMapper.userToUserDto(userRepository.save(updatedUser));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Пользователь с таким Email уже существует");
        }
    }

    @Override
    @NonNull
    public Optional<UserDto> getUserById(long id) {
        return userRepository.findById(id).map(userMapper::userToUserDto);
    }

    @Transactional
    @Override
    public void removeUserById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @NonNull
    public List<UserDto> getUserList() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }
}
