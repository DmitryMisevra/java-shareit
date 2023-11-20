package ru.practicum.shareit.item.repository;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item createItem(Item item);
    Optional<Item> updateItem(@NonNull Item item);
    Optional<Item> getItemById(long id);
    List<Item> getItemListByUserId(long userId);
    List<Item> searchItemsByText(@NonNull String text);
}
