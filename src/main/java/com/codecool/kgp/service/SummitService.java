package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.mappers.SummitMapper;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.SummitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
public class SummitService {

    private final SummitRepository summitRepository;
    private final SummitMapper summitMapper;

    public SummitService(SummitRepository summitRepository, SummitMapper summitMapper) {
        this.summitRepository = summitRepository;
        this.summitMapper = summitMapper;
    }


    public List<SummitDto> getAllSummits() {
        List<Summit> summits = summitRepository.findAll();
        log.info("{} summits were found", summits.size());
        return summits.stream().map(summitMapper::mapEntityToDto).toList();
    }

    public List<SummitSimpleDto> getAllSummitsSimplified() {
        List<Summit> summits = summitRepository.findAll();
        log.info("{} summits were found", summits.size());
        return summits.stream().map(summitMapper::mapEntityToSimpleDto).toList();
    }

    public SummitDto addNewSummit(SummitRequestDto dto) {
        Summit summit = summitMapper.mapRequestDtoToEntity(dto);
        Summit summitFromDb = summitRepository.save(summit);
        log.info("New summit with id '{}' was saved", summit.getId());
        return summitMapper.mapEntityToDto(summitFromDb);
    }
}
