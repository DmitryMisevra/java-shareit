package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreatedCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {

    CommentMapper commentMapper;

    CreatedCommentDto createdCommentDto;
    Comment comment;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();

        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test name")
                .description("test description")
                .ownerId(1L)
                .available(true)
                .requestId(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("test comment text1")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
                .build();

        createdCommentDto = CreatedCommentDto.builder()
                .text("test comment text1")
                .build();

    }

    @Test
    void createdCommentDtoToComment_WhenMappingValidCommentDto_thenReturnComment() {
        Comment convertedComment = commentMapper.createdCoomentDtoToComment(createdCommentDto);

        assertNotNull(convertedComment);
        assertEquals(createdCommentDto.getText(), convertedComment.getText());
        assertNull(convertedComment.getId());
        assertNull(convertedComment.getAuthor());
        assertNull(convertedComment.getCreated());
    }

    @Test
    void commentToCommentDto_WhenMappingValidComment_thenReturnCommentDto() {
        CommentDto convertedCommentDto = commentMapper.commentToCommentDto(comment);

        assertNotNull(convertedCommentDto);
        assertEquals(comment.getId(), convertedCommentDto.getId());
        assertEquals(comment.getText(), convertedCommentDto.getText());
        assertEquals(comment.getAuthor().getName(), convertedCommentDto.getAuthorName());
        assertEquals(comment.getCreated(), convertedCommentDto.getCreated());
    }
}
