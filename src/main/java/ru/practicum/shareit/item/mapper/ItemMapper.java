package ru.practicum.shareit.item.mapper;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.model.Item;

/**
 * ItemMapper для маппинга Dto
 */

@Component
public class ItemMapper {

    public Item createdItemDtoToItem(@NonNull CreatedItemDto createdUItemDto) {
        return Item.builder()
                .name(createdUItemDto.getName())
                .description(createdUItemDto.getDescription())
                .available(createdUItemDto.getAvailable())
                .requestId(createdUItemDto.getRequestId())
                .build();
    }

    public Item updatedItemDtoToItem(@NonNull UpdatedItemDto updatedUItemDto) {
        return Item.builder()
                .name(updatedUItemDto.getName())
                .description(updatedUItemDto.getDescription())
                .available(updatedUItemDto.getAvailable())
                .build();
    }

    public ItemDto itemToItemDto(@NonNull Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwnerId())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .lastBooking(item.getLastBooking())
                .nextBooking(item.getNextBooking())
                .comments(item.getComments())
                .build();
    }
}
