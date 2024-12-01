package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.entity.Challenge;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChallengeMapper {

    private final SummitMapper summitMapper;

    public ChallengeMapper(SummitMapper summitMapper) {
        this.summitMapper = summitMapper;
    }

    public ChallengeDto mapEntityToDto(Challenge challenge) {
        return mapEntityToDto(challenge, null);
    }

    public ChallengeDto mapEntityToDto(Challenge challenge, List<String> fields) {
        if (fields==null) fields = getAllFields();

        return new ChallengeDto(
                fields.contains("id") ? challenge.getId() : null,
                fields.contains("name") ? challenge.getName() : null,
                fields.contains("description") ? challenge.getDescription() : null,
                fields.contains("status") ? challenge.getStatus() : null,
                fields.contains("summitsSet") && !challenge.getSummitsSet().isEmpty() ?
                        challenge.getSummitsSet().stream().map(summitMapper::mapEntityToSimpleDto).collect(Collectors.toSet()) : null
        );
    }

    public Challenge mapRequestDtoToEntity(ChallengeRequestDto dto) {
        return new Challenge(
                dto.name(),
                dto.description(),
                dto.status()
        );
    }

    private List<String> getAllFields() {
        return List.of("id", "name", "description","status","summitsSet");
    }
}
