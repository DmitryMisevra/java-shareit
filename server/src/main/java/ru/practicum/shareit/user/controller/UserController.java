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
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdatedUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Добавление пользователя
     *
     * @param createdUserDto createdUserDto
     * @return UserDto
     */

    @PostMapping
    ResponseEntity<UserDto> createUser(@RequestBody CreatedUserDto createdUserDto) {
        UserDto createdUser = userService.createUser(createdUserDto);
        log.debug("Добавлен новый пользователь с id={}", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Обновление пользователя
     *
     * @param id             id пользователя
     * @param updatedUserDto updatedUserDto
     * @return UserDto
     */

    @PatchMapping(path = "/{id}")
    ResponseEntity<UserDto> updateUser(@PathVariable long id,
                                       @RequestBody UpdatedUserDto updatedUserDto) {
        UserDto updatedUser = userService.updateUser(id, updatedUserDto);
        log.debug("Обновлен пользователь с id={}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Поиск пользователя по id
     *
     * @param id id пользователя
     * @return UserDto
     */

    @GetMapping(path = "/{id}")
    ResponseEntity<UserDto> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Удаление пользователя по id
     *
     * @param id id пользователя
     * @return ResponseEntity со строковым сообщением.
     */

    @DeleteMapping(path = "/{id}")
    ResponseEntity<Map<String, String>> deleteUserById(@PathVariable long id) {
        userService.removeUserById(id);
        log.debug("фильм id={} удален", id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Пользователь с id: " + id + " успешно удален");
        return ResponseEntity.ok(response);
    }

    /**
     * Поиск пользователя по id
     *
     * @return List<UserDto>
     */

    @GetMapping
    ResponseEntity<List<UserDto>> getUserList() {
        return ResponseEntity.ok(userService.getUserList());
    }
}
