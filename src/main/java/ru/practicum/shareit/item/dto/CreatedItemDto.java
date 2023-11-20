package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CreatedItemDto передается при создании вещи
 */

@Data
@Builder
public class CreatedItemDto {

    @NotBlank(message = "Имя вещи не может быть пустым")
    private final String name;
    @NotBlank(message = "описание вещи не может быть пустым")
    @Size(max = 200, message = "Размер описания не может превышать 200 символов")
    private final String description;
    @NotNull
    private final Boolean available;
}
