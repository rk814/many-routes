package pl.manyroutes.controller;

import pl.manyroutes.config.SpringSecurityConfig;
import pl.manyroutes.controller.dto.ChallengeDto;
import pl.manyroutes.controller.dto.ChallengeRequestDto;
import pl.manyroutes.controller.dto.SummitSimpleDto;
import pl.manyroutes.entity.Challenge;
import pl.manyroutes.entity.Summit;
import pl.manyroutes.entity.enums.Status;
import pl.manyroutes.mappers.ChallengeMapper;
import pl.manyroutes.config.WithMockCustomUser;
import pl.manyroutes.repository.ChallengeRepository;
import pl.manyroutes.repository.SummitRepository;
import pl.manyroutes.repository.UserRepository;
import pl.manyroutes.service.ChallengeService;
import pl.manyroutes.service.CustomUserDetailsService;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.manyroutes.config.SpringSecurityConfig.ADMIN;
import static pl.manyroutes.config.SpringSecurityConfig.USER;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SpringSecurityConfig.class, CustomUserDetailsService.class})
@WebMvcTest(controllers = ChallengeController.class)
class ChallengeControllerTest {

    private final Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChallengeService challengeService;

    @MockBean
    private ChallengeMapper challengeMapper;

    @MockBean
    private ChallengeRepository challengeRepository;

    @MockBean
    private SummitRepository summitRepository;

    @MockBean
    private UserRepository userRepository;


