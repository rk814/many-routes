package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ChallengeMapperTest {

    @Mock
    private SummitMapper summitMapper;

    @InjectMocks
    private ChallengeMapper challengeMapper;


    private static Stream<Arguments> provideChallenges() {
        return Stream.of(
                Arguments.of(UUID.fromString("8c24db8e-37cb-4c80-90df-b00c2448d134"), "hard", "hard challenge",
                        Status.ACTIVE, Instancio.ofList(Summit.class).size(3).create(), null),
                Arguments.of(UUID.fromString("3b9649b8-4e49-40f6-9a7e-38e53e9102ba"), "light", "light challenge",
                        Status.ACTIVE, Instancio.ofList(Summit.class).size(2).create(), List.of("id", "name", "summitsList")),
                Arguments.of(UUID.fromString("f31ff492-4959-4637-a2cc-98779f365eed"), "old", "old challenge",
                        Status.REMOVED, Instancio.ofList(Summit.class).size(1).create(), List.of("id", "name", "description", "status", "summitsList")),
                Arguments.of(UUID.fromString("225bd451-1f2d-44fe-8141-7fc98d283dbb"), "xxx", "surprise",
                        Status.DEVELOP, Instancio.ofList(Summit.class).size(1).create(), List.of("name", "summitsList"))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "provideChallenges")
    void mapEntityToDto_shouldMapToDto_whenSummitsAreNotEmptyAndRequested(UUID id, String name, String description, Status status, List<Summit> summits,
                                                                          List<String> fields) throws NoSuchFieldException, IllegalAccessException {
        //given:
        Challenge challenge = new Challenge(name, description, status);
        summits.forEach(challenge::addSummit);
        Class<? extends Challenge> clazz = challenge.getClass();
        Field idField = clazz.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(challenge, id);

        SummitSimpleDto dummySummitDto = Instancio.create(SummitSimpleDto.class);
        List<SummitSimpleDto> dummySummitDtoList = IntStream.range(0, summits.size()).mapToObj(i -> dummySummitDto).toList();
        Mockito.when(summitMapper.mapEntityToSimpleDto(any(Summit.class)))
                .thenReturn(dummySummitDto);
        ChallengeDto result = new ChallengeDto(id, name, description, status, dummySummitDtoList);

        //when:
        ChallengeDto actual = challengeMapper.mapEntityToDto(challenge);

        //then:
        Assertions.assertThat(actual).extracting("id").isEqualTo(result.id());
        Assertions.assertThat(actual).extracting("name").isEqualTo(result.name());
        Assertions.assertThat(actual).extracting("description").isEqualTo(result.description());
        Assertions.assertThat(actual).extracting("status").isEqualTo(result.status());
        Assertions.assertThat(actual).extracting("summitsList").isEqualTo(result.summitsList());
    }

    @ParameterizedTest
    @MethodSource(value = "provideChallenges")
    void mapEntityToDtoWithFields_shouldMapToDto_whenSummitsAreNotEmptyAndRequested(UUID id, String name, String description,
                                                                                    Status status, List<Summit> summits, List<String> fields) throws NoSuchFieldException, IllegalAccessException {
        //given:
        Challenge challenge = new Challenge(name, description, status);
        summits.forEach(challenge::addSummit);
        Class<? extends Challenge> clazz = challenge.getClass();
        Field idField = clazz.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(challenge, id);

        SummitSimpleDto dummySummitDto = Instancio.create(SummitSimpleDto.class);
        List<SummitSimpleDto> dummySummitDtoList = IntStream.range(0, summits.size()).mapToObj(i -> dummySummitDto).toList();
        Mockito.when(summitMapper.mapEntityToSimpleDto(any(Summit.class)))
                .thenReturn(dummySummitDto);
        ChallengeDto result = new ChallengeDto(id, name, description, status, dummySummitDtoList);

        //when:
        ChallengeDto actual = challengeMapper.mapEntityToDto(challenge, fields);

        //then:
        Assertions.assertThat(actual).extracting("id")
                .isEqualTo((fields == null || fields.contains("id")) ? result.id() : null);
        Assertions.assertThat(actual).extracting("name")
                .isEqualTo((fields == null || fields.contains("name")) ? result.name() : null);
        Assertions.assertThat(actual).extracting("description")
                .isEqualTo((fields == null || fields.contains("description")) ? result.description() : null);
        Assertions.assertThat(actual).extracting("status")
                .isEqualTo((fields == null || fields.contains("status")) ? result.status() : null);
        Assertions.assertThat(actual).extracting("summitsList")
                .isEqualTo((fields == null || fields.contains("summitsList")) ? result.summitsList() : null);
    }

    private static Stream<Arguments> provideChallengesWithoutSummits() {
        return Stream.of(
                Arguments.of(UUID.fromString("8c24db8e-37cb-4c80-90df-b00c2448d134"), "hard", "hard challenge",
                        Status.ACTIVE, Instancio.ofList(Summit.class).size(0).create(), null),
                Arguments.of(UUID.fromString("3b9649b8-4e49-40f6-9a7e-38e53e9102ba"), "light", "light challenge",
                        Status.ACTIVE, Instancio.ofList(Summit.class).size(2).create(), List.of("id", "name"))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "provideChallengesWithoutSummits")
    void mapEntityToDtoWithFields_shouldMapToDto_whenSummitsAreEmptyOrNotRequested(UUID id, String name, String description, Status status, List<Summit> summits,
                                                                                   List<String> fields) throws NoSuchFieldException, IllegalAccessException {
        //given:
        Challenge challenge = new Challenge(name, description, status);
        Class<? extends Challenge> clazz = challenge.getClass();
        Field idField = clazz.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(challenge, id);

        SummitSimpleDto dummySummitDto = Instancio.create(SummitSimpleDto.class);
        List<SummitSimpleDto> dummySummitDtoList = IntStream.range(0, summits.size()).mapToObj(i -> dummySummitDto).toList();
        ChallengeDto result = new ChallengeDto(id, name, description, status, dummySummitDtoList);

        //when:
        ChallengeDto actual = challengeMapper.mapEntityToDto(challenge, fields);

        //then:
        Assertions.assertThat(actual).extracting("id")
                .isEqualTo((fields == null || fields.contains("id")) ? result.id() : null);
        Assertions.assertThat(actual).extracting("name")
                .isEqualTo((fields == null || fields.contains("name")) ? result.name() : null);
        Assertions.assertThat(actual).extracting("description")
                .isEqualTo((fields == null || fields.contains("description")) ? result.description() : null);
        Assertions.assertThat(actual).extracting("status")
                .isEqualTo((fields == null || fields.contains("status")) ? result.status() : null);
        Assertions.assertThat(actual).extracting("summitsList")
                .isEqualTo((fields == null || fields.contains("summitsList")) ? result.summitsList() : null);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "hard,hard challenge, ACTIVE",
            "light, light challenge, ACTIVE",
            "old, old challenge, REMOVED",
            "xxx, surprise, DEVELOP"
    })
    void mapRequestDtoToEntity_shouldMapDto(String name, String description, Status status) {
        //give:
        ChallengeRequestDto dto = new ChallengeRequestDto(name, description, status);
        Challenge result = new Challenge(name, description, status);

        //when:
        Challenge actual = challengeMapper.mapRequestDtoToEntity(dto);

        //then:
        Assertions.assertThat(actual).isEqualTo(result);
    }
}