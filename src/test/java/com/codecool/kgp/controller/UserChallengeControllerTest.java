package com.codecool.kgp.controller;

import com.codecool.kgp.config.SpringSecurityConfig;
import com.codecool.kgp.controller.dto.ChallengeSimpleDto;
import com.codecool.kgp.controller.dto.UserChallengeDto;
import com.codecool.kgp.controller.dto.UserSummitDto;
import com.codecool.kgp.entity.*;
import com.codecool.kgp.entity.enums.Role;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.mappers.UserChallengeMapper;
import com.codecool.kgp.config.WithMockCustomUser;
import com.codecool.kgp.repository.UserChallengeRepository;
import com.codecool.kgp.repository.UserRepository;
import com.codecool.kgp.service.CustomUserDetailsService;
import com.codecool.kgp.service.UserChallengeService;
import com.codecool.kgp.validators.UserChallengeValidator;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SpringSecurityConfig.class, CustomUserDetailsService.class})
@WebMvcTest(UserChallengeController.class)
class UserChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserChallengeService userChallengeService;

    @MockBean
    UserChallengeValidator userChallengeValidator;

    @MockBean
    UserChallengeMapper userChallengeMapper;

    @MockBean
    ChallengeMapper challengeMapper;

    @MockBean
    UserChallengeRepository userChallengeRepository;

    @MockBean
    UserRepository userRepository;


    @ParameterizedTest
    @ValueSource(strings = {
            "?filter=",
            "?filter=all",
            "?filter=ALL",
            "?filter=All",
            "",
    })
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void getUserChallenges_shouldReturnAllUserChallenges(String query) throws Exception {
        // given:
        User user = new User("bella_mystique", "password", "email", Role.USER);
        setId(user, UUID.fromString("7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7"));

        Set<Challenge> challenges = Instancio.ofSet(Challenge.class).size(8)
                .generate(field(Challenge::getSummitsSet), gen -> gen.collection().minSize(3))
                .create();
        challenges.forEach(ch -> ch.getSummitsSet().forEach(s -> s.setChallengesSet(Set.of(ch))));
        List<UserChallenge> userChallenges = challenges.stream().map(ch -> new UserChallenge(user, ch)).toList();

        Mockito.when(userChallengeService.getUserChallenges(user.getId())).thenReturn(userChallenges);
        mockMapEntityToDto();

        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/me/user-challenges/" + query));

        // then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(8))
                .andExpect(jsonPath("$[0].id").value(userChallenges.get(0).getId().toString()));
        Mockito.verify(userChallengeService).getUserChallenges(user.getId());
        Mockito.verify(userChallengeMapper, Mockito.times(8)).mapEntityToDto(any());
    }

    @Test
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void getUserChallenges_shouldReturnAllCompletedUserChallenges() throws Exception {
        // given:
        User user = new User("bella_mystique", "password", "email", Role.USER);
        setId(user, UUID.fromString("7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7"));

        Set<Challenge> challenges = Instancio.ofSet(Challenge.class).size(8)
                .generate(field(Challenge::getSummitsSet), gen -> gen.collection().minSize(3))
                .create();
        challenges.forEach(ch -> ch.getSummitsSet().forEach(s -> s.setChallengesSet(Set.of(ch))));
        List<UserChallenge> userChallenges = challenges.stream().map(ch -> new UserChallenge(user, ch)).limit(3).toList();
        userChallenges.forEach(uch -> uch.setFinishedAt(uch.getStartedAt().plusDays(1)));

        Mockito.when(userChallengeService.getCompletedUserChallenges(user.getId())).thenReturn(userChallenges);
        mockMapEntityToDto();

        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/me/user-challenges/?filter=completed"));

        // then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].id").value(userChallenges.get(0).getId().toString()))
                .andExpect(jsonPath("$[0].finishedAt").isNotEmpty())
                .andExpect(jsonPath("$[0].finishedAt").value(userChallenges.get(0).getFinishedAt().format(DateTimeFormatter.ISO_DATE_TIME)));
        Mockito.verify(userChallengeService).getCompletedUserChallenges(user.getId());
        Mockito.verify(userChallengeMapper, Mockito.times(3)).mapEntityToDto(any());
    }

    @Test
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void getUserChallenges_shouldReturnAllUncompletedUserChallenges() throws Exception {
        // given:
        User user = new User("bella_mystique", "password", "email", Role.USER);
        setId(user, UUID.fromString("7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7"));

        Set<Challenge> challenges = Instancio.ofSet(Challenge.class).size(8)
                .generate(field(Challenge::getSummitsSet), gen -> gen.collection().minSize(3))
                .create();
        challenges.forEach(ch -> ch.getSummitsSet().forEach(s -> s.setChallengesSet(Set.of(ch))));
        List<UserChallenge> userChallenges = challenges.stream().map(ch -> new UserChallenge(user, ch)).limit(5).toList();

        Mockito.when(userChallengeService.getUncompletedUserChallenges(user.getId())).thenReturn(userChallenges);
        mockMapEntityToDto();

        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/me/user-challenges/?filter=uncompleted"));

        // then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(5))
                .andExpect(jsonPath("$[0].id").value(userChallenges.get(0).getId().toString()))
                .andExpect(jsonPath("$[0].finishedAt").isEmpty());
        Mockito.verify(userChallengeService).getUncompletedUserChallenges(user.getId());
        Mockito.verify(userChallengeMapper, Mockito.times(5)).mapEntityToDto(any());
    }

    @Test
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void getUserChallenges_shouldReturn400() throws Exception {
        // given:

        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/me/user-challenges/?filter=unknown"));

        // then:
        response.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(username = "chris_crescendo", id = "c82f1e44-12a2-4e9b-acd9-5b7a019c6d8c")
    void getUserChallenge_shouldReturnUserChallenge() throws Exception {
        // given:
        User user = new User("chris_crescendo", "crescendo2024", "email", Role.USER);
        setId(user, UUID.fromString("c82f1e44-12a2-4e9b-acd9-5b7a019c6d8c"));

        Challenge challenge = Instancio.create(Challenge.class);
        UserChallenge userChallenge = new UserChallenge(user, challenge);

        Mockito.when(userChallengeService.getUserChallenge(userChallenge.getId())).thenReturn(userChallenge);
        mockMapEntityToDto();

        //when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/me/user-challenges/" + userChallenge.getId()));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userChallenge.getId().toString()));
        Mockito.verify(userChallengeService, Mockito.times(1)).getUserChallenge(userChallenge.getId());
        Mockito.verify(userChallengeMapper, Mockito.times(1)).mapEntityToDto(userChallenge);
    }

    @Test
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void addUserChallenge_shouldReturnUserChallenge() throws Exception {
        // given:
        UUID userId = UUID.fromString("7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7");
        User user = Instancio.create(User.class);
        setId(user, userId);

        UUID conquerorChallengeId = UUID.fromString("4c39c496-ff63-4c8a-bad4-47d6a97053e7");
        Challenge challenge = new Challenge("Conqueror", "Conquer all summits", Status.ACTIVE);
        setId(challenge, conquerorChallengeId);

        UserChallenge userChallenge = new UserChallenge(user, challenge);
        Mockito.when(userChallengeService.saveUserChallenge(userId, conquerorChallengeId)).thenReturn(userChallenge);
        mockMapEntityToDto();

        // when:
        ResultActions response = mockMvc.perform(post("/api/v1/users/me/user-challenges/add-new/{id}", conquerorChallengeId));


        // then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.challengeId").value(conquerorChallengeId.toString()));
    }

    @Test
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void conquerSummit_shouldReturnUserChallengeWithConqueredSummit() throws Exception {
        // given:
        int score = 100;

        UUID userId = UUID.fromString("7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7");
        User user = Instancio.create(User.class);
        setId(user, userId);

        UUID conquerorChallengeId = UUID.fromString("4c39c496-ff63-4c8a-bad4-47d6a97053e7");
        Challenge challenge = new Challenge("Conqueror", "Conquer all summits", Status.ACTIVE);
        setId(challenge, conquerorChallengeId);

        Summit summit = Instancio.create(Summit.class);
        challenge.addSummit(summit);

        UserChallenge userChallenge = new UserChallenge(user, challenge);
        UserSummit userSummit = new UserSummit(userChallenge, summit);
        userSummit.setConqueredAt(LocalDateTime.now());
        userChallenge.assignUserSummit(userSummit);
        userChallenge.setScore(score);

        Mockito.when(userChallengeService.setSummitConquered(userChallenge.getId(), userSummit.getId(), score)).thenReturn(userChallenge);
        mockMapEntityToDto();

        // when:
        ResultActions response = mockMvc.perform(post("/api/v1/users/me/user-challenges/{userChallengeId}/user-summits/{userSummitId}/conquer/{score}",
                userChallenge.getId(), userSummit.getId(), score));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.challengeId").value(conquerorChallengeId.toString()))
                .andExpect(jsonPath("$.userSummitsSet.size()").value(1))
                .andExpect(jsonPath("$.userSummitsSet[0].conqueredAt").isNotEmpty())
                .andExpect(jsonPath("$.score").value(score));
        Mockito.verify(userChallengeValidator).validateScore(UserSummit.class, userSummit.getId(), score);
        Mockito.verify(userChallengeService).setSummitConquered(userChallenge.getId(), userSummit.getId(), score);
    }

    @Test
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void updateUserChallengeScore_shouldUpdateScore() throws Exception {
        // given:
        int score = 100;

        UUID userId = UUID.fromString("7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7");
        User user = Instancio.create(User.class);
        setId(user, userId);

        UUID conquerorChallengeId = UUID.fromString("4c39c496-ff63-4c8a-bad4-47d6a97053e7");
        Challenge challenge = new Challenge("Conqueror", "Conquer all summits", Status.ACTIVE);
        setId(challenge, conquerorChallengeId);

        UserChallenge userChallenge = new UserChallenge(user, challenge);

        // when:
        ResultActions response = mockMvc.perform(patch("/api/v1/users/me/user-challenges/{challengeId}/update-score/{score}", userChallenge.getId(), score));

        // then:
        response.andExpect(status().isOk());
        Mockito.verify(userChallengeValidator).validateScore(UserChallenge.class, userChallenge.getId(), score);
        Mockito.verify(userChallengeService).setUserChallengeScore(userChallenge.getId(), score);
    }

    @Test
    @WithMockCustomUser(username = "bella_mystique", id = "7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7")
    void deleteUserChallenge_shouldDeleteUserChallenge() throws Exception {
        // given:
        UUID userId = UUID.fromString("7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7");
        User user = Instancio.create(User.class);
        setId(user, userId);

        UUID conquerorChallengeId = UUID.fromString("4c39c496-ff63-4c8a-bad4-47d6a97053e7");
        Challenge challenge = new Challenge("Conqueror", "Conquer all summits", Status.ACTIVE);
        setId(challenge, conquerorChallengeId);

        UserChallenge userChallenge = new UserChallenge(user, challenge);

        // when:
        ResultActions response = mockMvc.perform(delete("/api/v1/users/me/user-challenges/{challengeId}", userChallenge.getId()));

        // then:
        response.andExpect(status().isOk());
        Mockito.verify(userChallengeService).deleteUserChallenge(userChallenge.getId());
    }


    private <T> void setId(T object, UUID id) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField("id");
        field.setAccessible(true);
        field.set(object, id);
    }

    private void mockMapEntityToDto() {
        Mockito.when(userChallengeMapper.mapEntityToDto(any())).thenAnswer(invocationOnMock -> {
            UserChallenge uch = invocationOnMock.getArgument(0);
            Challenge ch = uch.getChallenge();
            Set<UserSummitDto> userSummitDto = uch.getUserSummitsSet().stream().map(us -> {
                Summit s = us.getSummit();
                Set<ChallengeSimpleDto> challengeSimpleDto = s.getChallengesSet().stream()
                        .map(sch -> new ChallengeSimpleDto(sch.getId(), sch.getName(), sch.getStatus())).collect(Collectors.toSet());
                return new UserSummitDto(us.getId(), s.getId(), us.getUserChallenge().getId(), us.getConqueredAt(),
                        challengeSimpleDto, s.getName(), s.getCoordinatesArray(), s.getMountainRange(), s.getMountainChain(),
                        s.getHeight(), s.getDescription(), s.getGuideNotes(), s.getScore(), s.getStatus().toString());
            }).collect(Collectors.toSet());

            return new UserChallengeDto(
                    uch.getId(),
                    uch.getUser().getId(),
                    ch.getId(),
                    ch.getDescription(),
                    ch.getName(),
                    ch.getStatus(),
                    uch.getStartedAt(),
                    uch.getFinishedAt(),
                    uch.getScore(),
                    userSummitDto
            );
        });
    }
}
