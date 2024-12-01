package com.codecool.kgp.entity;

import com.codecool.kgp.entity.geography.Coordinates;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static com.codecool.kgp.entity.enums.Status.ACTIVE;
import static org.instancio.Select.all;
import static org.instancio.Select.field;


class SummitTest {

    @Test
    void getCoordinatesArray_shouldReturnArrayOfDoubles() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .set(field(Summit::getCoordinates), new Coordinates(12.4, 35.6))
                .create();

        //when:
        Double[] actual = summit.getCoordinatesArray();

        //then:
        Assertions.assertThat(actual).isEqualTo(new Double[]{12.4, 35.6});
    }

    @Test
    void getCoordinatesArray_shouldReturnEmptyArrayOfDoubles() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .ignore(field(Summit::getCoordinates))
                .create();

        //when:
        System.out.println(summit.getCoordinates());
        Double[] actual = summit.getCoordinatesArray();

        //then:
        Assertions.assertThat(actual).isNull();
    }

    @Test
    void addChallenge() {
        //given:
        Summit summit = Instancio.create(Summit.class);
        Challenge challenge = Instancio.create(Challenge.class);

        //when:
        summit.addChallenge(challenge);

        //then:
        Assertions.assertThat(summit).extracting(Summit::getChallengesSet, InstanceOfAssertFactories.iterable(Challenge.class))
                .contains(challenge);
    }

    @Test
    void removeChallenge() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .set(field(Summit::getChallengesSet), Instancio.ofSet(Challenge.class).size(1).create())
                .create();
        Challenge challenge = summit.getChallengesSet().stream().findFirst().orElse(null);

        //when:
        summit.removeChallenge(challenge);

        //then:
        Assertions.assertThat(summit).extracting(Summit::getChallengesSet, InstanceOfAssertFactories.iterable(Challenge.class))
                .isEmpty();
    }

    @Test
    void updateSummit() {
        //given:
        Summit summit = Instancio.create(Summit.class);
        Summit updateToSummit = new Summit(
                "Mystic Mountain",
                new Coordinates(46.603354, 2.737646),
                "Alps",
                "Mont Blanc Massif",
                4810,
                "Mystic Mountain is a towering peak offering breathtaking views and challenging climbs.",
                "Bring warm clothes and climbing gear. Avoid in winter unless experienced.",
                95,
                ACTIVE
        );

        //when:
        summit.updateSummit(updateToSummit);

        //then:
        Assertions.assertThat(summit).extracting(Summit::getId).isEqualTo(summit.getId());
        Assertions.assertThat(summit).extracting(Summit::getId).isNotEqualTo(updateToSummit.getId());
        Assertions.assertThat(summit).extracting(Summit::getName).isEqualTo("Mystic Mountain");
        Assertions.assertThat(summit).extracting(Summit::getHeight).isEqualTo(4810);
        Assertions.assertThat(summit).extracting(Summit::getScore).isEqualTo(95);
        Assertions.assertThat(summit).extracting(Summit::getCoordinatesArray).isEqualTo(new Double[]{46.603354, 2.737646});
    }
}