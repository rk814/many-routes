package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.UserChallengeSimpleDto;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.enums.Role;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final UserChallengeMapper userChallengeMapper;

    public UserMapper(UserChallengeMapper userChallengeMapper) {
        this.userChallengeMapper = userChallengeMapper;
    }

    public UserDto mapEntityToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getCoordinatesArray(),
                user.getPhone(),
                user.getNewsletter(),
                user.getCreatedAt(),
                user.getDeletedAt(),
                user.getRole().name(),
                getUserChallengeDtoList(user.getUserChallenges())
        );
    }

    private List<UserChallengeSimpleDto> getUserChallengeDtoList(List<UserChallenge> userChallenges) {
        return userChallenges.stream().map(userChallengeMapper::mapEntityToSimpleDto).toList();
    }
}
