package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.repository.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto mapEntityToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getCoordinatesArray(),
                user.getPhone(),
                user.isNewsletter(),
                (user.getRole()!=null) ? user.getRole().name() : null
        );
    }

    public User mapRequestDtoToEntity(UserRequestDto dto) {
        return new User(
                dto.login(),
                dto.password(), // TODO hash
                dto.email()
        );
    }
}
