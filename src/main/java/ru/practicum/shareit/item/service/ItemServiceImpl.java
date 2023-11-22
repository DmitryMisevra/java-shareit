package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;


    @Override
    @NonNull
    public ItemDto createItem(@NonNull long ownerId, CreatedItemDto createdItemDto) {
        Item item = Optional.ofNullable(itemMapper.createdItemDtoToItem(createdItemDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации itemDto->Item. Метод вернул null."));
        item.setOwnerId(ownerId);
        userService.getUserById(item.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
        return itemMapper.itemToItemDto(itemRepository.createItem(item));
    }

    @Override
    public Optional<ItemDto> updateItem(@NonNull long ownerId, long itemId, UpdatedItemDto updatedItemDto) {
        Item item = Optional.ofNullable(itemMapper.updatedItemDtoToItem(updatedItemDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации itemDto->Item. Метод вернул null."));
        item.setId(itemId);
        item.setOwnerId(ownerId);
        userService.getUserById(item.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
        return getItemById(item.getId()).flatMap(savedItem -> {
            if (!Objects.equals(savedItem.getOwnerId(), item.getOwnerId())) {
                throw new ForbiddenUserException("Данные о вещи может обновлять только владелец");
            }
            return itemRepository.updateItem(item).map(itemMapper::itemToItemDto);
        });
    }

    @Override
    public Optional<ItemDto> getItemById(long id) {
        return itemRepository.getItemById(id).map(itemMapper::itemToItemDto);
    }

    @Override
    public List<ItemDto> getItemListByUserId(long userId) {
        return itemRepository.getItemListByUserId(userId).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(@NonNull String text) {
        return itemRepository.searchItemsByText(text.toLowerCase()).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }
}
