package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    @EntityGraph(value = "comment-with-item-and-author", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Comment> findById(Long id);

    @Query("SELECT new ru.practicum.shareit.item.dto.CommentDto(c.id, c.text, c.author.name, c.created)" +
            " FROM Comment c WHERE c.item.id = :itemId")
    List<CommentDto> findCommentsByItemId(@Param("itemId") Long itemId);
}
