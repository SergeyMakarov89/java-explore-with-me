package ru.practicum.ExploreWithMe.user;

import lombok.experimental.UtilityClass;
import ru.practicum.ExploreWithMe.user.dto.NewUserRequest;
import ru.practicum.ExploreWithMe.user.dto.UserDto;
import ru.practicum.ExploreWithMe.user.dto.UserShortDto;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());

        return userShortDto;
    }

    public User toUser(NewUserRequest newUserRequest) {
        User newUser = new User();
        newUser.setName(newUserRequest.getName());
        newUser.setEmail(newUserRequest.getEmail());

        return newUser;
    }
}
