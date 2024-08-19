package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.UserChallengeDto;
import com.codecool.kgp.entity.*;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.mappers.UserChallengeMapper;
import com.codecool.kgp.repository.ChallengeRepository;
import com.codecool.kgp.repository.UserChallengeRepository;
import com.codecool.kgp.repository.UserRepository;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class UserChallengeServiceTest {

    private final UserChallengeRepository userChallengeRepository = Mockito.mock();
    private final UserChallengeMapper userChallengeMapper = Mockito.mock();
    private final UserRepository userRepository = Mockito.mock();
    private final ChallengeRepository challengeRepository = Mockito.mock();
    private final ChallengeMapper challengeMapper = Mockito.mock();

    private final UserChallengeService userChallengeService = new UserChallengeService(userRepository,
            challengeRepository, userChallengeRepository, userChallengeMapper, challengeMapper
    );

    private static String login1;

    private static User user1;

    @Captor
    private ArgumentCaptor<UserChallenge> userChallengeCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeAll
    public static void init() {
        login1 = "Adam";
        user1 = Instancio.of(User.class)
                .set(field(User::getLogin), login1)
                .create();
    }


    @Test
    public void getUserChallenges_shouldAllUserChallenges() {
        //given:
        Mockito.when(userRepository.findByLogin(login1)).thenReturn(Optional.of(user1));

        UserChallenge challenge1 = Instancio.create(UserChallenge.class);
        UserChallenge challenge2 = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findAllByUserId(user1.getId()))
                .thenReturn(List.of(challenge1, challenge2));

        UserChallengeDto challenge1dto = Instancio.create(UserChallengeDto.class);
        UserChallengeDto challenge2dto = Instancio.create(UserChallengeDto.class);
        Mockito.when(userChallengeMapper.mapEntityToDto(challenge1)).thenReturn(challenge1dto);
        Mockito.when(userChallengeMapper.mapEntityToDto(challenge2)).thenReturn(challenge2dto);

        //when:
        List<UserChallengeDto> actual = userChallengeService.getUserChallenges(login1);

        //then:
        Assertions.assertThat(actual).containsAll(List.of(challenge1dto, challenge2dto));
    }

    @Test
    public void getUserChallenges_shouldReturnResponseStatusException() {
        //given:
        Mockito.when(userRepository.findByLogin(login1)).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() -> userChallengeService.getUserChallenges(login1))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getUserChallenge_shouldReturnUserChallengeDto() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        UserChallengeDto userChallengeDto = Instancio.create(UserChallengeDto.class);
        Mockito.when(userChallengeMapper.mapEntityToDto(userChallenge)).thenReturn(userChallengeDto);

        //when:
        UserChallengeDto actual = userChallengeService.getUserChallenge(userChallenge.getId());

        //then:
        Assertions.assertThat(actual).isEqualTo(userChallengeDto);
    }

    @Test
    public void getCompletedUserChallenges_shouldAllCompletedUserChallenges() {
        //given:
        Mockito.when(userRepository.findByLogin(login1)).thenReturn(Optional.of(user1));

        UserChallenge challenge1 = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .create();
        UserChallenge challenge2 = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findAllByUserId(user1.getId()))
                .thenReturn(List.of(challenge1, challenge2));

        UserChallengeDto challenge1dto = Instancio.create(UserChallengeDto.class);
        UserChallengeDto challenge2dto = Instancio.create(UserChallengeDto.class);
        Mockito.when(userChallengeMapper.mapEntityToDto(challenge1)).thenReturn(challenge1dto);
        Mockito.when(userChallengeMapper.mapEntityToDto(challenge2)).thenReturn(challenge2dto);

        //when:
        List<UserChallengeDto> actual = userChallengeService.getCompletedUserChallenges(login1);

        //then:
        Assertions.assertThat(actual).containsExactly(challenge2dto);
    }

    @Test
    public void getActiveUserChallenges_shouldAllCompletedUserChallenges() {
        //given:
        Mockito.when(userRepository.findByLogin(login1)).thenReturn(Optional.of(user1));

        UserChallenge challenge1 = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .create();
        UserChallenge challenge2 = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findAllByUserId(user1.getId()))
                .thenReturn(List.of(challenge1, challenge2));

        UserChallengeDto challenge1dto = Instancio.create(UserChallengeDto.class);
        UserChallengeDto challenge2dto = Instancio.create(UserChallengeDto.class);
        Mockito.when(userChallengeMapper.mapEntityToDto(challenge1)).thenReturn(challenge1dto);
        Mockito.when(userChallengeMapper.mapEntityToDto(challenge2)).thenReturn(challenge2dto);

        //when:
        List<UserChallengeDto> actual = userChallengeService.getActiveUserChallenges(login1);

        //then:
        Assertions.assertThat(actual).containsExactly(challenge1dto);
    }

    @Test
    void saveUserChallenge_shouldReturnUserChallengeDtoFromDb() {
        //given:
        Mockito.when(userRepository.findByLogin(login1)).thenReturn(Optional.ofNullable(user1));

        Challenge challenge = Instancio.of(Challenge.class)
                .generate(field(Challenge::getSummitList), gen -> gen.collection().size(4))
                .create();
        Mockito.when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));

        UserChallenge userChallenge = Instancio.create(UserChallenge.class);

        Mockito.when(userChallengeRepository.save(Mockito.any(UserChallenge.class))).thenReturn(userChallenge);

        UserChallengeDto userChallengeDto = Instancio.create(UserChallengeDto.class);
        Mockito.when(userChallengeMapper.mapEntityToDto(userChallenge)).thenReturn(userChallengeDto);

        //when:
        UserChallengeDto actual = userChallengeService.saveUserChallenge(login1, challenge.getId());


        //then:
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitList().size())
                .isEqualTo(challenge.getSummitList().size());

        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertThat(userCaptor.getValue().getUserChallenges()).contains(userChallenge);

        Assertions.assertThat(actual).isEqualTo(userChallengeDto);
    }

    @Test
    void saveUserChallenge_shouldReturn404() {
        //given:
        UUID invalidId = UUID.randomUUID();
        Mockito.when(challengeRepository.findById(invalidId)).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() ->
                        userChallengeService.saveUserChallenge(login1, invalidId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void setUserChallengeScore_shouldSetUserScore() {
        //given:
        int score = 2;
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getScore), 1)
                .create();
        Mockito.when(userChallengeRepository.findById(userChallenge.getId()))
                .thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.setUserChallengeScore(userChallenge.getId(), score);

        //then:
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getScore()).isEqualTo(score);
    }

    @Test
    void deleteUserChallenge_shouldDeleteUserChallenge() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findById(userChallenge.getId()))
                .thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.deleteUserChallenge(userChallenge.getId());

        //then:
        Mockito.verify(userChallengeRepository).delete(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue()).isEqualTo(userChallenge);
    }

    @Test
    void setSummitConquered_shouldSetConqueredAtTimeAndFinishChallenge() {
        //given:
        int score = 1;
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .set(field(UserChallenge::getFinishedAt), null)
                .set(field(UserChallenge::getScore), 1)
                .set(field(UserChallenge::getUserSummitList), List.of(
                        Instancio.of(UserSummit.class).set(field(UserSummit::getConqueredAt), null).create()
                )).create();
        System.out.println(userChallenge.getScore());
        Mockito.when(userChallengeRepository.findById(userChallenge.getId()))
                .thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.setSummitConquered(userChallenge.getId(),
                userChallenge.getUserSummitList().get(0).getId(), score);

        //then:
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitList().get(0).getConqueredAt()).isNotNull();
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitList().get(0).getScore())
                .isEqualTo(userChallenge.getUserSummitList().get(0).getScore());
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
                .set(field(UserChallenge::getUserSummitList), Instancio.ofList(UserSummit.class)
                        .size(2)
                        .set(field(UserSummit::getConqueredAt), null)
                        .create()
                ).create();
        Mockito.when(userChallengeRepository.findById(userChallenge.getId()))
                .thenReturn(Optional.of(userChallenge));

        //when:
        userChallengeService.setSummitConquered(userChallenge.getId(),
                userChallenge.getUserSummitList().get(0).getId(), score);

        //then:
        Mockito.verify(userChallengeRepository).save(userChallengeCaptor.capture());
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitList().get(0).getConqueredAt()).isNotNull();
        Assertions.assertThat(userChallengeCaptor.getValue().getUserSummitList().get(0).getScore())
                .isEqualTo(userChallenge.getUserSummitList().get(0).getScore());
        Assertions.assertThat(userChallengeCaptor.getValue().getScore()).isEqualTo(2);
        Assertions.assertThat(userChallengeCaptor.getValue().getFinishedAt()).isNull();
    }

    @Test
    void getUserChallengeById_shouldReturnUserChallenge() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);
        Mockito.when(userChallengeRepository.findById(userChallenge.getId())).thenReturn(Optional.of(userChallenge));

        //when:
        UserChallenge actual = userChallengeService.getUserChallengeById(userChallenge.getId());

        //then:
        Assertions.assertThat(actual).isEqualTo(userChallenge);
    }

    @Test
    void getUserChallengeById_shouldReturnResponseStatusException404() {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(userChallengeRepository.findById(id)).thenReturn(Optional.empty());

        //when and then:
        Assertions.assertThatThrownBy(() ->
                        userChallengeService.getUserChallengeById(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }
}