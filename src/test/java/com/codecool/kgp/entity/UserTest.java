package com.codecool.kgp.entity;

import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.geography.Coordinates;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.field;

class UserTest {

    @Test
    void getCoordinatesArray_shouldReturnArrayWithDoubles() {
        //given:
        User testUser = new User();
        testUser.setCoordinates(new Coordinates(1.0,2.0));

        //when:
        Double[] actual = testUser.getCoordinatesArray();

        //then:
        Assertions.assertThat(actual).isEqualTo(new Double[]{1.0, 2.0});
    }

    @Test
    void getCoordinatesArray_shouldReturnNull() {
        //given:
        User testUser = new User();

        //when:
        Double[] actual = testUser.getCoordinatesArray();

        //then:
        Assertions.assertThat(actual).isNull();
    }

    @Test
    void updateUser_shouldUpdateUser() {
        //given:
        User user = Instancio.of(User.class)
                .set(field(User::getName),"Adam")
                .set(field(User::getEmail),"adam@mail.com")
                .set(field(User::getPhone), "+00 111 222 333")
                .set(field(User::getNewsletter), true)
                .create();
        UserRequestDto userRequestDto = Instancio.of(UserRequestDto.class)
                .set(field(UserRequestDto::name),"Bogdan")
                .set(field(UserRequestDto::email), "bogdan@mail.com")
                .set(field(UserRequestDto::phone), "+99 123 456 789")
                .set(field(UserRequestDto::newsletter), false)
                .create();

        //when:
        user.updateUser(userRequestDto);

        //then:
        Assertions.assertThat(user.getName())
                .as("Name is not correct").isEqualTo(userRequestDto.name());
        Assertions.assertThat(user.getEmail())
                .as("Email is not correct").isEqualTo(userRequestDto.email());
        Assertions.assertThat(user.getPhone())
                .as("Phone number is not correct").isEqualTo(userRequestDto.phone());
        Assertions.assertThat(user.getNewsletter())
                .as("Newsletter value is not correct").isEqualTo(userRequestDto.newsletter());
        Assertions.assertThat(user.getCoordinates().getLatitude())
                .as("Latitude value is not correct").isEqualTo(userRequestDto.latitude());
        Assertions.assertThat(user.getCoordinates().getLongitude())
                .as("Longitude value is not correct").isEqualTo(userRequestDto.longitude());
    }

    @Test
    void updateUser_shouldSetCoordinatesToNull() {
        //given:
        User user = Instancio.create(User.class);
        UserRequestDto userRequestDto = Instancio.of(UserRequestDto.class)
                .set(field(UserRequestDto::latitude), null)
                .set(field(UserRequestDto::longitude), null)
                .create();

        //when:
        user.updateUser(userRequestDto);

        //then:
        Assertions.assertThat(user.getCoordinates()).isNull();
    }
}