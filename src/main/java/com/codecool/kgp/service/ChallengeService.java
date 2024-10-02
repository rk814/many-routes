package com.codecool.kgp.service;

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

    public ChallengeService(ChallengeRepository challengeRepository, SummitRepository summitRepository) {
        this.challengeRepository = challengeRepository;
        this.summitRepository = summitRepository;
    }

    public List<Challenge> getAllChallenges(Status status) {
        List<Challenge> challenges = challengeRepository.findAllByStatusWithSummits(status);
        log.info("{} challenges were found", challenges.size());
        return challenges;
    }

    public List<Challenge> getAllChallengesWithoutSummitLists(Status status) {
        List<Challenge> challenges = challengeRepository.findAllByStatus(status);
        log.info("{} challenges were found", challenges.size());
        return challenges;
    }

    public Challenge getChallenge(UUID id) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> {
            log.warn("Challenge with id '{}' was not found", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
        });
        log.info("Challenge with id '{}' was found", id);
        return challenge;
    }

    public Challenge addNewChallenge(Challenge challenge) {
        try {
            Challenge savedChallenge = challengeRepository.save(challenge);
            log.info("New challenge with id '{}' was saved", challenge.getId());
            return savedChallenge;
        } catch (DataIntegrityViolationException e) {
            log.warn("Challenge with name '{}' already exists", challenge.getName());
            throw new DuplicateEntryException("Challenge name must be unique");
        }
    }

    public Challenge attachSummitToChallenge(UUID summitId, UUID id) {
        Challenge challenge = findChallenge(id);
        Summit summit = findSummit(summitId);

        challenge.addSummit(summit);
        summit.addChallenge(challenge);
        log.info("Summit with id '{}' was successfully add to challenge with id '{}'", summitId, challenge);
        return challenge;
    }

    public Challenge detachSummitFromChallenge(UUID summitId, UUID id) {
        Challenge challenge = findChallenge(id);
        Summit summit = findSummit(summitId);

        challenge.removeSummit(summit);
        summit.removeChallenge(challenge);
        challengeRepository.save(challenge);
        log.info("Summit with id '{}' was successfully removed from challenge with id '{}'", summitId, challenge);
        return challenge;
    }

    public Challenge updateChallenge(UUID id, Challenge challenge) {
        Challenge challengeFromDB = challengeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Challenge with id '{}' was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
                });
        challengeFromDB.updateChallenge(challenge);
        challengeRepository.save(challengeFromDB);
        return challengeFromDB;
    }

    public void deleteChallenge(UUID id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Challenge with id '{}' was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
                });
        challengeRepository.delete(challenge);
        log.info("Challenge with id '{}' was successfully deleted", id);
    }

    private Challenge findChallenge(UUID id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Challenge with id '{}' was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
                });
    }

    private Summit findSummit(UUID id) {
        return summitRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Summit with id '{}' was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Summit was not found");
                });
    }


}
