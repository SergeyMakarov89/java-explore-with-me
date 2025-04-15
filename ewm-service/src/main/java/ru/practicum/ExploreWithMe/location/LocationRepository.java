package ru.practicum.ExploreWithMe.location;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Boolean existsByLatAndLon(Float lat, Float lon);
}