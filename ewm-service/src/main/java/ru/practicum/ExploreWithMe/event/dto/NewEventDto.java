package ru.practicum.ExploreWithMe.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ExploreWithMe.location.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    @Valid
    private LocationDto location;
    private Boolean paid = false;
    @PositiveOrZero
    private Long participantLimit = 0L;
    private Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
