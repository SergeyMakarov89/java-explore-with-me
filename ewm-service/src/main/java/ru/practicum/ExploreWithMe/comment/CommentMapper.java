package ru.practicum.ExploreWithMe.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.ExploreWithMe.comment.dto.CommentDto;
import ru.practicum.ExploreWithMe.event.EventMapper;
import ru.practicum.ExploreWithMe.user.UserMapper;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setCommentator(UserMapper.toUserShortDto(comment.getCommentator()));
        commentDto.setEvent(EventMapper.toEventShortDto(comment.getEvent()));
        commentDto.setComment(comment.getComment());

        return commentDto;
    }
}
