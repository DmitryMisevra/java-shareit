package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    List<Item> findItemsByOwnerIdOrderByIdAsc(long ownerId);

    Page<Item> findItemsByOwnerIdOrderByIdAsc(long ownerId, Pageable pageable);

    @Query(" select i from Item i " +
            "where i.available = true and (" +
            "      upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%'))" +
            ")")
    List<Item> searchItemsByText(String text);

    @Query(" select i from Item i " +
            "where i.available = true and (" +
            "      upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%'))" +
            ")")
    Page<Item> searchItemsByText(String text, Pageable pageable);
}
