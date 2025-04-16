package ru.practicum.ExploreWithMe.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NonNull
    Float lat;
    @NonNull
    Float lon;
}
