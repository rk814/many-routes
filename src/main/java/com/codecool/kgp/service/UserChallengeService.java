package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.UserChallengeDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.UserChallenge;
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
    private final UserSummitService userSummitService;
    private final ChallengeMapper challengeMapper;

    public UserChallengeService(UserRepository userRepository, ChallengeRepository challengeRepository,
                                UserChallengeRepository userChallengeRepository, UserSummitService userSummitService,
                                UserChallengeMapper userChallengeMapper, ChallengeMapper challengeMapper) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.userSummitService = userSummitService;
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
                                .anyMatch(uCh -> ch.getId() == uCh.getChallenge().getId()))
                .map(challengeMapper::mapEntityToDto).toList();
        log.info("Found {} available user challenges with login '{}", challengesDto.size(), login);
        return challengesDto;
    }

    public UserChallengeDto saveUserChallenge(String login, UUID challengeId) {
        User user = getUserByLogin(login);

        Challenge challenge = getChallengeById(challengeId);

        UserChallenge userChallenge = new UserChallenge(user, challenge);

        UserChallenge userChallengeFromDb = userChallengeRepository.save(userChallenge);

        challenge.getSummitList().forEach(summit -> {
            userSummitService.saveUserSummit(userChallenge, summit);
        });

        user.assignUserChallenge(userChallengeFromDb);
        userRepository.save(user);

        log.info("User challenge with id '{}' was save for user with login '{}' as user challenge with id '{}'",
                challengeId, login, userChallenge.getId());

        return userChallengeMapper.mapEntityToDto(userChallengeFromDb);
    }

    public void setUserChallengeFinishedTime(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);
        if (userChallenge.getFinishedAt() == null) {
            userChallenge.setFinishedAt(LocalDateTime.now());
            userChallengeRepository.save(userChallenge);
            log.info("User challenge with id '{}' was completed", id);
        } else {
            log.warn("User challenge with id '{}' was already completed", id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "To wyzwanie zostało już wcześniej zakończone");
        }
    }

    public void setUserChallengeScore(UUID id, Integer score) {
        UserChallenge userChallenge = getUserChallengeById(id);

        userChallenge.setScore(score);
        userChallengeRepository.save(userChallenge);
        log.info("Score in user challenge with id '{}' was update to new value of {} points", id, score);
    }

    public void increaseUserChallengeScore(UUID id, Integer score) {
        UserChallenge userChallenge = getUserChallengeById(id);
        int increasedScore = userChallenge.getScore() + score;

        userChallenge.setScore(increasedScore);
        userChallengeRepository.save(userChallenge);
        log.info("Score in user challenge with id '{}' was update to new value of {} points", id, increasedScore);
    }

    public void deleteUserChallenge(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);

        userChallengeRepository.delete(userChallenge);
        log.info("User challenge with id '{}' was successfully deleted", id);
        // TODO soft delete
    }

    public void completeUserChallengeIfValid(UUID id) {
        if (checkIfAllUserSummitsAreConquered(id)) {
            setUserChallengeFinishedTime(id);
//            log.info("User challenge with id '{}' was completed", id);
        } else {
            log.info("Refused to complete user challenge with id '{}'", id);
        }
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
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login jest  niewłaściwy");
                });
    }

    private boolean checkIfAllUserSummitsAreConquered(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);
        boolean areAllConquered = userChallenge.getUserSummitList().stream().allMatch(s -> s.getConqueredAt() != null);
        log.info(areAllConquered ? "All summits from user challenge with id '{}' are conquered" :
                "Not all summits from user challenge with id '{}' are conquered", id);
        return areAllConquered;
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

}
