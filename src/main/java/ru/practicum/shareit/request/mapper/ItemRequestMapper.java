package ru.practicum.shareit.request.mapper;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemRequestMapper {
    public ItemRequest createdItemRequestDtoToItemRequest(@NonNull CreatedItemRequestDto createdItemRequestDto) {
        return ItemRequest.builder()
                .description(createdItemRequestDto.getDescription())
                .build();
    }

    public ItemRequestDto itemRequestToItemRequestDto(@NonNull ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems())
                .build();
    }
}
