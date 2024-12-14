package pl.manyroutes.controller;

import pl.manyroutes.config.SpringSecurityConfig;
import pl.manyroutes.controller.dto.ChallengeSimpleDto;
import pl.manyroutes.controller.dto.SummitDto;
import pl.manyroutes.controller.dto.SummitRequestDto;
import pl.manyroutes.entity.Summit;
import pl.manyroutes.entity.enums.Status;
import pl.manyroutes.entity.geography.Coordinates;
import pl.manyroutes.mappers.SummitMapper;
import pl.manyroutes.repository.SummitRepository;
import pl.manyroutes.repository.UserRepository;
import pl.manyroutes.service.CustomUserDetailsService;
import pl.manyroutes.service.SummitService;
import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.manyroutes.config.SpringSecurityConfig.ADMIN;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SpringSecurityConfig.class, CustomUserDetailsService.class})
@WebMvcTest(controllers = SummitController.class)
class SummitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SummitService summitService;

    @MockBean
    private SummitMapper summitMapper;

    @MockBean
    private SummitRepository summitRepository;

    @MockBean
    private UserRepository userRepository;


    private static Stream<Arguments> ProvideSummits() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(
                        new Summit(
                                "K2",
                                new Coordinates(35.8808, 76.5133),
                                "Himalayas",
                                "Karakorum",
                                8611,
                                "The second highest mountain on Earth",
                                "Climbing K2 is very challenging",
                                10,
                                Status.ACTIVE
                        ),
                        new Summit(
                                "Elbrus",
                                new Coordinates(43.3497, 42.4453),
                                "Caucasus",
                                "Caucasus Range",
                                5642,
                                "Highest mountain in Europe",
                                "Popular for mountaineering",
                                8,
                                Status.REMOVED
                        ),
                        new Summit(
                                "Mont Blanc",
                                new Coordinates(45.8326, 6.8652),
                                "Alps",
                                "Graian Alps",
                                4808,
                                "Highest mountain in the Alps",
                                "A popular climbing destination",
                                9,
                                Status.ACTIVE
                        )
                ))
        );
    }

    @ParameterizedTest
    @WithMockUser(roles = {ADMIN})
    @MethodSource("ProvideSummits")
    void getSummits_shouldReturnAllActiveSummits_whenNoParametersSend(List<Summit> summitsList) throws Exception {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = null;
        Mockito.when(summitService.getAllSummits(any(Status.class))).thenAnswer(invocationOnMock ->
                summitsList.stream().filter(s -> s.getStatus().equals(status)).toList()
        );
        Mockito.when(summitMapper.mapEntityToDto(any(Summit.class), eq(fields))).thenAnswer(invocationOnMock -> {
            Summit summit = invocationOnMock.getArgument(0);
            return new SummitDto(summit.getId(), summit.getChallengesSet().stream().map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).collect(Collectors.toSet()),
                    summit.getName(), summit.getCoordinatesArray(), summit.getMountainRange(), summit.getMountainChain(), summit.getHeight(),
                    summit.getDescription(), summit.getGuideNotes(), summit.getScore(), summit.getStatus().name());
        });
        int expectedListSize = summitsList.stream().filter(s -> s.getStatus().equals(status)).toList().size();

        //when:
        var response = mockMvc.perform(get("/api/v1/summits/"));

        //then:
        response.andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(expectedListSize));
        Mockito.verify(summitService).getAllSummits(status);
        Mockito.verify(summitMapper, Mockito.times(expectedListSize)).mapEntityToDto(any(Summit.class), eq(fields));
    }

    @ParameterizedTest
    @MethodSource("ProvideSummits")
    @WithMockUser(roles = ADMIN)
    void getSummits_shouldReturnAllSummitsWithChooseStatus_whenStatusSend(List<Summit> summitsList) throws Exception {
        //given:
        Status status = Status.REMOVED;
        List<String> fields = null;
        Mockito.when(summitService.getAllSummits(any(Status.class))).thenAnswer(invocationOnMock ->
                summitsList.stream().filter(s -> s.getStatus().equals(status)).toList()
        );
        Mockito.when(summitMapper.mapEntityToDto(any(Summit.class), eq(fields))).thenAnswer(invocationOnMock -> {
            Summit summit = invocationOnMock.getArgument(0);
            return new SummitDto(summit.getId(), summit.getChallengesSet().stream().map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).collect(Collectors.toSet()),
                    summit.getName(), summit.getCoordinatesArray(), summit.getMountainRange(), summit.getMountainChain(), summit.getHeight(),
                    summit.getDescription(), summit.getGuideNotes(), summit.getScore(), summit.getStatus().name());
        });
        int expectedListSize = summitsList.stream().filter(s -> s.getStatus().equals(status)).toList().size();

        //when:
        var response = mockMvc.perform(get("/api/v1/summits/" + "?status=" + status));

        //then:
        response.andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(expectedListSize));
        Mockito.verify(summitService).getAllSummits(status);
        Mockito.verify(summitMapper, Mockito.times(expectedListSize)).mapEntityToDto(any(Summit.class), eq(fields));
    }

    @ParameterizedTest
    @MethodSource("ProvideSummits")
    @WithMockUser(roles = ADMIN)
    void getSummits_shouldReturnAllSummitsWithChooseFields_whenFieldsSend(List<Summit> summitsList) throws Exception {
        //given:
        Status status = Status.ACTIVE;
        List<String> fields = List.of("id", "name");
        Mockito.when(summitService.getAllSummits(any(Status.class))).thenAnswer(invocationOnMock ->
                summitsList.stream().filter(s -> s.getStatus().equals(status)).toList()
        );
        Mockito.when(summitMapper.mapEntityToDto(any(Summit.class), eq(fields))).thenAnswer(invocationOnMock -> {
            Summit summit = invocationOnMock.getArgument(0);
            return new SummitDto(summit.getId(), null, summit.getName(), null, null, null, null, null, null, null, null);
        });
        int expectedListSize = summitsList.stream().filter(s -> s.getStatus().equals(status)).toList().size();

        //when:
        var response = mockMvc.perform(get("/api/v1/summits/" + "?fields=" + String.join(",", fields)));

        //then:
        response.andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(expectedListSize));
        if (expectedListSize > 0) {
            response.andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].name").exists())
                    .andExpect(jsonPath("$[0].challengeList").doesNotExist())
                    .andExpect(jsonPath("$[0].coordinates").doesNotExist())
                    .andExpect(jsonPath("$[0].mountainRange").doesNotExist())
                    .andExpect(jsonPath("$[0].mountainChain").doesNotExist())
                    .andExpect(jsonPath("$[0].height").doesNotExist())
                    .andExpect(jsonPath("$[0].description").doesNotExist())
                    .andExpect(jsonPath("$[0].guideNotes").doesNotExist())
                    .andExpect(jsonPath("$[0].score").doesNotExist())
                    .andExpect(jsonPath("$[0].status").doesNotExist());
        }
        Mockito.verify(summitService).getAllSummits(status);
        Mockito.verify(summitMapper, Mockito.times(expectedListSize)).mapEntityToDto(any(Summit.class), eq(fields));
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void getSummit_shouldReturnRequestSummit() throws Exception {
        //given:
        Summit summit = Instancio.of(Summit.class)
                .generate(field(Summit::getChallengesSet), gen -> gen.collection().size(5))
                .create();
        SummitDto summitDto = Instancio.of(SummitDto.class)
                .generate(field(SummitDto::challengesSet), gen -> gen.collection().size(5))
                .create();
        Mockito.when(summitService.getSummit(summit.getId())).thenReturn(summit);
        Mockito.when(summitMapper.mapEntityToDto(summit)).thenReturn(summitDto);

        //when:
        var response = mockMvc.perform(get("/api/v1/summits/" + summit.getId()));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(summitDto.id().toString()))
                .andExpect(jsonPath("$.name").value(summitDto.name()))
                .andExpect(jsonPath("$.description").value(summitDto.description()))
                .andExpect(jsonPath("$.height").value(summitDto.height()))
                .andExpect(jsonPath("$.coordinates[0]").value(summitDto.coordinates()[0]))
                .andExpect(jsonPath("$.coordinates[1]").value(summitDto.coordinates()[1]))
                .andExpect(jsonPath("$.challengesSet.size()").value(5))
                .andExpect(jsonPath("$.challengesSet[*].id")
                        .value(Matchers.hasItem(summitDto.challengesSet().iterator().next().id().toString())));
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void getSummit_shouldReturn404() throws Exception {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(summitService.getSummit(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when:
        var response = mockMvc.perform(get("/api/v1/summits/" + id));

        //then:
        response.andExpect(status().isNotFound());

    }

    @ParameterizedTest
    @CsvSource(value = {
            "K2, 35.8808, 76.5158, Karakorum, Himalayas, 8611, Second highest mountain in the world., Extreme technical climb recommended for experienced mountaineers., 9, ACTIVE",
            "Elbrus, 43.3499, 42.4374, Caucasus, Caucasus Mountains, 5642, Highest peak in Europe., Easier routes available for guided climbs., 7, DEVELOP"
    })
    @WithMockUser(roles = ADMIN)
    void addSummit_shouldAddSummit(String name, Double latitude, Double longitude, String mountainRange, String mountainChain,
                                   Integer height, String description, String guideNotes, Integer score, String status) throws Exception {
        //given:
        Summit summit = new Summit(name, new Coordinates(latitude, longitude), mountainRange, mountainChain,
                height, description, guideNotes, score, Status.valueOf(status));
        Mockito.when(summitMapper.mapRequestDtoToEntity(any(SummitRequestDto.class))).thenReturn(summit);
        Mockito.when(summitService.addNewSummit(summit))
                .thenReturn(summit);
        SummitDto responseDto = new SummitDto(summit.getId(), summit.getChallengesSet().stream().map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).collect(Collectors.toSet()),
                name, new Double[]{latitude, longitude}, mountainRange, mountainChain, height, description, guideNotes, score, status);
        Mockito.when(summitMapper.mapEntityToDto(summit)).thenReturn(responseDto);

        //when:
        var response = mockMvc.perform(post("/api/v1/summits/add-new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                                {
                                "name": "%s",
                                "latitude" : "%s",
                                "longitude": "%s",
                                "mountainRange": "%s",
                                "mountainChain": "%s",
                                "height": "%d",
                                "description": "%s",
                                "guideNotes": "%s",
                                "score": "%d",
                                "status": "%s"
                                }
                                """, name, latitude.toString().replace(",", "."), longitude.toString().replace(",", "."),
                        mountainRange, mountainChain, height, description, guideNotes, score, status)));
        //then:
//        response.andDo(MockMvcResultHandlers.print());
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(summit.getId().toString()))
                .andExpect(jsonPath("$.name").value(summit.getName()))
                .andExpect(jsonPath("$.coordinates[0]").value(summit.getCoordinates().getLatitude()))
                .andExpect(jsonPath("$.height").value(summit.getHeight()))
                .andExpect(jsonPath("$.status").value(summit.getStatus().toString()));
        Mockito.verify(summitMapper).mapRequestDtoToEntity(any(SummitRequestDto.class));
        Mockito.verify(summitService).addNewSummit(summit);
        Mockito.verify(summitMapper).mapEntityToDto(summit);
    }


    @ParameterizedTest
    @CsvSource(value = {
            "K2, 35.8808, 76.5158, Karakorum, Himalayas, 8611, Second highest mountain in the world., Extreme technical climb recommended for experienced mountaineers., 9, ACTIVE",
            "Elbrus, 43.3499, 42.4374, Caucasus, Caucasus Mountains, 5642, Highest peak in Europe., Easier routes available for guided climbs., 7, DEVELOP"
    })
    @WithMockUser(roles = ADMIN)
    void updateSummit_shouldUpdateSummit(String name, Double latitude, Double longitude, String mountainRange, String mountainChain,
                                         Integer height, String description, String guideNotes, Integer score, String status) throws Exception {
        //given:
        Summit summit = Instancio.of(Summit.class).create();

        SummitRequestDto summitRequestDto = Instancio.of(SummitRequestDto.class).create();
        Summit updatedSummit = Instancio.of(Summit.class).create();
        SummitDto summitDto = new SummitDto(summit.getId(), summit.getChallengesSet().stream().map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).collect(Collectors.toSet()),
                name, new Double[]{latitude, longitude}, mountainRange, mountainChain, height, description, guideNotes, score, status);

        Mockito.when(summitMapper.mapRequestDtoToEntity(any(SummitRequestDto.class))).thenReturn(summit);
        Mockito.when(summitService.updateSummit(summit.getId(), summit)).thenReturn(updatedSummit);
        Mockito.when(summitMapper.mapEntityToDto(updatedSummit)).thenReturn(summitDto);

        //when:
        ResultActions response = mockMvc.perform(put("/api/v1/summits/" + summit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                                {
                                "name": "%s",
                                "latitude" : "%s",
                                "longitude": "%s",
                                "mountainRange": "%s",
                                "mountainChain": "%s",
                                "height": "%d",
                                "description": "%s",
                                "guideNotes": "%s",
                                "score": "%d",
                                "status": "%s"
                                }
                                """, name, latitude.toString().replace(",", "."), longitude.toString().replace(",", "."),
                        mountainRange, mountainChain, height, description, guideNotes, score, status)));
        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(summit.getId().toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.coordinates[0]").value(latitude))
                .andExpect(jsonPath("$.height").value(height))
                .andExpect(jsonPath("$.status").value(status));
        Mockito.verify(summitMapper).mapRequestDtoToEntity(any(SummitRequestDto.class));
        Mockito.verify(summitService).updateSummit(summit.getId(), summit);
        Mockito.verify(summitMapper).mapEntityToDto(updatedSummit);
    }

    @Test
    @WithMockUser(roles = ADMIN)
    void deleteSummit_shouldDeleteSummit() throws Exception {
        //given:
        UUID id = UUID.randomUUID();
        //when:
        var response = mockMvc.perform(delete("/api/v1/summits/" + id));
        //then:
        response.andExpect(status().isOk());
        Mockito.verify(summitService).deleteSummit(id);
    }
}
