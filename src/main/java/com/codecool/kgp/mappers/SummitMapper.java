package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.repository.Summit;
import org.springframework.stereotype.Component;

@Component
public class SummitMapper {

    public SummitDto mapEntityToDto(Summit summit) {
        return new SummitDto(
                summit.getId(),
                summit.getChallengeList(),
                summit.getName(),
                summit.getCoordinatesArray(),
                summit.getMountainRange(),
                summit.getMountains(),
                summit.getHeight(),
                summit.getDescription(),
                summit.getGuideNotes(),
                summit.getScore(),
                summit.getStatus().name()
        );
    }
}
