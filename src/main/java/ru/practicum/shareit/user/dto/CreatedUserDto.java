package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.validator.UniqueEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;

@Data
@Builder
public class CreatedUserDto {

    @NotNull(message = "не указано имя")
    private final String name;

    @NotNull(message = "не указан Email")
    @Email(message = "неправильный формат Email")
    @UniqueEmail
    private final String email;
}
