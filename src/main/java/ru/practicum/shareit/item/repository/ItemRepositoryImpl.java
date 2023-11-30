package ru.practicum.shareit.item.repository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private long counter = 1L;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> itemOwners = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        final Item createdItem = item.copyOf();
        createdItem.setId(counter);
        items.put(createdItem.getId(), createdItem);
        updateOwnerList(createdItem);
        counter++;
        return items.get(createdItem.getId());
    }

    @Override
    public Optional<Item> updateItem(@NonNull Item item) {
        return Optional.ofNullable(items.computeIfPresent(item.getId(), (existingId, existingItem) -> {
            existingItem.updateWith(item);
            return existingItem.copyOf();
        }));
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id))
                .map(Item::copyOf);
    }

    @Override
    public List<Item> getItemListByUserId(long userId) {
        return itemOwners.getOrDefault(userId, Collections.emptySet())
                .stream()
                .map(items::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemsByText(@NonNull String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    private void updateOwnerList(Item item) {
        Set<Long> ownerSet = itemOwners.computeIfAbsent(item.getOwnerId(), k -> new HashSet<>());
        ownerSet.add(item.getId());
    }
}
