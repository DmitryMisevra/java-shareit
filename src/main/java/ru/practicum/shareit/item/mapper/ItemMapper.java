package ru.practicum.shareit.item.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.model.Item;

/**
 * ItemMapper использует библиотеку MapStruct
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    Item createdItemDtoToItem(CreatedItemDto createdUItemDto);
    CreatedItemDto itemToCreatedItemDto(Item item);

    @Mapping(target = "ownerId", ignore = true)
    Item updatedItemDtoToItem(UpdatedItemDto updatedUItemDto);
    UpdatedItemDto itemToUpdatedItemDto(Item item);

    Item itemDtoToItem(ItemDto itemDto);
    ItemDto itemToItemDto(Item item);

}
