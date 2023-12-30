package ru.practicum.shareit.item.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findItemsByOwnerIdOrderByIdAsc_WhenFindItemsByOwnerId_thenReturnsItems() {
        long ownerId = 1L;

        Item item1 = Item.builder()
                .name("Test name1")
                .description("Test description1")
                .ownerId(1L)
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Test name2")
                .description("Test description2")
                .ownerId(1L)
                .available(true)
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);

        entityManager.flush();

        List<Item> foundItems = itemRepository.findItemsByOwnerIdOrderByIdAsc(ownerId);

        assertFalse(foundItems.isEmpty(), "Список не должен быть пустым");
        assertEquals(2, foundItems.size(), "Количество найденных элементов должно быть 2");
        assertTrue(foundItems.stream().allMatch(item -> item.getOwnerId() == ownerId),
                "Все найденные элементы должны принадлежать владельцу с ID " + ownerId);
    }

    @Test
    public void searchItemsByText_WhenSearchByValidText_thenReturnsMatchingItems() {
        String searchText = "test";

        Item item1 = Item.builder()
                .name("Test name1")
                .description("Some description")
                .ownerId(1L)
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Other name")
                .description("Contains test text")
                .ownerId(1L)
                .available(true)
                .build();

        Item item3 = Item.builder()
                .name("Different name")
                .description("No matching text")
                .ownerId(1L)
                .available(true)
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);

        entityManager.flush();

        List<Item> foundItems = itemRepository.searchItemsByText(searchText);

        assertFalse(foundItems.isEmpty(), "Список не должен быть пустым");
        assertEquals(2, foundItems.size(), "Количество найденных элементов должно быть 2");
        assertTrue(foundItems.contains(item1), "Список должен содержать item1");
        assertTrue(foundItems.contains(item2), "Список должен содержать item2");
        assertFalse(foundItems.contains(item3), "Список не должен содержать item3");
    }

    @Test
    public void searchItemsByText_WhenSearchByValidTextWithPagination_thenReturnsMatchingItems() {
        String searchText = "test";
        int page = 0;
        int size = 2;

        for (int i = 0; i < 5; i++) {
            Item item = Item.builder()
                    .name("Test name " + i)
                    .description("Description " + i)
                    .ownerId(1L)
                    .available(true)
                    .build();
            entityManager.persist(item);
        }

        entityManager.flush();

        Pageable pageable = PageRequest.of(page, size);
        Page<Item> foundItemsPage = itemRepository.searchItemsByText(searchText, pageable);

        List<Item> foundItems = foundItemsPage.getContent();

        assertFalse(foundItems.isEmpty(), "Список не должен быть пустым");
        assertEquals(size, foundItems.size(),
                "Количество найденных элементов должно соответствовать размеру страницы");
        assertTrue(foundItemsPage.getTotalPages() > 1,
                "Общее количество страниц должно быть больше 1");
        assertTrue(foundItemsPage.getTotalElements() > size,
                "Общее количество найденных элементов должно быть больше размера страницы");
    }
}
