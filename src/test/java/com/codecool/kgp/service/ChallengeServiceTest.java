package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.repository.ChallengeRepository;
import com.codecool.kgp.repository.SummitRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.assertj.core.api.Assertions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;

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
        actual.forEach(a -> Assertions.assertThat(a.getStatus()).isEqualTo(Status.ACTIVE));
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
        actual.forEach(a -> Assertions.assertThat(a.getStatus()).isEqualTo(Status.ACTIVE));
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

    @Test
    void addNewChallenge_shouldReturnAddedChallenge() {
        //given:
        Challenge challenge = Instancio.create(Challenge.class);
        Challenge savedChallenge = Instancio.create(Challenge.class);
        Mockito.when(challengeRepository.save(challenge)).thenReturn(savedChallenge);

        //when:
        Challenge actual = challengeService.addNewChallenge(challenge);

        //then:
        Assertions.assertThat(actual).isEqualTo(savedChallenge);
    }

    @Test
    void addNewChallenge_shouldThrowDuplicateEntryException() {
        //given:
        Challenge challenge = Instancio.create(Challenge.class);
        Mockito.when(challengeRepository.save(challenge))
                .thenThrow(new DataIntegrityViolationException("duplicated db rows"));

        //when and then:
        Assertions.assertThatThrownBy(() -> challengeService.addNewChallenge(challenge))
                .isInstanceOf(DuplicateEntryException.class);
    }

    @Test
    void attachSummitToChallenge_shouldReturnChallengeWithSummit() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .setBlank(field(Summit::getChallengeList))
                .create();
        Challenge challenge = Instancio.of(Challenge.class)
                .setBlank(field(Challenge::getSummitList))
                .create();
        Mockito.when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        Mockito.when(summitRepository.findById(summit.getId())).thenReturn(Optional.of(summit));

        //when:
        Challenge actual = challengeService.attachSummitToChallenge(summit.getId(), challenge.getId());

        //then:
        Assertions.assertThat(actual).extracting(Challenge::getSummitList).isNotNull();
        Assertions.assertThat(summit).extracting(Summit::getChallengeList).isNotNull();
    }

    @Test
    void attachSummitToChallenge_shouldReturn404NoChallengeFound() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .setBlank(field(Summit::getChallengeList))
                .create();
        Challenge challenge = Instancio.of(Challenge.class)
                .setBlank(field(Challenge::getSummitList))
                .create();
        Mockito.when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() ->
                        challengeService.attachSummitToChallenge(summit.getId(), challenge.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Challenge", "not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void attachSummitToChallenge_shouldReturn404NoSummitFound() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .setBlank(field(Summit::getChallengeList))
                .create();
        Challenge challenge = Instancio.of(Challenge.class)
                .setBlank(field(Challenge::getSummitList))
                .create();
        Mockito.when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        Mockito.when(summitRepository.findById(summit.getId())).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() ->
                        challengeService.attachSummitToChallenge(summit.getId(), challenge.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Summit", "not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateChallenge_shouldReturnUpdatedChallenge() {
        //given:
        UUID id = UUID.randomUUID();
        Challenge challenge = new Challenge("testChallenge", "some description", Status.ACTIVE);
        Challenge challengeFromDb = Instancio.of(Challenge.class)
                .set(field(Challenge::getName), challenge.getName())
                .set(field(Challenge::getDescription), challenge.getDescription())
                .set(field(Challenge::getStatus), challenge.getStatus())
                .create();
        Mockito.when(challengeRepository.findById(id)).thenReturn(Optional.of(challengeFromDb));
        Mockito.when(challengeRepository.save(challengeFromDb)).thenReturn(challengeFromDb);

        //when:
        Challenge actual = challengeService.updateChallenge(id, challenge);

        //then:
        Assertions.assertThat(actual).isEqualTo(challengeFromDb);

        verify(challengeRepository).findById(id);
        verify(challengeRepository).save(challengeFromDb);
    }

    @Test
    void updateChallenge_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();
        Challenge challenge = new Challenge("testChallenge", "some description", Status.ACTIVE);
        Mockito.when(challengeRepository.findById(id)).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() -> challengeService.updateChallenge(id, challenge))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Challenge", "not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteChallenge_shouldDeleteChallenge() {
        //given:
        UUID id = UUID.randomUUID();
        Challenge challenge = Instancio.create(Challenge.class);
        Mockito.when(challengeRepository.findById(id)).thenReturn(Optional.of(challenge));

        //when:
        challengeService.deleteChallenge(id);

        //then:
        verify(challengeRepository).findById(id);
        verify(challengeRepository).delete(challenge);
    }

    @Test
    void deleteChallenge_shouldReturn404() {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(challengeRepository.findById(id)).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() -> challengeService.deleteChallenge(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}