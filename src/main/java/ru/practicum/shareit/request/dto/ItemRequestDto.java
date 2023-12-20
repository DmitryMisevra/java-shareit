package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @Positive(message = "Id запроса должен быть положительным числом")
    private final Long id;
    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 255, message = "Размер описания не может превышать 255 символов")
    private final String description;
    @NotNull
    private final LocalDateTime created;
    private List<ItemDto> items;
}
