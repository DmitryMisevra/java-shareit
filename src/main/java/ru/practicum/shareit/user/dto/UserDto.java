package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


/**
 * UserDto передается в http-ответе для всех методов UserController
 */

@Data
@Builder
public class UserDto {

    @Positive(message = "Id пользователя должен быть положительным числом")
    private final Long id;
    @NotNull(message = "не указано имя")
    private final String name;

    @NotNull(message = "не указан Email")
    @Email(message = "неправильный формат Email")
    private final String email;
}
