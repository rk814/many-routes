package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.entity.geography.Coordinates;
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

    public Summit mapRequestDtoToEntity(SummitRequestDto dto) {
        return new Summit(
                dto.name(),
                new Coordinates(dto.latitude(), dto.longitude()),
                dto.mountainRange(),
                dto.mountains(),
                dto.height(),
                dto.description(),
                dto.guideNotes(),
                dto.score(),
                Status.valueOf(dto.status())
        );
    }
}
