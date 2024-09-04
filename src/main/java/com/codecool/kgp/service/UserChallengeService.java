package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.UserChallengeDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.mappers.UserChallengeMapper;
import com.codecool.kgp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class UserChallengeService {

    private final UserChallengeRepository userChallengeRepository;
    private final UserChallengeMapper userChallengeMapper;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeMapper challengeMapper;

    public UserChallengeService(UserRepository userRepository, ChallengeRepository challengeRepository,
                                UserChallengeRepository userChallengeRepository,
                                UserChallengeMapper userChallengeMapper, ChallengeMapper challengeMapper) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.userChallengeMapper = userChallengeMapper;
        this.challengeMapper = challengeMapper;
    }

    public List<UserChallengeDto> getUserChallenges(String login) {
        List<UserChallenge> userChallenges = getAllUserChallenges(login);
        log.info("Found {} user challenges with login '{}'", userChallenges.size(), login);
        return userChallenges.stream().map(userChallengeMapper::mapEntityToDto).toList();
    }

    public UserChallengeDto getUserChallenge(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);
        log.info("User challenge with id '{}' was found", id);
        return userChallengeMapper.mapEntityToDto(userChallenge);
    }

    public List<UserChallengeDto> getCompletedUserChallenges(String login) {
        List<UserChallenge> userChallenges = getAllUserChallenges(login);
        List<UserChallengeDto> userChallengesDto = userChallenges.stream()
                .filter(ch -> ch.getFinishedAt() != null)
                .map(userChallengeMapper::mapEntityToDto).toList();
        log.info("Found {} completed user challenges with login '{}'", userChallengesDto.size(), login);
        return userChallengesDto;
    }

    public List<UserChallengeDto> getActiveUserChallenges(String login) {
        List<UserChallenge> userChallenges = getAllUserChallenges(login);
        List<UserChallengeDto> userChallengesDto = userChallenges.stream()
                .filter(ch -> ch.getFinishedAt() == null)
                .map(userChallengeMapper::mapEntityToDto).toList();
        log.info("Found {} active user challenges with login '{}'", userChallengesDto.size(), login);
        return userChallengesDto;
    }

    // TODO move to ChallengeService
    public List<ChallengeDto> getAvailableChallenges(String login) {
        List<Challenge> challenges = challengeRepository.findAll();
        List<UserChallenge> userChallenges = getAllUserChallenges(login);
        List<ChallengeDto> challengesDto = challenges.stream()
                .filter(ch ->
                        userChallenges.stream()
                                .noneMatch(uCh -> ch.getId() == uCh.getChallenge().getId()))
                .map(challengeMapper::mapEntityToDto).toList();
        log.info("Found {} available user challenges with login '{}'", challengesDto.size(), login);
        return challengesDto;
    }

    public UserChallengeDto saveUserChallenge(String login, UUID challengeId) {
        User user = getUserByLogin(login);
        if (hasThatChallengeActive(challengeId, user.getUserChallenges())) {
            log.warn("User with login '{}' attempted to start the challenge with id '{}' already active", login, challengeId);
            throw new DuplicateEntryException("Użytkownik już uczestniczy w tym wyzwaniu");
        }

        Challenge challenge = getChallengeById(challengeId);
        UserChallenge userChallenge = new UserChallenge(user, challenge);
        challenge.getSummitList().forEach(summit -> {
            UserSummit userSummit = new UserSummit(userChallenge, summit);
            userChallenge.assignUserSummit(userSummit);
        });

        UserChallenge userChallengeFromDb = userChallengeRepository.save(userChallenge);

        user.assignUserChallenge(userChallengeFromDb);
        userRepository.save(user);

        log.info("User challenge with id '{}' was save for user with login '{}' as user challenge with id '{}'",
                challengeId, login, userChallenge.getId());

        return userChallengeMapper.mapEntityToDto(userChallengeFromDb);
    }

    public UserChallengeDto setSummitConquered(UUID userChallengeId, UUID userSummitId, int score) {
        UserChallenge userChallenge = getUserChallengeById(userChallengeId);
        UserSummit userSummit = userChallenge.getUserSummitList().stream().filter(us -> us.getId().equals(userSummitId)).findFirst()
                .orElseThrow(() -> {
                    log.warn("User Challenge with id '{}' does not contain User Summit with id '{}'", userChallengeId, userSummitId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Szczyt o numerze id '%s' nie został znaleziony", userSummitId)
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

        UserChallenge userChallengeFromDb = userChallengeRepository.save(userChallenge);
        return userChallengeMapper.mapEntityToDto(userChallengeFromDb);
    }

    public void setUserChallengeScore(UUID id, Integer score) {
        UserChallenge userChallenge = getUserChallengeById(id);

        userChallenge.setScore(score);
        userChallengeRepository.save(userChallenge);
        log.info("Score of user challenge with id '{}' was update to new value of {} points", id, score);
    }


    public void deleteUserChallenge(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);

        userChallengeRepository.delete(userChallenge);
        log.info("User challenge with id '{}' was successfully deleted", id);
        // TODO soft delete
    }

    protected UserChallenge getUserChallengeById(UUID id) {
        return userChallengeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User challenge with id '{}' was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Wyzwanie użytkownika o numerze id '%s' nie istnieje", id));
                });
    }

    private User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.warn("Login '{}' is invalid", login);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Login jest niewłaściwy");
                });
    }

    private List<UserChallenge> getAllUserChallenges(String login) {
        User user = getUserByLogin(login);
        return userChallengeRepository.findAllByUserId(user.getId());
    }

    private Challenge getChallengeById(UUID challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> {
                    log.warn("Challenge with {} was not found", challengeId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Wyzwanie o numerze id '%s' nie zostało znalezione", challengeId));
                });
    }

    private boolean hasThatChallengeActive(UUID challengeId, List<UserChallenge> userChallenges) {
        return userChallenges.stream().anyMatch(ch -> ch.getChallenge().getId() == challengeId && ch.getFinishedAt() == null);
    }

    private void increaseUserChallengeScore(UserChallenge userChallenge, Integer score) {
        int increasedScore = userChallenge.getScore() + score;
        userChallenge.setScore(increasedScore);
        log.info("Score in user challenge with id '{}' was updated to new value of {} points", userChallenge.getId(), increasedScore);
    }

    private boolean areAllUserSummitsConquered(UserChallenge userChallenge) {
        return userChallenge.getUserSummitList().stream().allMatch(s -> s.getConqueredAt() != null);
    }

    private void setUserChallengeFinishedTime(UserChallenge userChallenge) {
        if (userChallenge.getFinishedAt() == null) {
            userChallenge.setFinishedAt(LocalDateTime.now());
        } else {
            log.warn("User challenge with id '{}' was already completed", userChallenge.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "To wyzwanie zostało już wcześniej zakończone");
        }
    }
}
