package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
public class UpdatedItemDto {

    @Positive(message = "Id вещи должен быть положительным числом")
    private Long id;
    private String name;
    @Size(max = 200, message = "Размер описания не может превышать 200 символов")
    private String description;
    private Boolean available;
}
