package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.ChallengeSimpleDto;
import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.entity.geography.Coordinates;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class SummitMapper {

    public SummitDto mapEntityToDto(Summit summit) {
        return mapEntityToDto(summit, null);
    }

    public SummitDto mapEntityToDto(Summit summit, List<String> fields) {
        if (fields == null) fields = getAllFields();

        return new SummitDto(
                fields.contains("id") ? summit.getId() : null,
                fields.contains("challengeList") ?
                        summit.getChallengeList().stream().map(ch -> new ChallengeSimpleDto(
                                ch.getId(),
                                ch.getName(),
                                ch.getStatus()
                        )).toList() : null,
                fields.contains("name") ? summit.getName() : null,
                fields.contains("coordinates") ? summit.getCoordinatesArray() : null,
                fields.contains("mountainRange") ? summit.getMountainRange() : null,
                fields.contains("mountainChain") ? summit.getMountainChain() : null,
                fields.contains("height") ? summit.getHeight() : null,
                fields.contains("description") ? summit.getDescription() : null,
                fields.contains("guideNotes") ? summit.getGuideNotes() : null,
                fields.contains("score") ? summit.getScore() : null,
                fields.contains("status") ? summit.getStatus().name() : null
        );
    }

    public SummitSimpleDto mapEntityToSimpleDto(Summit summit) {
        return new SummitSimpleDto(
                summit.getId(),
                summit.getName(),
                summit.getMountainRange(),
                summit.getMountainChain(),
                summit.getHeight(),
                summit.getStatus().name()
        );
    }

    public Summit mapRequestDtoToEntity(SummitRequestDto dto) {
        return new Summit(
                dto.name(),
                new Coordinates(dto.latitude(), dto.longitude()),
                dto.mountainRange(),
                dto.mountainChain(),
                dto.height(),
                dto.description(),
                dto.guideNotes(),
                dto.score(),
                Status.valueOf(dto.status())
        );
    }

    private List<String> getAllFields() {
        List<String> fields;
        fields = List.of("id", "challengeList", "name", "coordinates", "mountainRange", "mountainChain", "height", "description", "guideNotes", "score", "status");
        return fields;
    }
}
