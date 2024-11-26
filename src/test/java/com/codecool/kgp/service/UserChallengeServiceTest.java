package com.codecool.kgp.service;

import com.codecool.kgp.entity.*;
import com.codecool.kgp.entity.enums.Role;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.repository.UserChallengeRepository;
import org.instancio.Instancio;
import org.assertj.core.api.Assertions;
import org.instancio.generators.Generators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class UserChallengeServiceTest {

    private final UserChallengeRepository userChallengeRepository = Mockito.mock();
    private final ChallengeService challengeService = Mockito.mock();
    private final UserService userService = Mockito.mock();

    private final UserChallengeService userChallengeService = new UserChallengeService(userChallengeRepository,
            challengeService, userService);


    private static User user1;


    @BeforeEach
    public void init() {
        user1 = Instancio.of(User.class)
                .set(field(User::getLogin), "bella_mystique")
                .set(field(User::getEmail), "bella@artisticvisions.com")
                .set(field(User::getName), "Bella")
                .setBlank(field(User::getUserChallenges))
                .set(field(User::getRole), Role.USER)
                .create();
    }


    @Test
    public void getUserChallenges_shouldReturnAllUserChallenges() {
        //given:
        UUID user1Id = user1.getId();

        UserChallenge challenge1 = Instancio.create(UserChallenge.class);
        UserChallenge challenge2 = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findAllByUserIdWithAllRelationships(user1Id)).thenReturn(List.of(challenge1, challenge2));

        //when:
        List<UserChallenge> actual = userChallengeService.getUserChallenges(user1Id);

        //then:
        Assertions.assertThat(actual).containsAll(List.of(challenge1, challenge2));
    }

    @Test
    public void getUserChallenge_shouldReturnUserChallenge() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);
        UUID userChallengeId = userChallenge.getId();
        Mockito.when(userChallengeRepository.findById(userChallengeId)).thenReturn(Optional.of(userChallenge));

        //when:
        UserChallenge actual = userChallengeService.getUserChallenge(userChallengeId);

        //then:
        Assertions.assertThat(actual).isEqualTo(userChallenge);
    }

    @Test
    public void getCompletedUserChallenges_shouldReturnAllCompletedUserChallenges() {
        //given:
        UUID user1Id = user1.getId();

        UserChallenge uncompletedUserChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .create();
        UserChallenge completedUserChallenge = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findAllByUserIdWithAllRelationships(user1Id))
                .thenReturn(List.of(uncompletedUserChallenge, completedUserChallenge));

        //when:
        List<UserChallenge> actual = userChallengeService.getCompletedUserChallenges(user1Id);

        //then:
        Assertions.assertThat(actual).containsExactly(completedUserChallenge);
    }

    @Test
    public void getUncompletedUserChallenges_shouldReturnAllUncompletedUserChallenges() {
        //given:
        UUID user1Id = user1.getId();

        UserChallenge uncompletedUserChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .create();
        UserChallenge completedUserChallenge = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findAllByUserIdWithAllRelationships(user1.getId()))
                .thenReturn(List.of(uncompletedUserChallenge, completedUserChallenge));

        //when:
        List<UserChallenge> actual = userChallengeService.getUncompletedUserChallenges(user1Id);

        //then:
        Assertions.assertThat(actual).containsExactly(uncompletedUserChallenge);
    }

    @Test
    void saveUserChallenge_shouldReturnSavedUserChallenge_whenChallengeWasNotStartedJet() {
        //given:
        UUID user1Id = user1.getId();
        Mockito.when(userService.findUser(user1Id)).thenReturn(user1);

        Challenge challenge = Instancio.of(Challenge.class)
                .generate(field(Challenge::getSummitsList), gen -> gen.collection().size(4))
                .create();
        Mockito.when(challengeService.findChallenge(challenge.getId())).thenReturn(challenge);

        AtomicReference<UserChallenge> savedChallenge = new AtomicReference<>();
        Mockito.when(userChallengeRepository.save(Mockito.any(UserChallenge.class))).thenAnswer(invocationOnMock -> {
            UserChallenge userChallenge = invocationOnMock.getArgument(0);
            savedChallenge.set(userChallenge);
            return userChallenge;
        });
        //when:
        UserChallenge actual = userChallengeService.saveUserChallenge(user1Id, challenge.getId());

        //then:
        ArgumentCaptor<UserChallenge> userChallengeCaptor = ArgumentCaptor.forClass(UserChallenge.class);
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitsList().size())
                .isEqualTo(challenge.getSummitsList().size());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userService).saveUser(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getUserChallenges().size()).isEqualTo(1);
        Assertions.assertThat(userCaptor.getValue().getUserChallenges().get(0)).isEqualTo(savedChallenge.get());

        Assertions.assertThat(actual).isEqualTo(savedChallenge.get());
    }

    @Test
    void saveUserChallenge_shouldReturnSavedUserChallenge_whenSameChallengeWasFinished() {
        //given:
        User user = Instancio.of(User.class)
                .generate(field(User::getUserChallenges), gen -> gen.collection().size(5))
                .create();

        Mockito.when(userService.findUser(user.getId())).thenReturn(user);

        Challenge challenge = Instancio.of(Challenge.class)
                .generate(field(Challenge::getSummitsList), gen -> gen.collection().size(4))
                .create();
        Mockito.when(challengeService.findChallenge(challenge.getId())).thenReturn(challenge);

        UserChallenge finishedUserChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getChallenge), challenge)
                .generate(field(UserChallenge::getFinishedAt), gen -> gen.temporal().localDateTime().past())
                .create();
        user.assignUserChallenge(finishedUserChallenge);

        AtomicReference<UserChallenge> savedChallenge = new AtomicReference<>();
        Mockito.when(userChallengeRepository.save(Mockito.any(UserChallenge.class))).thenAnswer(invocationOnMock -> {
            UserChallenge userChallenge = invocationOnMock.getArgument(0);
            savedChallenge.set(userChallenge);
            return userChallenge;
        });
        //when:
        UserChallenge actual = userChallengeService.saveUserChallenge(user.getId(), challenge.getId());

        //then:
        ArgumentCaptor<UserChallenge> userChallengeCaptor = ArgumentCaptor.forClass(UserChallenge.class);
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitsList().size())
                .isEqualTo(challenge.getSummitsList().size());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userService).saveUser(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getUserChallenges().size()).isEqualTo(7);

        Assertions.assertThat(actual).isEqualTo(savedChallenge.get());
    }

    @Test
    void saveUserChallenge_shouldThrowDuplicatedEntryException() {
        //given:
        UUID user1Id = user1.getId();
        Mockito.when(userService.findUser(user1Id)).thenReturn(user1);

        Challenge challenge = Instancio.create(Challenge.class);
        UserChallenge userChallenges = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getChallenge), challenge)
                .setBlank(field(UserChallenge::getFinishedAt))
                .create();
        user1.setUserChallenges(List.of(userChallenges));

        Mockito.when(challengeService.findChallenge(challenge.getId())).thenReturn(challenge);

        //when and then:
        Assertions.assertThatThrownBy(() -> userChallengeService.saveUserChallenge(user1Id, challenge.getId()))
                .isInstanceOf(DuplicateEntryException.class);
    }

    @Test
    void setUserChallengeScore_shouldSetUserScore() {
        //given:
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getScore), 1)
                .create();
        Mockito.when(userChallengeRepository.findById(userChallenge.getId()))
                .thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.setUserChallengeScore(userChallenge.getId(), 2);

        //then:
        ArgumentCaptor<UserChallenge> userChallengeCaptor = ArgumentCaptor.forClass(UserChallenge.class);
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getScore()).isEqualTo(2);
    }

    @Test
    void deleteUserChallenge_shouldDeleteUserChallenge() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.deleteUserChallenge(userChallenge.getId());

        //then:
        ArgumentCaptor<UserChallenge> userChallengeCaptor = ArgumentCaptor.forClass(UserChallenge.class);
        Mockito.verify(userChallengeRepository).delete(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue()).isEqualTo(userChallenge);
    }

    @Test
    void setSummitConquered_shouldSetConqueredAtTimeAndCompleteChallenge() {
        //given:
        int score = 1;
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .set(field(UserChallenge::getScore), 1)
                .set(field(UserChallenge::getUserSummitsList), List.of(
                        Instancio.of(UserSummit.class).set(field(UserSummit::getConqueredAt), null).create()
                )).create();
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.setSummitConquered(userChallenge.getId(),
                userChallenge.getUserSummitsList().get(0).getId(), score);

        //then:
        ArgumentCaptor<UserChallenge> userChallengeCaptor = ArgumentCaptor.forClass(UserChallenge.class);
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitsList().get(0).getConqueredAt()).isNotNull();
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitsList().get(0).getScore())
                .isEqualTo(userChallenge.getUserSummitsList().get(0).getScore());
        Assertions.assertThat(userChallengeCaptor.getValue().getScore()).isEqualTo(2);
        Assertions.assertThat(userChallengeCaptor.getValue().getFinishedAt()).isNotNull();
    }

    @Test
    void setSummitConquered_shouldNotCompleteUserChallenge() {
        //given:
        int score = 1;
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .set(field(UserChallenge::getScore), 1)
                .set(field(UserChallenge::getUserSummitsList), Instancio.ofList(UserSummit.class)
                        .size(2)
                        .set(field(UserSummit::getConqueredAt), null)
                        .create()
                ).create();
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.setSummitConquered(userChallenge.getId(),
                userChallenge.getUserSummitsList().get(0).getId(), score);

        //then:
        ArgumentCaptor<UserChallenge> userChallengeCaptor = ArgumentCaptor.forClass(UserChallenge.class);
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitsList().get(0).getConqueredAt()).isNotNull();
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitsList().get(0).getScore())
                .isEqualTo(userChallenge.getUserSummitsList().get(0).getScore());
        Assertions.assertThat(userChallengeCaptor.getValue().getScore()).isEqualTo(2);
        Assertions.assertThat(userChallengeCaptor.getValue().getFinishedAt()).isNull();
    }

    @Test
    void setSummitConquered_shouldReturn404() {
        //given:
        int score = 1;
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .setBlank(field(UserChallenge::getUserSummitsList))
                .create();
        UUID userSummitId = UUID.randomUUID();
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        //when:
        Throwable actual = Assertions.catchThrowable(() -> userChallengeService.setSummitConquered(userChallenge.getId(), userSummitId, score));

        //then:
        Assertions.assertThat(actual).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nie istnieje");
    }

    @Test
    void setSummitConquered_shouldReturn409() {
        //given:
        int score = 1;
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .generate(field(UserChallenge::getFinishedAt), gen->gen.temporal().localDateTime())
                .set(field(UserChallenge::getUserSummitsList), Instancio.ofList(UserSummit.class)
                        .size(2)
                        .generate(field(UserSummit::getConqueredAt), gen->gen.temporal().localDateTime())
                        .create()
                ).create();
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        //when:
        Throwable actual = Assertions.catchThrowable(() -> userChallengeService.setSummitConquered(userChallenge.getId(),
                userChallenge.getUserSummitsList().get(0).getId(), score));

        //then:
        Assertions.assertThat(actual).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("zakończone")
                .extracting(ex-> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void findUserChallengeById_shouldReturnUserChallenge() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        //when:
        UserChallenge actual = userChallengeService.findUserChallengeById(userChallenge.getId());

        //then:
        Assertions.assertThat(actual).isEqualTo(userChallenge);
    }

    @Test
    void findUserChallengeById_shouldReturnResponseStatusException404() {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(userChallengeRepository.findById(id)).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() ->
                        userChallengeService.findUserChallengeById(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }
}