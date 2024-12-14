package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.*;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.entity.geography.Coordinates;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
class SummitMapperTest {

    private final SummitMapper summitMapper = new SummitMapper();


    private static Stream<Arguments> provideFields() {
        return Stream.of(
                Arguments.of((List<Field>) null),
                Arguments.of(List.of("id", "guideNotes")),
                Arguments.of(List.of("id", "name", "description", "status", "challengesSet")),
                Arguments.of(List.of("name", "challengesSet"))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "provideFields")
    void mapEntityToDtoWithFields_shouldMapToDto(List<String> fields) {
        //given:
        Summit summit = Instancio.create(Summit.class);

        Set<ChallengeSimpleDto> challengeSimpleDtoSet = summit.getChallengesSet().stream()
                .map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).collect(Collectors.toSet());
        SummitDto result = new SummitDto(summit.getId(), challengeSimpleDtoSet, summit.getName(), summit.getCoordinatesArray(),
                summit.getMountainRange(), summit.getMountainChain(), summit.getHeight(), summit.getDescription(),
                summit.getGuideNotes(), summit.getScore(), summit.getStatus().toString());

        //when:
        SummitDto actual = summitMapper.mapEntityToDto(summit, fields);

        //then:
        Assertions.assertThat(actual).extracting("id")
                .isEqualTo((fields == null || fields.contains("id")) ? result.id() : null);
        Assertions.assertThat(actual).extracting("challengesSet")
                .isEqualTo((fields == null || fields.contains("challengesSet")) ? result.challengesSet() : null);
        Assertions.assertThat(actual).extracting("name")
                .isEqualTo((fields == null || fields.contains("name")) ? result.name() : null);
        Assertions.assertThat(actual).extracting("coordinates")
                .isEqualTo((fields == null || fields.contains("coordinates")) ? result.coordinates() : null);
        Assertions.assertThat(actual).extracting("mountainRange")
                .isEqualTo((fields == null || fields.contains("mountainRange")) ? result.mountainRange() : null);
        Assertions.assertThat(actual).extracting("mountainChain")
                .isEqualTo((fields == null || fields.contains("mountainChain")) ? result.mountainChain() : null);
        Assertions.assertThat(actual).extracting("height")
                .isEqualTo((fields == null || fields.contains("height")) ? result.height() : null);
        Assertions.assertThat(actual).extracting("description")
                .isEqualTo((fields == null || fields.contains("description")) ? result.description() : null);
        Assertions.assertThat(actual).extracting("guideNotes")
                .isEqualTo((fields == null || fields.contains("guideNotes")) ? result.guideNotes() : null);
        Assertions.assertThat(actual).extracting("score")
                .isEqualTo((fields == null || fields.contains("score")) ? result.score() : null);
        Assertions.assertThat(actual).extracting("status")
                .isEqualTo((fields == null || fields.contains("status")) ? result.status() : null);
    }

    @Test
    void mapEntityToDto_shouldMapToDto() {
        //given:
        Summit summit = Instancio.create(Summit.class);

        Set<ChallengeSimpleDto> challengeSimpleDtoSet = summit.getChallengesSet().stream()
                .map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).collect(Collectors.toSet());
        SummitDto result = new SummitDto(summit.getId(), challengeSimpleDtoSet, summit.getName(), summit.getCoordinatesArray(),
                summit.getMountainRange(), summit.getMountainChain(), summit.getHeight(), summit.getDescription(),
                summit.getGuideNotes(), summit.getScore(), summit.getStatus().toString());

        //when:
        SummitDto actual = summitMapper.mapEntityToDto(summit);

