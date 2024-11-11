package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.entity.enums.Role;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.geography.Coordinates;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private UserChallengeMapper userChallengeMapper;

    @InjectMocks
    private UserMapper userMapper;


    @Test
    void mapEntityToDto_shouldMapToDto() {
        //given:
        UUID id = UUID.randomUUID();
        User user = Instancio.of(User.class)
                .set(field(User::getId), id)
                .set(field(User::getLogin), "skywalker42")
                .set(field(User::getHashPassword), "SecurePass!2024")
                .set(field(User::getName), "Luke Skywalker")
                .set(field(User::getEmail), "luke.skywalker@galaxy.com")
                .set(field(User::getCoordinates), new Coordinates(51.5074, -0.1278)) // Coordinates for London
                .set(field(User::getPhone), "+44 7700 900123")
                .set(field(User::getNewsletter), true)
                .setBlank(field(User::getUserChallenges))
                .generate(field(User::getCreatedAt), gen->gen.temporal().localDateTime().past())
                .generate(field(User::getDeletedAt), gen->gen.temporal().localDateTime())
                .set(field(User::getRole), Role.USER)
                .create();

        //when:
        UserDto actual = userMapper.mapEntityToDto(user);

        //then:
        Assertions.assertThat(actual.id()).isEqualTo(user.getId());
        Assertions.assertThat(actual.login()).isEqualTo("skywalker42");
        Assertions.assertThat(actual.name()).isEqualTo("Luke Skywalker");
        Assertions.assertThat(actual.email()).isEqualTo("luke.skywalker@galaxy.com");
        Assertions.assertThat(actual.coordinates()).isEqualTo(List.of(51.5074, -0.1278).toArray());
        Assertions.assertThat(actual.phone()).isEqualTo("+44 7700 900123");
        Assertions.assertThat(actual.newsletter()).isEqualTo(true);
        Assertions.assertThat(actual.createAt()).isEqualTo(user.getCreatedAt());
        Assertions.assertThat(actual.deleteAt()).isEqualTo(user.getDeletedAt());
        Assertions.assertThat(actual.role()).isEqualTo(user.getRole().name());
    }
}