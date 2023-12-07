package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    @NonNull
    public ItemDto createItem(@NonNull long ownerId, CreatedItemDto createdItemDto) {
        userService.getUserById(ownerId);
        Item item = Optional.ofNullable(itemMapper.createdItemDtoToItem(createdItemDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации itemDto->Item. Метод вернул null."));
        item.setOwnerId(ownerId);
        return Optional.ofNullable(itemMapper.itemToItemDto(itemRepository.save(item))).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации Item->ItemDto. Метод вернул null."));
    }

    @Transactional
    @Override
    public ItemDto updateItem(@NonNull long ownerId, long itemId, UpdatedItemDto updatedItemDto) {
        userService.getUserById(ownerId);
        Item updateditem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с таким id не найдена"));
        if (updateditem.getOwnerId() != ownerId) {
            throw new ForbiddenUserException("Данные о вещи может обновлять только владелец");
        }
        Item item = Optional.ofNullable(itemMapper.updatedItemDtoToItem(updatedItemDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации itemDto->Item. Метод вернул null."));
        updateditem.updateWith(item);
        return Optional.ofNullable(itemMapper.itemToItemDto(itemRepository.save(updateditem))).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации Item->ItemDto. Метод вернул null."));
    }

    @Override
    public ItemDto getItemById(long id) {
        Item foundedItem = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        return Optional.ofNullable(itemMapper.itemToItemDto(foundedItem)).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации Item->ItemDto. Метод вернул null."));
    }

    @Override
    public List<ItemDto> getItemListByUserId(long userId) {
        return itemRepository.findItemsByOwnerId(userId).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(@NonNull String text) {
        List<Item> foundedItems = itemRepository.searchItemsByText(text);
        return itemRepository.searchItemsByText(text).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }
}
