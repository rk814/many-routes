package com.codecool.kgp.controller;

import com.codecool.kgp.config.SpringSecurityConfig;
import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.repository.ChallengeRepository;
import com.codecool.kgp.repository.SummitRepository;
import com.codecool.kgp.service.ChallengeService;
import com.codecool.kgp.service.CustomUserDetailsService;
import com.google.gson.Gson;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static com.codecool.kgp.config.SpringSecurityConfig.USER;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SpringSecurityConfig.class)
@WebMvcTest(controllers = ChallengeController.class)
class ChallengeControllerTest {

    private final Gson gson = new Gson();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ChallengeService challengeService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    ChallengeRepository challengeRepository;

    @MockBean
    SummitRepository summitRepository;


    @Test
    @WithMockUser(roles = ADMIN)
    void getChallenges_shouldReturnAllActiveChallenges() throws Exception {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = null;
        List<ChallengeDto> challenges = Instancio.ofList(ChallengeDto.class).size(3).set(field(ChallengeDto::status), Status.ACTIVE).create();
        Mockito.when(challengeService.getAllChallenges(status, fields)).thenReturn(challenges);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$[2].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].summits").exists());

        verify(challengeService).getAllChallenges(Status.ACTIVE, null);
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void getChallenge_shouldReturnAllActiveChallengesWithSpecifyFieldsOnly() throws Exception {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = List.of("id", "status");
        List<ChallengeDto> challenges = Instancio.ofList(ChallengeDto.class).size(1)
                .set(field(ChallengeDto::status), Status.ACTIVE)
                .ignore(field(ChallengeDto::summits))
                .ignore(field(ChallengeDto::name))
                .ignore(field(ChallengeDto::description))
                .create();
        Mockito.when(challengeService.getAllChallenges(status, fields)).thenReturn(challenges);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges?fields=id,status"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].summits").doesNotExist())
                .andExpect(jsonPath("$[0].name").doesNotExist())
                .andExpect(jsonPath("$[0].description").doesNotExist());

        verify(challengeService).getAllChallenges(Status.ACTIVE, fields);
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void getChallenge_shouldReturnAllRemovedChallenges() throws Exception {
        //given:
        Status status = Status.REMOVED;
        List<String> fields = null;
        List<ChallengeDto> challenges = Instancio.ofList(ChallengeDto.class).size(1)
                .set(field(ChallengeDto::status), Status.REMOVED)
                .create();
        Mockito.when(challengeService.getAllChallenges(status, fields)).thenReturn(challenges);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges?status=REMOVED"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("REMOVED"));

        verify(challengeService).getAllChallenges(Status.REMOVED, fields);
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void getChallenge_shouldThrow404() throws Exception {
        //when:
        var response = mockMvc.perform(get("/api/v1/challenges?status=UNKNOWN"));

        //then:
        response.andExpect(status().isBadRequest());
    }

    @Test
    void getChallenge_shouldThrow401() throws Exception {
        //when:
        var response = mockMvc.perform(get("/api/v1/challenges"));

        //then:
        response.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void addChallenge_shouldReturnNewAddedChallenge() throws Exception {
        //given:
        ChallengeRequestDto requestDto = Instancio.create(ChallengeRequestDto.class);
        ChallengeDto dto = Instancio.create(ChallengeDto.class);
        Mockito.when(challengeService.addNewChallenge(requestDto)).thenReturn(dto);

        //when:
        var response = mockMvc.perform(post("/api/v1/challenges/add-new")
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.id().toString()));

        verify(challengeService).addNewChallenge(requestDto);
    }

    @Test
    @WithMockUser(roles = USER)
    void addChallenge_shouldReturn401() throws Exception {
        //given:
        ChallengeRequestDto requestDto = Instancio.create(ChallengeRequestDto.class);

        //when:
        var response = mockMvc.perform(post("/api/v1/challenges/add-new")
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void attachSummit_shouldReturnChallengeWithAddedSummit() throws Exception {
        //given:
        Challenge challenge = Instancio.of(Challenge.class)
                .setBlank(field(Challenge::getSummitList))
                .create();
        Summit summit = Instancio.create(Summit.class);
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .set(field(ChallengeDto::summits), List.of(new SummitSimpleDto(summit.getId(), summit.getName(),
                        summit.getMountainRange(), summit.getMountains(), summit.getHeight(), summit.getStatus().toString())))
                .create();
        Mockito.when(challengeService.attachSummitToChallenge(summit.getId(), challenge.getId())).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(post(
                "/api/v1/challenges/" + challenge.getId() + "/attach-summit/" + summit.getId()));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.summits.size()").value(1))
                .andExpect(jsonPath("$.summits[0].id").value(summit.getId().toString()));

        verify(challengeService).attachSummitToChallenge(summit.getId(), challenge.getId());
    }

    @Test
    @WithMockUser(roles = USER)
    void attachSummit_shouldReturn403() throws Exception {
        //when:
        var response = mockMvc.perform(post(
                "/api/v1/challenges/" + UUID.randomUUID() + "/attach-summit/" +UUID.randomUUID()));

        //then:
        response.andExpect(status().isForbidden());
    }
}