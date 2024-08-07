package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.UserChallengeDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.mappers.UserChallengeMapper;
import com.codecool.kgp.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserChallengeService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserSummitService userSummitService;
    private final UserChallengeMapper userChallengeMapper;
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
        return userChallenges.stream().map(userChallengeMapper::mapEntityToDto).toList();
    }

    public UserChallengeDto getUserChallenge(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);
        return userChallengeMapper.mapEntityToDto(userChallenge);
    }

    public List<UserChallengeDto> getCompletedUserChallenges(String login) {
        List<UserChallenge> userChallenges = getAllUserChallenges(login);
        return userChallenges.stream()
                .filter(ch -> ch.getFinishedAt() != null)
                .map(userChallengeMapper::mapEntityToDto).toList();
    }

    public List<UserChallengeDto> getActiveUserChallenges(String login) {
        List<UserChallenge> userChallenges = getAllUserChallenges(login);
        return userChallenges.stream()
                .filter(ch -> ch.getFinishedAt() == null)
                .map(userChallengeMapper::mapEntityToDto).toList();
    }

    // TODO move to ChallengeService
    public List<ChallengeDto> getAvailableChallenges(String login) {
        List<Challenge> challenges = challengeRepository.findAll();
        List<UserChallenge> userChallenges = getAllUserChallenges(login);
        return challenges.stream()
                .filter(ch ->
                        userChallenges.stream()
                                .anyMatch(uCh -> ch.getId() == uCh.getChallenge().getId()))
                .map(challengeMapper::mapEntityToDto).toList();
    }

    public UserChallengeDto saveUserChallenge(String login, UUID challengeId) {
        User user = getUserByLogin(login);
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Wyzwanie o numerze id '%s' nie istnieje", challengeId)));

        UserChallenge userChallenge = new UserChallenge(user, challenge);
        UserChallenge userChallengeFromDb = userChallengeRepository.save(userChallenge);

        challenge.getSummitList().forEach(summit -> {
            userSummitService.saveUserSummit(userChallenge, summit);
        });

        user.assignUserChallenge(userChallengeFromDb);
        userRepository.save(user);

        return userChallengeMapper.mapEntityToDto(userChallengeFromDb);
    }

    public void setUserChallengeFinishedTime(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);
        if (userChallenge.getFinishedAt() == null) {
            userChallenge.setFinishedAt(LocalDateTime.now());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "To wyzwanie zostało już wcześniej zakończone");
        }

        userChallengeRepository.save(userChallenge);
    }

    public void setUserChallengeScore(UUID id, Integer score) {
        UserChallenge userChallenge = getUserChallengeById(id);

        userChallenge.setScore(score);
        userChallengeRepository.save(userChallenge);
    }

    public void increaseUserChallengeScore(UUID id, Integer score) {
        UserChallenge userChallenge = getUserChallengeById(id);

        userChallenge.setScore(userChallenge.getScore() + score);
        userChallengeRepository.save(userChallenge);
    }

    public void deleteUserChallenge(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);

        userChallengeRepository.delete(userChallenge);
        // TODO soft delete
    }

    public void completeUserChallengeIfValid(UUID id) {
        if (checkIfAllUserSummitsAreConquered(id)) {
            setUserChallengeFinishedTime(id);
        }
    }

    protected UserChallenge getUserChallengeById(UUID id) {
        return userChallengeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Wyzwanie użytkownika o numerze id '%s' nie istnieje", id)));
    }

    private User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login nie istnieje"));
    }

    private boolean checkIfAllUserSummitsAreConquered(UUID id) {
        UserChallenge userChallenge = getUserChallengeById(id);
        return userChallenge.getUserSummitList().stream().allMatch(s -> s.getConqueredAt() != null);
    }

    private List<UserChallenge> getAllUserChallenges(String login) {
        User user = getUserByLogin(login);
        return userChallengeRepository.findAllByUserId(user.getId());
    }

}
