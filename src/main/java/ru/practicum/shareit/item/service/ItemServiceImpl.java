package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;


    @Override
    @NonNull
    public Item createItem(@NonNull Item item) {
        userService.getUserById(item.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));

        return itemRepository.createItem(item);
    }

    @Override
    public Optional<Item> updateItem(@NonNull Item item) {
        userService.getUserById(item.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));

        return getItemById(item.getId()).flatMap(savedItem -> {
            if (!Objects.equals(savedItem.getOwnerId(), item.getOwnerId())) {
                throw new ForbiddenUserException("Данные о вещи может обновлять только владелец");
            }
            return itemRepository.updateItem(item);
        });
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public List<Item> getItemListByUserId(long userId) {
        return itemRepository.getItemListByUserId(userId);
    }

    @Override
    public List<Item> searchItemsByText(@NonNull String text) {
        return itemRepository.searchItemsByText(text.toLowerCase());
    }
}
