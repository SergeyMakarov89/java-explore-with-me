package ru.practicum.ExploreWithMe.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ExploreWithMe.event.dto.EventShortDto;
import ru.practicum.ExploreWithMe.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private LocalDateTime created;
    private UserShortDto commentator;
    private EventShortDto event;
    @NotBlank
    @Size(min = 1, max = 1000)
    private String comment;
}
