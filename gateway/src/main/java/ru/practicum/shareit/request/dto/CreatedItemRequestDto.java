package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * CreatedItemRequestDto передается при создании запроса
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedItemRequestDto {
    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 255, message = "Размер описания не может превышать 255 символов")
    private String description;
}
