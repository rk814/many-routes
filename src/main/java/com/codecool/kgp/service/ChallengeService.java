package com.codecool.kgp.service;

import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.repository.ChallengeRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SummitService summitService;
    private final UserChallengeService userChallengeService;

    public ChallengeService(ChallengeRepository challengeRepository, SummitService summitService, UserChallengeService userChallengeService) {
        this.challengeRepository = challengeRepository;
        this.summitService = summitService;
        this.userChallengeService = userChallengeService;
    }

    public List<Challenge> getAllChallenges(Status status, List<String> fields) {
        boolean includeSummitList = fields.contains("summitsSet");
        List<Challenge> challenges;
        if (includeSummitList) {
            challenges = challengeRepository.findAllByStatusWithSummits(status);
        } else {
            challenges = challengeRepository.findAllByStatus(status);
            Hibernate.initialize(challenges);
        }
        log.info("{} challenges were found", challenges.size());
        return challenges;
    }

    public List<Challenge> getUnstartedChallenges(UUID userId, Status status, List<String> fields) {
        List<Challenge> allChallenges = getAllChallenges(status, fields);
        List<UserChallenge> userChallenges = userChallengeService.getUserChallenges(userId);
        List<Challenge> availableChallenges = allChallenges.stream()
                .filter(challenge ->
                        userChallenges.stream()
                                .noneMatch(userChallenge -> challenge.getId().equals(userChallenge.getChallenge().getId())))
                .toList();
        log.info("Found {} available (unstarted) user challenges for user with id '{}'", availableChallenges.size(), userId);
        return availableChallenges;
    }

    public Challenge getChallenge(UUID challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> {
            log.warn("Challenge with id '{}' was not found", challengeId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
        });
        log.info("Challenge with id '{}' was found", challengeId);
        return challenge;
    }

    public Challenge addNewChallenge(Challenge challenge) {
        try {
            Challenge savedChallenge = challengeRepository.save(challenge);
            log.info("New challenge with id '{}' was saved", challenge.getId());
            return savedChallenge;
        } catch (DataIntegrityViolationException e) {
            log.warn("Challenge with challengeName '{}' already exists", challenge.getName());
            throw new DuplicateEntryException("Challenge challengeName must be unique");
        }
    }

    public Challenge attachSummitToChallenge(UUID summitId, UUID challengeId) {
        Challenge challenge = findChallenge(challengeId);
        Summit summit = summitService.getSummit(summitId);

        challenge.addSummit(summit);
        summit.addChallenge(challenge);
        log.info("Summit with id '{}' was successfully add to challenge with id '{}'", summitId, challenge);
        return challenge;
    }

    public Challenge detachSummitFromChallenge(UUID summitId, UUID challengeId) {
        Challenge challenge = findChallenge(challengeId);
        Summit summit = summitService.getSummit(summitId);

        challenge.removeSummit(summit);
        summit.removeChallenge(challenge);
        challengeRepository.save(challenge);
        log.info("Summit with id '{}' was successfully removed from challenge with id '{}'", summitId, challenge);
        return challenge;
    }

    public Challenge updateChallenge(UUID challengeId, Challenge challenge) {
        Challenge challengeFromDB = findChallenge(challengeId);
        challengeFromDB.updateChallenge(challenge);
        challengeRepository.save(challengeFromDB);
        return challengeFromDB;
    }

    public void deleteChallenge(UUID challengeId) {
        Challenge challenge = findChallenge(challengeId);
        challengeRepository.delete(challenge);
        log.info("Challenge with id '{}' was successfully deleted", challengeId);
    }

    protected Challenge findChallenge(UUID challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> {
                    log.warn("Challenge with id '{}' was not found", challengeId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge was not found");
                });
    }
}
