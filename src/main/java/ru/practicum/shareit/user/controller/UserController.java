package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping
    ResponseEntity<UserDto> createUser(@Valid @RequestBody CreatedUserDto createdUserDto) {
        User user = Optional.ofNullable(userMapper.createdUserDtoToUser(createdUserDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        User createdUser = userService.createUser(user);
        log.debug("Добавлен новый пользователь с id={}", createdUser.getId());
        return ResponseEntity.ok(userMapper.userToUserDto(createdUser));
    }

    @PatchMapping(path = "/{id}")
    ResponseEntity<UserDto> updateUser(@PathVariable long id,
                                              @Valid @RequestBody UpdatedUserDto updatedUserDto) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        User user = Optional.ofNullable(userMapper.updatedUserDtoToUser(updatedUserDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        user.setId(id);
        User updatedUser = userService.updateUser(user)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.debug("Обновлен пользователь с id={}", updatedUser.getId());
        return ResponseEntity.ok(userMapper.userToUserDto(updatedUser));
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<UserDto> getUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        return userService.getUserById(id)
                .map(userMapper::userToUserDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + id));
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity<String> deleteUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        userService.removeUserById(id);
        log.debug("фильм id={} удален", id);
        return ResponseEntity.ok("Пользователь с id: " + id + " успешно удален");
    }

    @GetMapping
    ResponseEntity<List<UserDto>> getUserList() {
        List<UserDto> userDtoList = userService.getUserList()
                .stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtoList);
    }
}
