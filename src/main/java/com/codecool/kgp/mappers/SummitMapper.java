package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.ChallengeSimpleDto;
import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.entity.geography.Coordinates;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class SummitMapper {

    public SummitDto mapEntityToDto(Summit summit) {
        return new SummitDto(
                summit.getId(),
                summit.getChallengeList().stream().map(ch -> new ChallengeSimpleDto(
                        ch.getId(),
                        ch.getName(),
                        ch.getStatus()
                )).toList(),
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

    public SummitSimpleDto mapEntityToSimpleDto(Summit summit) {
        return new SummitSimpleDto(
                summit.getId(),
                summit.getName(),
                summit.getMountainRange(),
                summit.getMountains(),
                summit.getHeight(),
                summit.getStatus().name()
        );
    }
}
