package com.codecool.kgp.service;

import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class UserChallengeService {

    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeService challengeService;
    private final UserService userService;


    public UserChallengeService(UserChallengeRepository userChallengeRepository, @Lazy ChallengeService challengeService, UserService userService) {
        this.challengeService = challengeService;
        this.userService = userService;
        this.userChallengeRepository = userChallengeRepository;
    }


    public List<UserChallenge> getUserChallenges(UUID userId) {
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByUserIdWithAllRelationships(userId);
        log.info("Found {} user challenges with id '{}'", userChallenges.size(), userId);
        return userChallenges;
    }

    public UserChallenge getUserChallenge(UUID userChallengeId) {
        UserChallenge userChallenge = findUserChallengeById(userChallengeId);
        log.info("User challenge with id '{}' was found", userChallengeId);
        return userChallenge;
    }

    public List<UserChallenge> getCompletedUserChallenges(UUID userId) {
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByUserIdWithAllRelationships(userId);
        List<UserChallenge> completedUserChallenges = userChallenges.stream()
                .filter(ch -> ch.getFinishedAt() != null).toList();
        log.info("Found {} completed user challenges with id '{}'", completedUserChallenges.size(), userId);
        return completedUserChallenges;
    }

    public List<UserChallenge> getUncompletedUserChallenges(UUID userId) {
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByUserIdWithAllRelationships(userId);
        List<UserChallenge> uncompletedUserChallenges = userChallenges.stream()
                .filter(ch -> ch.getFinishedAt() == null).toList();
        log.info("Found {} active user challenges with id '{}'", uncompletedUserChallenges.size(), userId);
        return uncompletedUserChallenges;
    }

    public UserChallenge saveUserChallenge(UUID userId, UUID challengeId) {
        User user = userService.findUser(userId);
        if (isTheChallengeActive(challengeId, user.getUserChallengesSet())) {
            log.warn("User with id '{}' attempted to start the challenge with id '{}' already active", userId, challengeId);
            throw new DuplicateEntryException("To wyzwanie jest już rozpoczęte");
        }

        Challenge challenge = challengeService.findChallenge(challengeId);
        UserChallenge userChallenge = new UserChallenge(user, challenge);
        challenge.getSummitsSet().forEach(summit -> {
            UserSummit userSummit = new UserSummit(userChallenge, summit);
            userChallenge.assignUserSummit(userSummit);
        });

        UserChallenge userChallengeFromDb = userChallengeRepository.save(userChallenge);

        user.assignUserChallenge(userChallengeFromDb);
        userService.saveUser(user);

        log.info("User challenge with id '{}' was save for user with id '{}' as user challenge with id '{}'",
                challengeId, userId, userChallenge.getId());

        return userChallengeFromDb;
    }

    public UserChallenge setSummitConquered(UUID userChallengeId, UUID userSummitId, int score) {
        UserChallenge userChallenge = findUserChallengeById(userChallengeId);
        UserSummit userSummit = userChallenge.getUserSummitsSet().stream().filter(us -> us.getId().equals(userSummitId)).findFirst()
                .orElseThrow(() -> {
                    log.warn("User Challenge with id '{}' does not contain User Summit with id '{}'", userChallengeId, userSummitId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Szczyt o numerze id '%s' nie istnieje", userSummitId)
                    );
                });

        userSummit.setConqueredAt(LocalDateTime.now());
        userSummit.setScore(score);
        log.info("User summit with id '{}' was conquered with score value of {}", userSummitId, score);

        increaseUserChallengeScore(userChallenge, score);

        if (areAllUserSummitsConquered(userChallenge)) {
            setUserChallengeFinishedTime(userChallenge);
            log.info("User challenge with id '{}' was completed", userChallenge.getId());
        } else {
            log.info("User challenge with id '{}' is not completed jet", userChallenge.getId());
        }

        return userChallengeRepository.save(userChallenge);
    }

    public void setUserChallengeScore(UUID userChallengeId, Integer score) {
        UserChallenge userChallenge = findUserChallengeById(userChallengeId);
        userChallenge.setScore(score);
        userChallengeRepository.save(userChallenge);
        log.info("Score of user challenge with id '{}' was update to new value of {} points", userChallengeId, score);
    }


    public void deleteUserChallenge(UUID userChallengeId) {
        UserChallenge userChallenge = findUserChallengeById(userChallengeId);
        userChallengeRepository.delete(userChallenge);
        log.info("User challenge with id '{}' was successfully deleted", userChallengeId);
    }

    protected UserChallenge findUserChallengeById(UUID userChallengeId) {
        return userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> {
                    log.warn("User challenge with id '{}' was not found", userChallengeId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Wyzwanie użytkownika o numerze id '%s' nie istnieje", userChallengeId));
                });
    }

    private boolean isTheChallengeActive(UUID challengeId, Set<UserChallenge> userChallenges) {
        return userChallenges.stream().anyMatch(ch -> ch.getChallenge().getId() == challengeId && ch.getFinishedAt() == null);
    }

    private void increaseUserChallengeScore(UserChallenge userChallenge, Integer score) {
        int increasedScore = userChallenge.getScore() + score;
        userChallenge.setScore(increasedScore);
        log.info("Score in user challenge with id '{}' was updated to new value of {} points", userChallenge.getId(), increasedScore);
    }

    private boolean areAllUserSummitsConquered(UserChallenge userChallenge) {
        return userChallenge.getUserSummitsSet().stream().allMatch(s -> s.getConqueredAt() != null);
    }

    private void setUserChallengeFinishedTime(UserChallenge userChallenge) {
        try {
            userChallenge.setFinishedAt(LocalDateTime.now());
        } catch (IllegalStateException e) {
            log.warn("User challenge with id '{}' was already completed", userChallenge.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "To wyzwanie zostało już wcześniej zakończone");
        }
    }
}
