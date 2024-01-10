package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


/**
 * CreatedUserDto передается при создании пользователя
 */

@Data
@Builder
public class CreatedUserDto {

    @NotNull(message = "не указано имя")
    private final String name;

    @NotNull(message = "не указан Email")
    @Email(message = "неправильный формат Email")
    private final String email;
}
