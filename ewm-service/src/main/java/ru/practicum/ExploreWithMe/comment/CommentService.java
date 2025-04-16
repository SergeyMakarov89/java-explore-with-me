package ru.practicum.ExploreWithMe.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.comment.dto.CommentDto;
import ru.practicum.ExploreWithMe.comment.dto.NewCommentDto;
import ru.practicum.ExploreWithMe.event.Event;
import ru.practicum.ExploreWithMe.event.EventRepository;
import ru.practicum.ExploreWithMe.exception.ConflictException;
import ru.practicum.ExploreWithMe.user.User;
import ru.practicum.ExploreWithMe.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        if (!userRepository.existsById(userId)) {
            throw new ConflictException("Такого пользователя не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new ConflictException("Такого события не существует");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();

        Comment comment = new Comment();
        comment.setCommentator(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        comment.setComment(newCommentDto.getComment());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ConflictException("Такого пользователя не существует");
        }
        if (!commentRepository.existsById(commentId)) {
            throw new ConflictException("Такого комментария не существует");
        }
        if (!commentRepository.findById(commentId).orElseThrow().getCommentator().getId().equals(userId)) {
            throw new ConflictException("Удалять комментарий может только автор комментария");
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, Long userId, Long eventId, NewCommentDto newCommentDto) {
        if (!userRepository.existsById(userId)) {
            throw new ConflictException("Такого пользователя не существует");
        }
        if (!commentRepository.existsById(commentId)) {
            throw new ConflictException("Такого комментария не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new ConflictException("Такого события не существует");
        }
        if (!commentRepository.findById(commentId).orElseThrow().getEvent().getId().equals(eventId)) {
            throw new ConflictException("Этот комментарий оставлен под другим событием");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow();

        comment.setComment(newCommentDto.getComment());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new ConflictException("Такого события не существует");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }
}
