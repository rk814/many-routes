package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.controller.dto.ChallengeSimpleDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.repository.ChallengeRepository;
import com.codecool.kgp.repository.SummitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SummitRepository summitRepository;
    private final ChallengeMapper challengeMapper;

    public ChallengeService(ChallengeRepository challengeRepository, SummitRepository summitRepository, ChallengeMapper challengeMapper) {
        this.challengeRepository = challengeRepository;
        this.summitRepository = summitRepository;
        this.challengeMapper = challengeMapper;
    }

    public List<ChallengeDto> getAllChallenges(Status status, List<String> fields) {
        List<Challenge> challenges = challengeRepository.findAllByStatus(status);
        log.info("{} challenges were found", challenges.size());
        return challenges.stream().map(challenge ->
                (fields != null) ? challengeMapper.mapEntityToDto(challenge, fields) : challengeMapper.mapEntityToDto(challenge)
        ).toList();
    }

    public List<ChallengeSimpleDto> getAllChallengesSimplified() {
        List<Challenge> challenges = challengeRepository.findAll();
        log.info("{} challenges were found", challenges.size());
        return challenges.stream().map(ch -> new ChallengeSimpleDto(ch.getId(), ch.getName(), ch.getStatus())).toList();
    }

    public ChallengeDto getChallenge(UUID id) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> {
            log.warn("Challenge with id '{}' was not found", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
        });
        return challengeMapper.mapEntityToDto(challenge, null); // TODO
    }

    public ChallengeDto addNewChallenge(ChallengeRequestDto dto) {
        Challenge challenge = challengeMapper.mapRequestDtoToEntity(dto);

        try {
            Challenge savedChallenge = challengeRepository.save(challenge);
            log.info("New challenge with id '{}' was saved", challenge.getId());
            return challengeMapper.mapEntityToDto(savedChallenge, null); //TODO
        } catch (DataIntegrityViolationException e) {
            log.warn("Challenge with name '{}' already exists", challenge.getName());
            throw new DuplicateEntryException("Challenge name must be unique");
        }
    }

    public ChallengeDto attachSummitToChallenge(UUID summitId, UUID id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Challenge with id '{}' was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
                });
        Summit summit = summitRepository.findById(summitId)
                .orElseThrow(() -> {
                    log.warn("Summit with id '{}' was not found", summitId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Summit was not found");
                });
        challenge.addSummit(summit);
        summit.addChallenge(challenge);

        return challengeMapper.mapEntityToDto(challenge, null); //TODO
    }
}
