package pl.manyroutes.mappers;

import pl.manyroutes.controller.dto.ChallengeSimpleDto;
import pl.manyroutes.controller.dto.SummitDto;
import pl.manyroutes.controller.dto.SummitRequestDto;
import pl.manyroutes.controller.dto.SummitSimpleDto;
import pl.manyroutes.entity.Summit;
import pl.manyroutes.entity.enums.Status;
import pl.manyroutes.entity.geography.Coordinates;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class SummitMapper {

    public SummitDto mapEntityToDto(Summit summit) {
        return mapEntityToDto(summit, null);
    }

    public SummitDto mapEntityToDto(Summit summit, List<String> fields) {
        if (fields == null) fields = getAllFields();

        return new SummitDto(
                fields.contains("id") ? summit.getId() : null,
                fields.contains("challengesSet") ?
                        summit.getChallengesSet().stream().map(ch -> new ChallengeSimpleDto(
                                ch.getId(),
                                ch.getName(),
                                ch.getStatus()
                        )).collect(Collectors.toSet()) : null,
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
        fields = List.of("id", "challengesSet", "name", "coordinates", "mountainRange", "mountainChain", "height", "description", "guideNotes", "score", "status");
        return fields;
    }
}
