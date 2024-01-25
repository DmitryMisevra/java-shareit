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

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    /**
     * Добавление пользователя
     *
     * @param createdUserDto createdUserDto
     * @return UserDto
     */

    @PostMapping
    ResponseEntity<Object> createUser(@Valid @RequestBody CreatedUserDto createdUserDto) {
        log.debug("Добавлен новый пользователь {}", createdUserDto);
        return userClient.createUser(createdUserDto);
    }

    /**
     * Обновление пользователя
     *
     * @param id             id пользователя
     * @param updatedUserDto updatedUserDto
     * @return UserDto
     */

    @PatchMapping(path = "/{id}")
    ResponseEntity<Object> updateUser(@PathVariable long id,
                                       @Valid @RequestBody UpdatedUserDto updatedUserDto) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        log.debug("Обновлен пользователь {}", updatedUserDto);
        return userClient.updateUser(id, updatedUserDto);
    }

    /**
     * Поиск пользователя по id
     *
     * @param id id пользователя
     * @return UserDto
     */

    @GetMapping(path = "/{id}")
    ResponseEntity<Object> getUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        return userClient.getUserById(id);
    }

    /**
     * Удаление пользователя по id
     *
     * @param id id пользователя
     * @return ResponseEntity со строковым сообщением.
     */

    @DeleteMapping(path = "/{id}")
    ResponseEntity<Object> deleteUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        log.debug("фильм id={} удален", id);
        return userClient.removeUserById(id);
    }

    /**
     * Поиск пользователя по id
     *
     * @return List<UserDto>
     */

    @GetMapping
    ResponseEntity<Object> getUserList() {
        return userClient.getUserList();
    }
}
