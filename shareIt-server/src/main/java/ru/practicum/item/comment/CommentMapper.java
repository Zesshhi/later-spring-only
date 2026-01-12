package ru.practicum.item.comment;

import lombok.NoArgsConstructor;
import ru.practicum.item.comment.dto.CommentCreateDto;
import ru.practicum.item.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated().toString()
        );
    }

    public static List<CommentDto> mapToCommentDto(List<Comment> comments) {
        List<CommentDto> commentDtos = comments.stream().map(CommentMapper::mapToCommentDto).toList();
        return commentDtos;
    }

    public static Comment mapToNewComment(CommentCreateDto commentDto) {
        Comment comment = new Comment();

        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());

        return comment;
    }

}
