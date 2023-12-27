package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    User user1;
    User user2;
    Item item1;
    Item item2;
    Item item3;
    Item item4;

    Booking pastBooking;
    Booking futureBooking;

    @BeforeAll
    void beforeAll() {
        user1 = User.builder()
                .name("user1")
                .email("user1@user.com")
                .build();

        user2 = User.builder()
                .name("user2")
                .email("user2@user.com")
                .build();

        item1 = Item.builder()
                .name("item1")
                .description("item1 description")
                .ownerId(1L)
                .available(true)
                .build();

        item2 = Item.builder()
                .name("item2")
                .description("item2 description")
                .ownerId(2L)
                .available(true)
                .build();

        item3 = Item.builder()
                .name("item3")
                .description("item1 description")
                .ownerId(2L)
                .available(true)
                .build();

        item4 = Item.builder()
                .name("item4")
                .description("item4 description")
                .ownerId(2L)
                .available(true)
                .build();

        pastBooking = Booking.builder()
                .item(item1)
                .booker(user2)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(3))
                .status(Status.APPROVED)
                .build();

        futureBooking = Booking.builder()
                .item(item1)
                .booker(user2)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(Status.APPROVED)
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);
        item4 = itemRepository.save(item4);

        pastBooking = bookingRepository.save(pastBooking);
        futureBooking = bookingRepository.save(futureBooking);
    }

    @Test
    void getItemListByUserId_withoutPagination_returnsItemList() {
        long userId = 1L;
        Long from = null;
        Long size = null;

        List<ItemDto> result = itemService.getItemListByUserId(userId, from, size);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(1));
        result.forEach(itemDto -> {
            assertThat(itemDto.getLastBooking(), hasProperty("id", equalTo(1L)));
            assertThat(itemDto.getNextBooking(), hasProperty("id", equalTo(2L)));
        });
    }

    @Test
    void getItemListByUserId_withPagination_returnsItemList() {
        long userId = 2L;
        Long from = 0L;
        Long size = 2L;

        List<ItemDto> result = itemService.getItemListByUserId(userId, from, size);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(2));
    }

    @Test
    void getItemListByUserId_whenUserNotFound_returnsEmptyListOrThrowsException() {
        long invalidUserId = -1L;
        Long from = null;
        Long size = null;

        List<ItemDto> result = itemService.getItemListByUserId(invalidUserId, from, size);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(empty()));
    }
}
