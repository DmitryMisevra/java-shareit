package ru.practicum.shareit.item.service;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    @NonNull
    ItemDto createItem(@NonNull long ownerId, CreatedItemDto createdItemDto);

    Optional<ItemDto> updateItem(@NonNull long ownerId, long itemId, UpdatedItemDto updatedItemDto);

    Optional<ItemDto> getItemById(long id);

    List<ItemDto> getItemListByUserId(long userId);

    List<ItemDto> searchItemsByText(@NonNull String text);
}
