package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.mappers.SummitMapper;
import com.codecool.kgp.repository.SummitRepository;
import com.codecool.kgp.service.SummitService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SummitController.class)
class SummitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SummitService summitService;

    public static Stream<Arguments> SummitsStream() {
        return Stream.of(
                Arguments.of(List.of(), 0),
                Arguments.of(List.of(
                        new SummitSimpleDto(
                                UUID.randomUUID(),
                                "rysy",
                                "karpaty",
                                "tatry",
                                1234,
                                "ACTIVE"
                        )
                ), 1)
        );
    }


    @ParameterizedTest
    @MethodSource("SummitsStream")
    void shouldReturnAllSummitsSimplified(List<SummitSimpleDto> input, int expected) throws Exception {
        //given:
//        List<SummitSimpleDto> summitSimpleDtoList = Instancio.ofList(SummitSimpleDto.class).size(1).create();
        Mockito.when(summitService.getAllSummitsSimplified())
                .thenReturn(input);

        //when:
        var response = mockMvc.perform(get("/got/v1/summits/"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expected));

        // no if!!!!
        if (expected > 0) {
            response.andExpect(jsonPath("$[0].id").value(input.get(0).id().toString()))
                    .andExpect(jsonPath("$[0].name").value(input.get(0).name()))
                    .andExpect(jsonPath("$[0].mountainRange").value(input.get(0).mountainRange()))
                    .andExpect(jsonPath("$[0].mountains").value(input.get(0).mountains()))
                    .andExpect(jsonPath("$[0].height").value(input.get(0).height()))
                    .andExpect(jsonPath("$[0].status").value(input.get(0).status()));
        }
    }


    @Test
    void shouldAddSummit() throws Exception {
        //given:
        SummitRequestDto requestDto = new SummitRequestDto(
                "test",
                1.23,
                4.56,
                "test range",
                "test mountains",
                1234,
                "test description",
                "test notes",
                11,
                "ACTIVE"
        );
        SummitDto dto = Instancio.create(SummitDto.class);
        Mockito.when(summitService.addNewSummit(requestDto))
                .thenReturn(dto);

        //when:
        var response = mockMvc.perform(post("/got/v1/summits/add-new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": "test",
                        "latitude" : 1.23,
                        "longitude": 4.56,
                        "mountainRange": "test range",
                        "mountains": "test mountains",
                        "height": 1234,
                        "description": "test description",
                        "guideNotes": "test notes",
                        "score": 11,
                        "status": "ACTIVE"
                        }
                        """));
        //then:
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.id().toString()));
    }
}