package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    User user = User.builder()
            .name("user")
            .email("user@user.com")
            .build();
    Item item = Item.builder()
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(1L)
            .available(true)
            .build();


    @Test
    public void findById_WhenCommentExists_thenReturnComment() {
        Comment comment = Comment.builder()
                .text("test comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
                .build();

        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment);

        entityManager.flush();

        Optional<Comment> foundComment = commentRepository.findById(comment.getId());

        assertTrue(foundComment.isPresent(), "Комментарий должен быть найден");
        assertEquals(comment.getId(), foundComment.get().getId(), "ID комментария должен совпадать");
        assertNotNull(foundComment.get().getAuthor(), "Автор комментария должен быть загружен");
        assertNotNull(foundComment.get().getItem(), "Объект комментария должен быть загружен");
    }

    @Test
    public void findById_WhenCommentDoesNotExist_thenReturnEmpty() {
        Optional<Comment> foundComment = commentRepository.findById(-1L);
        assertFalse(foundComment.isPresent(), "Не должно быть найдено комментария с несуществующим ID");
    }

    @Test
    public void findCommentsByItemId_WhenCommentsExist_thenReturnCommentDtoList() {
        Comment comment1 = Comment.builder()
                .text("test comment1")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
                .build();

        Comment comment2 = Comment.builder()
                .text("test comment2")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2023, Month.DECEMBER, 19, 12, 30))
                .build();

        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment1);
        entityManager.persist(comment2);

        entityManager.flush();

        List<CommentDto> foundComments = commentRepository.findCommentsByItemId(item.getId());

        // Проверка результатов
        assertEquals(2, foundComments.size(), "Должно быть найдено 2 комментария");
        assertTrue(foundComments.stream().allMatch(commentDto ->
                        commentDto.getAuthorName().equals(user.getName())),
                "Все комментарии должны быть связаны с указанным элементом и содержать имя автора");
    }

    @Test
    public void findCommentsByItemId_WhenCommentsDoNotExist_thenReturnEmptyList() {
        List<CommentDto> foundComments = commentRepository.findCommentsByItemId(-1L);
        assertTrue(foundComments.isEmpty(), "Список комментариев должен быть пустым для несуществующего элемента");
    }

}
