package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    User user1;
    User user2;
    User user3;
    Item item1;
    Item item2;
    Booking futureBooking1;
    Booking futureBooking2;
    Booking pastBooking1;
    Booking pastBooking2;
    Booking rejectedBooking1;
    Booking rejectedBooking2;
    Booking currentBooking1;
    Booking currentBooking2;

    @BeforeAll
    void setUp() {
        user1 = User.builder()
                .name("user1")
                .email("user1@user.com")
                .build();

        user2 = User.builder()
                .name("user2")
                .email("user2@user.com")
                .build();

        user3 = User.builder()
                .name("user3")
                .email("user3@user.com")
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        Item item1 = Item.builder()
                .name("Test name1")
                .description("Test description1")
                .ownerId(2L)
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Test name2")
                .description("Test description2")
                .ownerId(3L)
                .available(true)
                .build();

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        futureBooking1 = Booking.builder()
                .item(item1)
                .booker(user1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(Status.WAITING)
                .build();

        futureBooking2 = Booking.builder()
                .item(item2)
                .booker(user1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(Status.WAITING)
                .build();

        rejectedBooking1 = Booking.builder()
                .item(item1)
                .booker(user1)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(15))
                .status(Status.REJECTED)
                .build();

        rejectedBooking2 = Booking.builder()
                .item(item2)
                .booker(user1)
                .start(LocalDateTime.now().plusDays(15))
                .end(LocalDateTime.now().plusDays(20))
                .status(Status.REJECTED)
                .build();

        pastBooking1 = Booking.builder()
                .item(item1)
                .booker(user1)
                .start(LocalDateTime.now().minusDays(7))
                .end(LocalDateTime.now().minusDays(5))
                .status(Status.APPROVED)
                .build();

        pastBooking2 = Booking.builder()
                .item(item2)
                .booker(user1)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(3))
                .status(Status.APPROVED)
                .build();

        currentBooking1 = Booking.builder()
                .item(item1)
                .booker(user1)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .build();

        currentBooking2 = Booking.builder()
                .item(item2)
                .booker(user1)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(Status.APPROVED)
                .build();

        futureBooking1 = bookingRepository.save(futureBooking1);
        futureBooking2 = bookingRepository.save(futureBooking2);
        rejectedBooking1 = bookingRepository.save(rejectedBooking1);
        rejectedBooking2 = bookingRepository.save(rejectedBooking2);
        pastBooking1 = bookingRepository.save(pastBooking1);
        pastBooking2 = bookingRepository.save(pastBooking2);
        currentBooking1 = bookingRepository.save(currentBooking1);
        currentBooking2 = bookingRepository.save(currentBooking2);
    }

    @Test
    public void getBookingListCreatedByUserIdTest_whenStatusAll_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListCreatedByUserId(user1.getId(),
                "ALL",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(8));
    }

    @Test
    public void getBookingListCreatedByUserIdTest_whenStatusCURRENT_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListCreatedByUserId(user1.getId(),
                "CURRENT",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(2));
    }


    @Test
    public void getBookingListCreatedByUserIdTest_whenStatusFUTURE_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListCreatedByUserId(user1.getId(),
                "FUTURE",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(4));
    }

    @Test
    public void getBookingListCreatedByUserIdTest_whenStatusREJECTED_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListCreatedByUserId(user1.getId(),
                "REJECTED",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(2));
    }

    @Test
    public void getBookingListCreatedByUserIdTest_whenStatusWAITING_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListCreatedByUserId(user1.getId(),
                "REJECTED",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(2));
    }

    @Test
    public void getBookingListCreatedByUserIdTest_whenUserNotFound_thenThrowException() {
        long nonExistentUserId = 999L;

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingListCreatedByUserId(nonExistentUserId, "ALL", null, null);
        });

        String expectedMessage = "Пользователь с id: " + nonExistentUserId + " не найден";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getBookingListCreatedByUserIdTest_whenInvalidStatus_thenThrowException() {
        String invalidStatus = "INVALID_STATUS";

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.getBookingListCreatedByUserId(user1.getId(), invalidStatus, null, null);
        });

        String expectedMessage = "Unknown state: " + invalidStatus;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getBookingListCreatedByUserIdTest_withPagination_shouldReturnPagedResults() {
        List<BookingDto> firstPageResults = bookingService.getBookingListCreatedByUserId(user1.getId(),
                "ALL",
                0L,
                2L);
        List<BookingDto> secondPageResults = bookingService.getBookingListCreatedByUserId(user1.getId(),
                "ALL",
                2L,
                2L);

        assertThat(firstPageResults, hasSize(2));
        assertThat(secondPageResults, hasSize(2));

        assertThat(firstPageResults.get(0).getId(), not(equalTo(secondPageResults.get(0).getId())));
        assertThat(firstPageResults.get(1).getId(), not(equalTo(secondPageResults.get(1).getId())));
    }

    /// следующий метод

    @Test
    public void getBookingListForAllOwnerItems_whenStatusAll_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListForAllOwnerItems(user3.getId(),
                "ALL",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(4));
    }

    @Test
    public void getBookingListForAllOwnerItems_whenStatusCURRENT_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListForAllOwnerItems(user3.getId(),
                "CURRENT",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(1));
    }


    @Test
    public void getBookingListForAllOwnerItems_whenStatusFUTURE_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListForAllOwnerItems(user3.getId(),
                "FUTURE",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(2));
    }

    @Test
    public void getBookingListForAllOwnerItems_whenStatusREJECTED_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListForAllOwnerItems(user3.getId(),
                "REJECTED",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(1));
    }

    @Test
    public void getBookingListForAllOwnerItems_whenStatusWAITING_thenReturnCorrectBookingList() {
        List<BookingDto> result = bookingService.getBookingListForAllOwnerItems(user3.getId(),
                "REJECTED",
                null,
                null);

        assertThat(result, is(not(empty())));
        assertThat(result, hasSize(1));
    }

    @Test
    public void getBookingListForAllOwnerItems_whenUserNotFound_thenThrowException() {
        long nonExistentUserId = 999L;

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingListCreatedByUserId(nonExistentUserId, "ALL", null, null);
        });

        String expectedMessage = "Пользователь с id: " + nonExistentUserId + " не найден";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getBookingListForAllOwnerItems_whenInvalidStatus_thenThrowException() {
        String invalidStatus = "INVALID_STATUS";

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.getBookingListCreatedByUserId(user1.getId(), invalidStatus, 0L, 10L);
        });

        String expectedMessage = "Unknown state: " + invalidStatus;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getBookingListForAllOwnerItems_withPagination_shouldReturnPagedResults() {
        List<BookingDto> firstPageResults = bookingService.getBookingListForAllOwnerItems(user3.getId(),
                "ALL",
                0L,
                2L);
        List<BookingDto> secondPageResults = bookingService.getBookingListForAllOwnerItems(user3.getId(),
                "ALL",
                2L,
                2L);

        assertThat(firstPageResults, hasSize(2));
        assertThat(secondPageResults, hasSize(2));

        assertThat(firstPageResults.get(0).getId(), not(equalTo(secondPageResults.get(0).getId())));
        assertThat(firstPageResults.get(1).getId(), not(equalTo(secondPageResults.get(1).getId())));
    }
}

