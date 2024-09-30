package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.entity.Challenge;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChallengeMapper {

    private final SummitMapper summitMapper;

    public ChallengeMapper(SummitMapper summitMapper) {
        this.summitMapper = summitMapper;
    }

    public ChallengeDto mapEntityToDto(Challenge challenge) {
        return new ChallengeDto(
                challenge.getId(),
                challenge.getName(),
                challenge.getDescription(),
                challenge.getStatus(),
                challenge.getSummitList().stream().map(summitMapper::mapEntityToSimpleDto).toList()
        );
    }

    public ChallengeDto mapEntityToDto(Challenge challenge, List<String> fields) {
        return new ChallengeDto(
                fields.contains("id") ? challenge.getId() : null,
                fields.contains("name") ? challenge.getName() : null,
                fields.contains("description") ? challenge.getDescription() : null,
                fields.contains("status") ? challenge.getStatus() : null,
                fields.contains("summits") ? challenge.getSummitList().stream().map(summitMapper::mapEntityToSimpleDto).toList() : null
        );
    }

    public Challenge mapRequestDtoToEntity(ChallengeRequestDto dto) {
        return new Challenge(
                dto.name(),
                dto.description(),
                dto.status()
        );
    }
}
