package ru.practicum.ExploreWithMe.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.comment.CommentService;
import ru.practicum.ExploreWithMe.comment.dto.CommentDto;
import ru.practicum.ExploreWithMe.comment.dto.NewCommentDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId, @PathVariable Long eventId, @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long commentId, @PathVariable Long userId, @PathVariable Long eventId, @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.updateComment(commentId, userId, eventId, newCommentDto);
    }
}
