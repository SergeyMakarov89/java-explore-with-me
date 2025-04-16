package ru.practicum.ExploreWithMe.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.comment.CommentService;
import ru.practicum.ExploreWithMe.comment.dto.CommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("events/{eventId}/comments")
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDto> getComments(@PathVariable Long eventId,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        return commentService.getComments(eventId, from, size);
    }
}
