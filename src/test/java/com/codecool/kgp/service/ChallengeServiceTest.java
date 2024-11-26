package com.codecool.kgp.service;

import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.repository.ChallengeRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.hamcrest.Matchers;
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
    private final SummitService summitService = Mockito.mock();
    private final UserChallengeService userChallengeService = Mockito.mock();

    private final ChallengeService challengeService = new ChallengeService(
            challengeRepository, summitService, userChallengeService
    );

    @Test
    void getAllChallenges_shouldReturnAllChallengesWithSummits() {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = List.of("summitsList");
        List<Challenge> challenges = Instancio.ofList(Challenge.class)
                .set(field(Challenge::getStatus), Status.ACTIVE)
                .create();
        Mockito.when(challengeRepository.findAllByStatusWithSummits(status)).thenReturn(challenges);

        //when:
        List<Challenge> actual = challengeService.getAllChallenges(status, fields);

        //then:
        Assertions.assertThat(actual).hasSize(challenges.size());
        actual.forEach(a -> Assertions.assertThat(a.getStatus()).isEqualTo(Status.ACTIVE));
        actual.forEach(a -> Assertions.assertThat(a.getSummitsList()).isNotEmpty());
    }

    @Test
    void getAllChallenges_shouldReturnAllChallengesWithoutSummitList() {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = List.of("id", "name");
        List<Challenge> challenges = Instancio.ofList(Challenge.class)
                .set(field(Challenge::getStatus), Status.ACTIVE)
                .setBlank(field(Challenge::getSummitsList))
                .create();
        Mockito.when(challengeRepository.findAllByStatus(status)).thenReturn(challenges);

        //when:
        List<Challenge> actual = challengeService.getAllChallenges(status, fields);

        //then:
        Assertions.assertThat(actual).hasSize(challenges.size());
        actual.forEach(a -> Assertions.assertThat(a.getStatus()).isEqualTo(Status.ACTIVE));
        actual.forEach(a -> Assertions.assertThat(a.getSummitsList()).isEmpty());
    }

    @Test
    void getUnstartedChallenges_shouldReturnAllUnstartedChallengesWithoutSummitList() {
        //given:
        UUID userId = UUID.randomUUID();
        Status status = Status.ACTIVE;
        List<String> fields = List.of("id", "name");
        List<Challenge> challenges = Instancio.ofList(Challenge.class)
                .size(3)
                .set(field(Challenge::getStatus), Status.ACTIVE)
                .setBlank(field(Challenge::getSummitsList))
                .create();
        List<UserChallenge> userChallenges = Instancio.ofList(UserChallenge.class)
                .size(1)
                .set(field(UserChallenge::getChallenge), challenges.get(0))
                .create();
        Mockito.when(challengeRepository.findAllByStatus(status)).thenReturn(challenges);
        Mockito.when(userChallengeService.getUserChallenges(userId)).thenReturn(userChallenges);

        //when:
        List<Challenge> actual = challengeService.getUnstartedChallenges(userId, status, fields);

        //then:
        Assertions.assertThat(actual).hasSize(2);
        Assertions.assertThat(actual).extracting(Challenge::getStatus).containsOnly(Status.ACTIVE);
        Assertions.assertThat(actual).flatExtracting(Challenge::getSummitsList).isEmpty();
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
        Mockito.verify(challengeRepository).save(challenge);
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
        Mockito.verify(challengeRepository).save(challenge);
    }

    @Test
    void attachSummitToChallenge_shouldReturnChallengeWithSummit() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .setBlank(field(Summit::getChallengesList))
                .create();
        Challenge challenge = Instancio.of(Challenge.class)
                .setBlank(field(Challenge::getSummitsList))
                .create();
        Mockito.when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        Mockito.when(summitService.getSummit(summit.getId())).thenReturn(summit);

        //when:
        Challenge actual = challengeService.attachSummitToChallenge(summit.getId(), challenge.getId());

        //then:
        Assertions.assertThat(actual).extracting(Challenge::getSummitsList).isNotNull();
        Assertions.assertThat(summit).extracting(Summit::getChallengesList).isNotNull();
    }

    @Test
    void attachSummitToChallenge_shouldReturn404() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .setBlank(field(Summit::getChallengesList))
                .create();
        Challenge challenge = Instancio.of(Challenge.class)
                .setBlank(field(Challenge::getSummitsList))
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
    void detachSummitFromChallenge_shouldReturnChallengeWithoutSummit() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .setBlank(field(Summit::getChallengesList))
                .create();
        Challenge challenge = Instancio.of(Challenge.class)
                .set(field(Challenge::getSummitsList), List.of(summit))
                .create();
        Mockito.when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        Mockito.when(summitService.getSummit(summit.getId())).thenReturn(summit);

        //when:
        Challenge actual = challengeService.detachSummitFromChallenge(summit.getId(), challenge.getId());

        //then:
        Assertions.assertThat(actual).extracting(Challenge::getSummitsList, InstanceOfAssertFactories.list(Challenge.class))
                .isEmpty();
        Assertions.assertThat(summit).extracting(Summit::getChallengesList, InstanceOfAssertFactories.list(Summit.class))
                .isEmpty();
    }

    @Test
    void detachSummitFromChallenge_shouldReturn404() {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .setBlank(field(Summit::getChallengesList))
                .create();
        Challenge challenge = Instancio.of(Challenge.class)
                .set(field(Challenge::getSummitsList), List.of(summit))
                .create();
        Mockito.when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() ->
                        challengeService.detachSummitFromChallenge(summit.getId(), challenge.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Challenge", "not found")
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

        Mockito.verify(challengeRepository).findById(id);
        Mockito.verify(challengeRepository).save(challengeFromDb);
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
        Mockito.verify(challengeRepository).findById(id);
        Mockito.verify(challengeRepository).delete(challenge);
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