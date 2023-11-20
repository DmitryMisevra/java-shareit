package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.validator.UniqueEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UserDto {

    @Positive(message = "Id пользователя должен быть положительным числом")
    private final Long id;
    @NotNull(message = "не указано имя")
    private final String name;

    @NotNull(message = "не указан Email")
    @Email(message = "неправильный формат Email")
    @UniqueEmail
    private final String email;
}
