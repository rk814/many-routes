package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.repository.ChallengeRepository;
import com.codecool.kgp.repository.SummitRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;

class ChallengeServiceTest {

    private final ChallengeRepository challengeRepository = Mockito.mock();
    private final SummitRepository summitRepository = Mockito.mock();
    private final ChallengeMapper challengeMapper = Mockito.mock();

    private final ChallengeService challengeService = new ChallengeService(
            challengeRepository, summitRepository, challengeMapper
    );

    @Test
    void getAllChallenges_shouldReturnAllChallenges() {
        //given:
        Status status = Status.ACTIVE;
        List<Challenge> challenges = Instancio.ofList(Challenge.class)
                .set(field(Challenge::getStatus), Status.ACTIVE)
                .create();
        Mockito.when(challengeRepository.findAllByStatusWithSummits(status)).thenReturn(challenges);

        //when:
        List<Challenge> actual = challengeService.getAllChallenges(status);

        //then:
        Assertions.assertThat(actual).hasSize(challenges.size());
        actual.forEach(a-> Assertions.assertThat(a.getStatus()).isEqualTo(Status.ACTIVE));
    }

    @Test
    void getAllChallengesWithoutSummitLists_shouldReturnAllChallenges() {
        //given:
        Status status = Status.ACTIVE;
        List<Challenge> challenges = Instancio.ofList(Challenge.class)
                .set(field(Challenge::getStatus), Status.ACTIVE)
                .create();
        Mockito.when(challengeRepository.findAllByStatus(status)).thenReturn(challenges);

        //when:
        List<Challenge> actual = challengeService.getAllChallengesWithoutSummitLists(status);

        //then:
        Assertions.assertThat(actual).hasSize(challenges.size());
        actual.forEach(a-> Assertions.assertThat(a.getStatus()).isEqualTo(Status.ACTIVE));
    }

    @Test
    void getChallenge_shouldReturnProperChallenge() {
        //given:
        UUID id = UUID.randomUUID();
        Challenge testChallenge = Instancio.of(Challenge.class)
                .set(field(Challenge::getId), id)
                .create();
        Mockito.when(challengeRepository.findById(id)).thenReturn(Optional.of(testChallenge));

        //when:
        Challenge actual = challengeService.getChallenge(id);

        //then:
        Assertions.assertThat(actual).isEqualTo(testChallenge);
    }

    @Test
    void getChallenge_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(challengeRepository.findById(id)).thenReturn(Optional.empty());

        //when:
        Assertions.assertThatThrownBy(() -> challengeService.getChallenge(id))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}