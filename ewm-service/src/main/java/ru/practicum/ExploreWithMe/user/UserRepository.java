package ru.practicum.ExploreWithMe.user;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> idsList, Pageable pageable);

    Boolean existsByEmail(String email);
}
