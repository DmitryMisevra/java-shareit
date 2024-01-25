package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

/**
 * UpdatedUserDto передается при обновлении пользователя
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedUserDto {

    @Positive(message = "Id пользователя должен быть положительным числом")
    private Long id;
    private String name;

    @Email(message = "неправильный формат Email")
    private String email;
}