    @ParameterizedTest
    @CsvSource(value = {
            "?filter=&status=ACTIVE",
            "?filter=&status=active",
            "?filter=&status=Active",
            "?filter=all&status=ACTIVE",
            "?filter=All&status=ACTIVE",
            "?status=ACTIVE&filter=All",
            "''",
            "?filter=All",
    })
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getChallenges_shouldReturnAllActiveChallenges(String query) throws Exception {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = null;
        List<Challenge> challenges = Instancio.ofList(Challenge.class).size(3)
                .set(field(Challenge::getStatus), Status.ACTIVE).create();
        List<ChallengeDto> challengesDto = Instancio.ofList(ChallengeDto.class).size(3)
                .set(field(ChallengeDto::status), Status.ACTIVE)
                .create();
        Mockito.when(challengeService.getAllChallenges(eq(status), any())).thenReturn(challenges);

        AtomicInteger counter = new AtomicInteger(0);
        Mockito.when(challengeMapper.mapEntityToDto(any(Challenge.class), any())).thenAnswer(invocationOnMock ->
                challengesDto.get(counter.getAndIncrement())
        );

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/" + query));

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
                .andExpect(jsonPath("$[0].summitsSet").exists());

        Mockito.verify(challengeService).getAllChallenges(eq(Status.ACTIVE), argThat(Objects::nonNull));
        Mockito.verify(challengeMapper, Mockito.times(3)).mapEntityToDto(any(Challenge.class), argThat(Objects::nonNull));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "?filter=non&status=ACTIVE",
            "?filter=Unstarted&status=xxx",
            "?filter=off&status=deActive",
            "?filter=off",
            "?status=unActive",
    })
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getChallenges_shouldReturn404(String query) throws Exception {
        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/" + query));

        //then:
        response.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getChallenges_shouldReturnAllActiveChallengesWithSpecifiedFieldsOnly() throws Exception {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = List.of("id", "status");
        List<Challenge> challenges = Instancio.ofList(Challenge.class).size(1)
                .set(field(Challenge::getStatus), Status.ACTIVE)
                .ignore(field(Challenge::getSummitsSet))
                .ignore(field(Challenge::getName))
                .ignore(field(Challenge::getDescription))
                .create();
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .set(field(ChallengeDto::status), Status.ACTIVE)
                .ignore(field(ChallengeDto::summitsSet))
                .ignore(field(ChallengeDto::name))
                .ignore(field(ChallengeDto::description))
                .create();
        Mockito.when(challengeService.getAllChallenges(status, fields)).thenReturn(challenges);
        Mockito.when(challengeMapper.mapEntityToDto(any(Challenge.class), eq(fields))).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/?fields=id,status"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].summits").doesNotExist())
                .andExpect(jsonPath("$[0].name").doesNotExist())
                .andExpect(jsonPath("$[0].description").doesNotExist());

        Mockito.verify(challengeService).getAllChallenges(Status.ACTIVE, fields);
        Mockito.verify(challengeMapper).mapEntityToDto(challenges.get(0), fields);
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getChallenges_shouldReturnAllActiveChallengesWithSummitsFields() throws Exception {
        //given:
        List<String> fields = List.of("summitsSet");
        List<Challenge> challenges = Instancio.ofList(Challenge.class).size(1)
                .set(field(Challenge::getStatus), Status.ACTIVE)
                .create();
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .ignore(field(ChallengeDto::id))
                .ignore(field(ChallengeDto::status))
                .ignore(field(ChallengeDto::name))
                .ignore(field(ChallengeDto::description))
                .create();
        Mockito.when(challengeService.getAllChallenges(any(Status.class), eq(fields))).thenReturn(challenges);
        Mockito.when(challengeMapper.mapEntityToDto(any(Challenge.class), eq(fields))).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/" + "?fields=" + String.join(",", fields)));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].summitsSet").exists())
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].status").doesNotExist())
                .andExpect(jsonPath("$[0].name").doesNotExist())
                .andExpect(jsonPath("$[0].description").doesNotExist());

        Mockito.verify(challengeService).getAllChallenges(Status.ACTIVE, fields);
        Mockito.verify(challengeMapper).mapEntityToDto(challenges.get(0), fields);
    }


    @ParameterizedTest
    @ValueSource(strings = {"unstarted", "UNSTARTED", "Unstarted"})
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getChallenges_shouldReturnAllActiveUnstartedChallengesWithoutSummitsFields(String filter) throws Exception {
        //given:
        UUID userId = UUID.fromString("5c39c496-ff63-4c8a-bad4-47d6a97053e7");
        List<String> fields = null;

        List<Challenge> challenges = Instancio.ofList(Challenge.class).size(8).create();
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .set(field(ChallengeDto::status), Status.ACTIVE).create();

        Mockito.when(challengeService.getUnstartedChallenges(eq(userId), any(Status.class), any())).thenReturn(challenges);
        Mockito.when(challengeMapper.mapEntityToDto(any(Challenge.class), any())).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/" + "?filter=" + filter));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(8));
        response.andExpect(jsonPath("$[*].status").value(Matchers.everyItem(Matchers.is("ACTIVE"))));
        Mockito.verify(challengeService).getUnstartedChallenges(eq(userId), eq(Status.ACTIVE), argThat(arg -> arg != null));
        Mockito.verify(challengeMapper, Mockito.times(8)).mapEntityToDto(any(Challenge.class), argThat(arg -> arg != null));
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getChallenges_shouldReturnAllRemovedChallenges() throws Exception {
        //given:
        Status status = Status.REMOVED;
        List<String> fields = null;
        List<Challenge> challenges = Instancio.ofList(Challenge.class).size(1)
                .set(field(Challenge::getStatus), Status.REMOVED)
                .create();
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .set(field(ChallengeDto::status), Status.REMOVED)
                .create();
        Mockito.when(challengeService.getAllChallenges(eq(status), any())).thenReturn(challenges);
        Mockito.when(challengeMapper.mapEntityToDto(eq(challenges.get(0)), any())).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/?status=REMOVED"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("REMOVED"));

        Mockito.verify(challengeService).getAllChallenges(eq(Status.REMOVED), argThat(Objects::nonNull));
        Mockito.verify(challengeMapper).mapEntityToDto(eq(challenges.get(0)), argThat(Objects::nonNull));
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getChallenges_shouldThrow404() throws Exception {
        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/?status=UNKNOWN"));

        //then:
        response.andExpect(status().isBadRequest());
    }

    @Test
    void getChallenges_shouldThrow401() throws Exception {
        //when:
        var response = mockMvc.perform(get("/api/v1/challenges"));

        //then:
        response.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = USER)
    void getChallenge_shouldReturnRequestedChallenge() throws Exception {
        //given:
        UUID id = UUID.randomUUID();
        Challenge challenge = Instancio.create(Challenge.class);
        ChallengeDto challengeDto = Instancio.create(ChallengeDto.class);
        Mockito.when(challengeService.getChallenge(id)).thenReturn(challenge);
        Mockito.when(challengeMapper.mapEntityToDto(challenge)).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/" + id));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(challengeDto.id().toString()));
    }

    @Test
    @WithMockUser(roles = USER)
    void getChallenge_shouldReturn404() throws Exception {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(challengeService.getChallenge(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when:
        var response = mockMvc.perform(get("/api/v1/challenges/" + id));

        //then:
        response.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void addChallenge_shouldReturnNewAddedChallenge() throws Exception {
        //given:
        ChallengeRequestDto requestDto = new ChallengeRequestDto(
                "test name",
                "test description",
                Status.ACTIVE
        );
        Challenge challenge = Instancio.create(Challenge.class);
        Challenge savedChallenge = Instancio.create(Challenge.class);
        ChallengeDto dto = Instancio.create(ChallengeDto.class);
        Mockito.when(challengeMapper.mapRequestDtoToEntity(requestDto)).thenReturn(challenge);
        Mockito.when(challengeService.addNewChallenge(challenge)).thenReturn(savedChallenge);
        Mockito.when(challengeMapper.mapEntityToDto(savedChallenge)).thenReturn(dto);

        //when:
        var response = mockMvc.perform(post("/api/v1/challenges/add-new")
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.id().toString()));

        verify(challengeService).addNewChallenge(challenge);
    }

    @Test
    @WithMockUser(roles = USER)
    void addChallenge_shouldReturn403() throws Exception {
        //given:
        ChallengeRequestDto requestDto = new ChallengeRequestDto(
                "test name",
                "test description",
                Status.ACTIVE
        );

        //when:
        var response = mockMvc.perform(post("/api/v1/challenges/add-new")
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = USER)
    void addChallenge_shouldReturn404() throws Exception {
        //given:
        ChallengeRequestDto requestDto = new ChallengeRequestDto(
                "",
                "test description",
                Status.ACTIVE
        );

        //when:
        var response = mockMvc.perform(post("/api/v1/challenges/add-new")
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void attachSummit_shouldReturnChallengeWithAddedSummit() throws Exception {
        //given:
        Challenge challenge = Instancio.of(Challenge.class)
                .setBlank(field(Challenge::getSummitsSet))
                .create();
        Summit summit = Instancio.create(Summit.class);
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .set(field(ChallengeDto::summitsSet), Set.of(new SummitSimpleDto(summit.getId(), summit.getName(),
                        summit.getMountainRange(), summit.getMountainChain(), summit.getHeight(), summit.getStatus().toString())))
                .create();
        Mockito.when(challengeService.attachSummitToChallenge(summit.getId(), challenge.getId())).thenReturn(challenge);
        Mockito.when(challengeMapper.mapEntityToDto(challenge)).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(post(
                "/api/v1/challenges/" + challenge.getId() + "/attach-summit/" + summit.getId()));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.summitsSet.size()").value(1))
                .andExpect(jsonPath("$.summitsSet[*].id").value(Matchers.containsInAnyOrder(summit.getId().toString())));

        verify(challengeService).attachSummitToChallenge(summit.getId(), challenge.getId());
    }

    @Test
    @WithMockUser(roles = USER)
    void attachSummit_shouldReturn403() throws Exception {
        //when:
        var response = mockMvc.perform(post(
                "/api/v1/challenges/" + UUID.randomUUID() + "/attach-summit/" + UUID.randomUUID()));

        //then:
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void detachSummit_shouldReturnChallengeWithoutRemovedSummit() throws Exception {
        //given:
        Summit summit = Instancio.create(Summit.class);
        Challenge challenge = Instancio.of(Challenge.class)
                .set(field(Challenge::getSummitsSet), Set.of(summit))
                .create();
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .setBlank(field(ChallengeDto::summitsSet))
                .create();
        Mockito.when(challengeService.detachSummitFromChallenge(summit.getId(), challenge.getId())).thenReturn(challenge);
        Mockito.when(challengeMapper.mapEntityToDto(challenge)).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(post(
                "/api/v1/challenges/" + challenge.getId() + "/detach-summit/" + summit.getId()));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.summitsSet").isEmpty());

        verify(challengeService).detachSummitFromChallenge(summit.getId(), challenge.getId());
    }

    @Test
    @WithMockUser(roles = USER)
    void detachSummit_shouldReturn403() throws Exception {
        //when:
        var response = mockMvc.perform(post(
                "/api/v1/challenges/" + UUID.randomUUID() + "/detach-summit/" + UUID.randomUUID()));

        //then:
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void updateChallenge_shouldReturnUpdatedChallenge() throws Exception {
        //given:
        Challenge oldChallenge = Instancio.create(Challenge.class);
        ChallengeRequestDto requestDto = new ChallengeRequestDto(
                "test name",
                "test description",
                Status.ACTIVE
        );
        Challenge requestedUpdate = new Challenge(requestDto.name(), requestDto.description(), requestDto.status());
        Challenge updatedChallenge = Instancio.of(Challenge.class)
                .set(field(Challenge::getId), oldChallenge.getId())
                .set(field(Challenge::getName), requestedUpdate.getName())
                .set(field(Challenge::getDescription), requestedUpdate.getDescription())
                .set(field(Challenge::getStatus), requestedUpdate.getStatus())
                .create();
        ChallengeDto challengeDto = Instancio.of(ChallengeDto.class)
                .set(field(ChallengeDto::id), updatedChallenge.getId())
                .set(field(ChallengeDto::name), updatedChallenge.getName())
                .set(field(ChallengeDto::description), updatedChallenge.getDescription())
                .set(field(ChallengeDto::status), updatedChallenge.getStatus())
                .create();
        Mockito.when(challengeMapper.mapRequestDtoToEntity(requestDto)).thenReturn(requestedUpdate);
        Mockito.when(challengeService.updateChallenge(oldChallenge.getId(), requestedUpdate)).thenReturn(updatedChallenge);
        Mockito.when(challengeMapper.mapEntityToDto(updatedChallenge)).thenReturn(challengeDto);

        //when:
        var response = mockMvc.perform(put("/api/v1/challenges/" + oldChallenge.getId())
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(oldChallenge.getId().toString()))
                .andExpect(jsonPath("$.name").value(requestDto.name()))
                .andExpect(jsonPath("$.description").value(requestDto.description()))
                .andExpect(jsonPath("$.status").value(requestDto.status().toString()));

        verify(challengeService).updateChallenge(oldChallenge.getId(), requestedUpdate);
    }


    @Test
    @WithMockUser(roles = USER)
    void updateChallenge_shouldReturn403() throws Exception {
        //given:
        UUID id = UUID.randomUUID();
        ChallengeRequestDto requestDto = new ChallengeRequestDto(
                "test name",
                "test description",
                Status.ACTIVE
        );

        //when:
        var response = mockMvc.perform(put("/api/v1/challenges/" + id)
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void updateChallenge_shouldReturn404() throws Exception {
        //given:
        UUID id = UUID.randomUUID();
        ChallengeRequestDto requestDto = new ChallengeRequestDto(
                "",
                "test description",
                Status.ACTIVE
        );

        //when:
        var response = mockMvc.perform(put("/api/v1/challenges/" + id)
                .contentType("application/json")
                .content(gson.toJson(requestDto)));

        //then:
        response.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void deleteChallenge_shouldDeleteChallenge() throws Exception {
        //given:
        UUID id = UUID.randomUUID();

        //when:
        var response = mockMvc.perform(delete("/api/v1/challenges/" + id));

        //then:
        response.andExpect(status().isOk());

        verify(challengeService).deleteChallenge(id);
    }

    @Test
    @WithMockUser(roles = USER)
    void deleteChallenge_shouldReturn403() throws Exception {
        //given:
        UUID id = UUID.randomUUID();

        //when:
        var response = mockMvc.perform(delete("/api/v1/challenges/" + id));

        //then:
        response.andExpect(status().isForbidden());
    }
}