        //then:
        Assertions.assertThat(actual).extracting("id").isEqualTo(result.id());
        Assertions.assertThat(actual).extracting("challengesSet").isEqualTo(result.challengesSet());
        Assertions.assertThat(actual).extracting("name").isEqualTo(result.name());
        Assertions.assertThat(actual).extracting("coordinates").isEqualTo(result.coordinates());
        Assertions.assertThat(actual).extracting("mountainRange").isEqualTo(result.mountainRange());
        Assertions.assertThat(actual).extracting("mountainChain").isEqualTo(result.mountainChain());
        Assertions.assertThat(actual).extracting("height").isEqualTo(result.height());
        Assertions.assertThat(actual).extracting("description").isEqualTo(result.description());
        Assertions.assertThat(actual).extracting("guideNotes").isEqualTo(result.guideNotes());
        Assertions.assertThat(actual).extracting("score").isEqualTo(result.score());
        Assertions.assertThat(actual).extracting("status").isEqualTo(result.status());
    }

    @Test
    void mapEntityToSimpleDto_shouldMapToDto() {
        //given:
        Summit summit = Instancio.create(Summit.class);

        Set<ChallengeSimpleDto> challengeSimpleDtoSet = summit.getChallengesSet().stream()
                .map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).collect(Collectors.toSet());
        SummitDto result = new SummitDto(summit.getId(), challengeSimpleDtoSet, summit.getName(), summit.getCoordinatesArray(),
                summit.getMountainRange(), summit.getMountainChain(), summit.getHeight(), summit.getDescription(),
                summit.getGuideNotes(), summit.getScore(), summit.getStatus().toString());

        //when:
        SummitSimpleDto actual = summitMapper.mapEntityToSimpleDto(summit);

        //then:
        Assertions.assertThat(actual).extracting("id").isEqualTo(result.id());
        Assertions.assertThat(actual).extracting("name").isEqualTo(result.name());
        Assertions.assertThat(actual).extracting("mountainRange").isEqualTo(result.mountainRange());
        Assertions.assertThat(actual).extracting("mountainChain").isEqualTo(result.mountainChain());
        Assertions.assertThat(actual).extracting("height").isEqualTo(result.height());
        Assertions.assertThat(actual).extracting("status").isEqualTo(result.status());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "K2, 35.8808, 76.5158, Karakorum, Himalayas, 8611, Second highest mountain in the world., Extreme technical climb recommended for experienced mountaineers., 9, ACTIVE",
            "Elbrus, 43.3499, 42.4374, Caucasus, Caucasus Mountains, 5642, Highest peak in Europe., Easier routes available for guided climbs., 7, DEVELOP"
    })
    void mapRequestDtoToEntity_shouldMapDto(String name, Double latitude, Double longitude, String mountainRange, String mountainChain,
                                            Integer height, String description, String guideNotes, Integer score, String status) {
        //give:
        SummitRequestDto dto = new SummitRequestDto(name, latitude, longitude, mountainRange, mountainChain, height, description, guideNotes, score, status);
        Summit result = new Summit(name, new Coordinates(latitude, longitude), mountainRange, mountainChain, height, description, guideNotes, score, Status.valueOf(status));

        //when:
        Summit actual = summitMapper.mapRequestDtoToEntity(dto);

        //then:
        Assertions.assertThat(actual.getName()).isEqualTo(result.getName());
        Assertions.assertThat(actual.getStatus()).isEqualTo(result.getStatus());
        Assertions.assertThat(actual.getHeight()).isEqualTo(result.getHeight() );
        Assertions.assertThat(actual.getScore()).isEqualTo(result.getScore() );
        Assertions.assertThat(actual.getDescription()).isEqualTo(result.getDescription() );
        Assertions.assertThat(actual.getGuideNotes()).isEqualTo(result.getGuideNotes() );
        Assertions.assertThat(actual.getChallengesSet()).isEqualTo(result.getChallengesSet() );
        Assertions.assertThat(actual.getMountainRange()).isEqualTo(result.getMountainRange() );
        Assertions.assertThat(actual.getCoordinatesArray()).isEqualTo(result.getCoordinatesArray() );
        Assertions.assertThat(actual.getMountainChain()).isEqualTo(result.getMountainChain() );
    }
}