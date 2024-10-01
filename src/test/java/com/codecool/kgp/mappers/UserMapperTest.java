package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.entity.enums.Role;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.geography.Coordinates;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.instancio.Select.field;


class UserMapperTest {

    private final UserMapper userMapper = new UserMapper(new UserChallengeMapper());

    @Test
    void mapEntityToDto() {
        //given:
        UUID id = UUID.randomUUID();
        Coordinates coordinates = Instancio.create(Coordinates.class);
        User user = Instancio.of(User.class)
                .set(field(User::getId), id)
                .set(field(User::getLogin), "adam")
                .set(field(User::getHashPassword), "xxx")
                .set(field(User::getName), "Adam")
                .set(field(User::getEmail), "adam@adam.com")
                .set(field(User::getCoordinates),coordinates)
                .set(field(User::getPhone), "+48 123456789")
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
        Assertions.assertThat(actual.login()).isEqualTo(user.getLogin());
        Assertions.assertThat(actual.name()).isEqualTo(user.getName());
        Assertions.assertThat(actual.email()).isEqualTo(user.getEmail());
        Assertions.assertThat(actual.coordinates()).isEqualTo(user.getCoordinatesArray());
        Assertions.assertThat(actual.phone()).isEqualTo(user.getPhone());
        Assertions.assertThat(actual.newsletter()).isEqualTo(user.getNewsletter());
        Assertions.assertThat(actual.createAt()).isEqualTo(user.getCreatedAt());
        Assertions.assertThat(actual.deletedAt()).isEqualTo(user.getDeletedAt());
        Assertions.assertThat(actual.role()).isEqualTo(user.getRole().name());
    }

//    @Test
//    void mapRequestDtoToEntity() {
//        //given:
//        UserRequestDto userRequestDto = Instancio.of(UserRequestDto.class)
//                .set(field(UserRequestDto::login), "adam")
//                .set(field(UserRequestDto::password), "xxx")
//                .set(field(UserRequestDto::email), "adam@adam.com")
//                .create();
//
//        //when:
//        User actual = userMapper.mapRequestDtoToEntity(userRequestDto);
//
//        //then:
//        Assertions.assertThat(actual.getLogin()).isEqualTo(userRequestDto.login());
//        Assertions.assertThat(actual.getHashPassword()).isEqualTo(userRequestDto.password());
//        Assertions.assertThat(actual.getEmail()).isEqualTo(userRequestDto.email());
//        Assertions.assertThat(actual.getRole()).isEqualTo(Role.USER);
//    }
}