package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    @EntityGraph(value = "booking-with-item-and-user", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Booking> findById(Long id);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingInfoDto(b.id, b.booker.id) FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.start < CURRENT_TIMESTAMP AND b.status = 'APPROVED' ORDER BY b.end DESC")
    Page<BookingInfoDto> findLastBookingByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingInfoDto(b.id, b.booker.id) FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.start > CURRENT_TIMESTAMP AND b.status = 'APPROVED' ORDER BY b.start ASC")
    Page<BookingInfoDto> findNextBookingByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId AND b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND b.end < CURRENT_TIMESTAMP")
    boolean checkIfCompletedBookingExistsForItemByUserId(@Param("userId") Long userId, @Param("itemId") Long itemId);
}
