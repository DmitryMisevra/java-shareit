package ru.practicum.shareit.item.mapper;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreatedCommentDto;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {

    public Comment createdCoomentDtoToComment(@NonNull CreatedCommentDto createdCommentDto) {
        return Comment.builder()
                .text(createdCommentDto.getText())
                .build();
    }

    public CommentDto commentToCommentDto(@NonNull Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

}
