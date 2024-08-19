package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.mappers.SummitMapper;
import com.codecool.kgp.repository.SummitRepository;
import org.instancio.Instancio;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;


class SummitServiceTest {

    private final SummitRepository summitRepository = Mockito.mock();
    private final SummitMapper summitMapper = Mockito.mock();
    private final SummitService summitService = new SummitService(summitRepository, summitMapper);


    @Test
    void getAllSummitsSimplified_shouldReturnAllSummits() {
        //given:
        List<Summit> summitList = Instancio.ofList(Summit.class).size(4).create();

        Mockito.when(summitRepository.findAll()).thenReturn(summitList);

        SummitSimpleDto summitSimpleDto = Instancio.create(SummitSimpleDto.class);
        Mockito.when(summitMapper.mapEntityToSimpleDto(any(Summit.class)))
                .thenReturn(summitSimpleDto);

        //when:
        List<SummitSimpleDto> actual = summitService.getAllSummitsSimplified();

        //then:
        Assertions.assertThat(actual).hasSize(4);
    }

    @Test
    void getAllSummitsSimplified_shouldReturnEmptyList() {
        //given:
        List<Summit> summitList = Instancio.ofList(Summit.class).size(0).create();

        Mockito.when(summitRepository.findAll()).thenReturn(summitList);

        //when:
        List<SummitSimpleDto> actual = summitService.getAllSummitsSimplified();

        //then:
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void addNewSummit_shouldReturnNewSummitDto() {
        //given:
        SummitRequestDto requestDto = Instancio.create(SummitRequestDto.class);
        Summit summit = Instancio.create(Summit.class);
        SummitDto dto = Instancio.create(SummitDto.class);

        Mockito.when(summitMapper.mapRequestDtoToEntity(requestDto)).thenReturn(summit);
        Mockito.when(summitRepository.save(summit)).thenReturn(summit);
        Mockito.when(summitMapper.mapEntityToDto(summit)).thenReturn(dto);

        //when:
        SummitDto actual = summitService.addNewSummit(requestDto);

        //then:
        Assertions.assertThat(actual).isEqualTo(dto);

    }
}