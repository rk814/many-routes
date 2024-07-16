package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.UserDto;
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
                user.getCoordinatessArray(),
                user.getPhone(),
                user.isNewsletter(),
                user.getRole().name()
        );
    }
}
