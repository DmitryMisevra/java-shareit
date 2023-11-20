package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * itemDto передается в http-ответе для всех методов ItemController
 */

@Data
@Builder
public class ItemDto {

    @Positive(message = "Id вещи должен быть положительным числом")
    private final Long id;
    @NotBlank(message = "Имя вещи не может быть пустым")
    private final String name;
    @NotBlank(message = "описание вещи не может быть пустым")
    @Size(max = 200, message = "Размер описания не может превышать 200 символов")
    private final String description;
    @NotNull
    @Positive(message = "Id пользователя должен быть положительным числом")
    private final Long ownerId;
    @NotNull
    private final Boolean available;
}
