package ru.practicum.shareit.request.service;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User userWithRequests;
    User userWithNoRequests;

    Item itemWithRequestId1;
    Item itemWithRequestId2;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;


    @BeforeEach
    void beforeEach() {
        userWithRequests = User.builder()
                .name("user1")
                .email("user1@user.com")
                .build();

        userWithNoRequests = User.builder()
                .name("user2")
                .email("user2@user.com")
                .build();

        userWithRequests = userRepository.save(userWithRequests);
        userWithNoRequests = userRepository.save(userWithNoRequests);

        itemRequest1 = ItemRequest.builder()
                .description("Test description")
                .created(LocalDateTime.now())
                .requestor(userWithRequests)
                .build();

        itemRequest2 = ItemRequest.builder()
                .description("Test description")
                .created(LocalDateTime.now())
                .requestor(userWithRequests)
                .build();

        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequest2 = itemRequestRepository.save(itemRequest2);

        itemWithRequestId1 = Item.builder()
                .name("item1")
                .description("item1 description")
                .ownerId(userWithRequests.getId())
                .available(true)
                .requestId(itemRequest1.getId())
                .build();

        itemWithRequestId2 = Item.builder()
                .name("item1")
                .description("item2 description")
                .ownerId(userWithRequests.getId())
                .available(true)
                .requestId(itemRequest1.getId())
                .build();

        itemWithRequestId1 = itemRepository.save(itemWithRequestId1);
        itemWithRequestId2 = itemRepository.save(itemWithRequestId2);
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getItemRequestListByUserId_whenUserExists_andHasRequests_thenReturnItemRequestDtoList() {
        long ownerId = userWithRequests.getId();

        List<ItemRequestDto> result = itemRequestService.getItemRequestListByUserId(ownerId);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(2));
    }

    @Test
    void getItemRequestListByUserId_whenUserNotFound_thenReturnThrowNotFoundException() {
        long invalidUserId = -1L;

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestListByUserId(invalidUserId));
    }

    @Test
    void getItemRequestListByUserId_whenUserExists_butNoRequests_thenReturnEmptyList() {
        long userId = userWithNoRequests.getId();

        List<ItemRequestDto> result = itemRequestService.getItemRequestListByUserId(userId);

        assertThat(result, is(empty()));
    }

    @Test
    void getAllItemRequestList_whenUserExists_thenReturnItemRequestDtoList() {
        long userId = userWithNoRequests.getId();

        List<ItemRequestDto> result = itemRequestService.getAllItemRequestList(userId, null, null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(2));
    }

    @Test
    void getAllItemRequestList_whenUserNotFound_thenThrowNotFoundException() {
        long invalidUserId = -1L;

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequestList(invalidUserId, null, null));
    }

    @Test
    void getAllItemRequestList_withPagination_thenReturnPaginatedResult() {
        long userId = userWithNoRequests.getId();
        Long from = 0L;
        Long size = 1L;

        List<ItemRequestDto> result = itemRequestService.getAllItemRequestList(userId, from, size);

        assertThat(result, hasSize(1));
    }

    @Test
    void getAllItemRequestList_whenNoMatchingRequests_thenReturnEmptyList() {
        long userId = userWithRequests.getId();

        List<ItemRequestDto> result = itemRequestService.getAllItemRequestList(userId, null, null);

        assertThat(result, is(empty()));
    }

    @Test
    void getRequestByItemRequestId_whenRequestAndUserExist_thenReturnItemRequestDto() {
        long userId = userWithRequests.getId();
        long requestId = itemRequest1.getId();

        ItemRequestDto result = itemRequestService.getRequestByItemRequestId(userId, requestId);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(equalTo(requestId)));
        assertThat(result.getItems(), hasSize(2));
        assertThat(result.getItems(), hasItem(hasProperty("id", is(itemWithRequestId1.getId()))));
        assertThat(result.getItems(), hasItem(hasProperty("id", is(itemWithRequestId2.getId()))));
    }

    @Test
    void getRequestByItemRequestId_whenUserNotFound_thenThrowNotFoundException() {
        long invalidUserId = -1L;
        long requestId = itemRequest1.getId();

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestByItemRequestId(invalidUserId, requestId));
    }

    @Test
    void getRequestByItemRequestId_whenRequestNotFound_thenThrowNotFoundException() {
        long userId = userWithRequests.getId();
        long invalidRequestId = -1L;

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestByItemRequestId(userId, invalidRequestId));
    }
}
