package ru.practicum.shareit.booking.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    User user1 = User.builder()
            .name("user1")
            .email("user1@user.com")
            .build();

    User user2 = User.builder()
            .name("user2")
            .email("user2@user.com")
            .build();

    Item item1 = Item.builder()
            .name("Test name1")
            .description("Test description1")
            .ownerId(2L)
            .available(true)
            .build();

    Item item2 = Item.builder()
            .name("Test name2")
            .description("Test description2")
            .ownerId(2L)
            .available(true)
            .build();

    Booking booking1 = Booking.builder()
            .item(item1)
            .booker(user1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .status(Status.WAITING)
            .build();

    Booking booking2 = Booking.builder()
            .item(item2)
            .booker(user2)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .status(Status.WAITING)
            .build();

    Booking pastBooking = Booking.builder()
            .item(item1)
            .booker(user1)
            .start(LocalDateTime.now().minusDays(5))
            .end(LocalDateTime.now().minusDays(3))
            .status(Status.APPROVED)
            .build();

    Booking pastBookingWithWrongStatus = Booking.builder()
            .item(item1)
            .booker(user1)
            .start(LocalDateTime.now().minusDays(5))
            .end(LocalDateTime.now().minusDays(3))
            .status(Status.WAITING)
            .build();

    Booking futureBooking = Booking.builder()
            .item(item1)
            .booker(user1)
            .start(LocalDateTime.now().plusDays(3))
            .end(LocalDateTime.now().plusDays(5))
            .status(Status.APPROVED)
            .build();

    Booking futureBookingWithWrongStatus = Booking.builder()
            .item(item1)
            .booker(user1)
            .start(LocalDateTime.now().plusDays(3))
            .end(LocalDateTime.now().plusDays(5))
            .status(Status.WAITING)
            .build();

    @Test
    void findById_WhenBookingExists_thenReturnBooking() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item2);
        entityManager.persist(booking2);

        entityManager.flush();


        Optional<Booking> foundBooking = bookingRepository.findById(1L);

        assertTrue(foundBooking.isPresent());
        assertEquals(1L, foundBooking.get().getId());
        assertEquals(booking2.getBooker().getId(), foundBooking.get().getBooker().getId());
        assertEquals(booking2.getItem().getId(), foundBooking.get().getItem().getId());
    }

    @Test
    void findById_WhenBookingDoesNotExist_returnEmptyOptional() {
        Optional<Booking> foundBooking = bookingRepository.findById(-1L);

        assertFalse(foundBooking.isPresent());
    }

    @Test
    void findLastBookingByItemId_WhenBookingExists_thenReturnBookingInfo() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(pastBooking);

        entityManager.flush();

        Page<BookingInfoDto> result = bookingRepository.findLastBookingByItemId(1L,
                PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());

        BookingInfoDto bookingInfoDto = result.getContent().get(0);
        assertEquals(pastBooking.getId(), bookingInfoDto.getId());
        assertEquals(user1.getId(), bookingInfoDto.getBookerId());
    }

    @Test
    void findLastBookingByItemId_WhenNoBookingExists_thenReturnEmptyResult() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(pastBookingWithWrongStatus);

        entityManager.flush();

        Page<BookingInfoDto> result = bookingRepository.findLastBookingByItemId(1L,
                PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
    }

    @Test
    void findNextBookingByItemId_WhenBookingExists_thenReturnBookingInfo() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(futureBooking);

        entityManager.flush();

        Page<BookingInfoDto> result = bookingRepository.findNextBookingByItemId(1L,
                PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());

        BookingInfoDto bookingInfoDto = result.getContent().get(0);
        assertEquals(futureBooking.getId(), bookingInfoDto.getId());
        assertEquals(user1.getId(), bookingInfoDto.getBookerId());
    }

    @Test
    void findNextBookingByItemId_WhenNoBookingExists_thenReturnEmptyResult() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(futureBookingWithWrongStatus);

        entityManager.flush();

        Page<BookingInfoDto> result = bookingRepository.findNextBookingByItemId(1L,
                PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
    }

    @Test
    void checkIfCompletedBookingExistsForItemByUserId_WhenBookingExists_thenReturnTrue() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(pastBooking);

        entityManager.flush();

        boolean result = bookingRepository.checkIfCompletedBookingExistsForItemByUserId(user1.getId(), item1.getId());

        assertTrue(result);
    }

    @Test
    void checkIfCompletedBookingExistsForItemByUserId_WhenNoBookingExists_thenReturnFalse() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(pastBookingWithWrongStatus);

        entityManager.flush();

        boolean result = bookingRepository.checkIfCompletedBookingExistsForItemByUserId(user1.getId(), item1.getId());

        assertFalse(result);
    }
}
