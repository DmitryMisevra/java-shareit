package ru.practicum.shareit.item.service;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    @NonNull
    Item createItem(@NonNull Item item);

    Optional<Item> updateItem(@NonNull Item item);
    Optional<Item> getItemById(long id);
    List<Item> getItemListByUserId(long userId);

    List<Item> searchItemsByText(@NonNull String text);
}
