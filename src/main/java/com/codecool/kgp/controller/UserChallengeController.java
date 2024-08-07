package com.codecool.kgp.controller;

import com.codecool.kgp.service.UserChallengeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.codecool.kgp.controller.dto.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/users/{login}/user-challenges")
public class UserChallengeController {

    private final UserChallengeService userChallengeService;

    public UserChallengeController(UserChallengeService userChallengeService) {
        this.userChallengeService = userChallengeService;
    }

    @GetMapping("/")
    public List<UserChallengeDto> getUserChallenges(@PathVariable String login) {
        log.info("Received request for all user challenges of the user with login '{}'", login);
        return userChallengeService.getUserChallenges(login);
    }

    @GetMapping("/{id}")
    public UserChallengeDto getUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        log.info("Received request for user challenge with id '{}' of the user with login '{}'", id, login);
        return userChallengeService.getUserChallenge(id);
    }

    @GetMapping("/completed")
    public List<UserChallengeDto> getCompletedUserChallenges(@PathVariable String login) {
        log.info("Received request for all completed user challenges of the user with login '{}'", login);
        return userChallengeService.getCompletedUserChallenges(login);
    }

    @GetMapping("/active")
    public List<UserChallengeDto> getActiveUserChallenges(@PathVariable String login) {
        log.info("Received request for all active user challenges of the user with login '{}'", login);
        return userChallengeService.getActiveUserChallenges(login);
    }

    // TODO move to ChallengeService
    @GetMapping("/goals")
    public List<ChallengeDto> getGoals(@PathVariable String login) {
        log.info("Received request for all goals of the user with login '{}'", login);
        return userChallengeService.getAvailableChallenges(login);
    }

    @PostMapping(value = "/add-new/{challengeId}")
    public UserChallengeDto addUserChallenge(@PathVariable String login, @PathVariable UUID challengeId) {
        log.info("Received request to add new user challenge with id '{}' for user with login '{}'", challengeId, login);
        return userChallengeService.saveUserChallenge(login, challengeId);
    }

    @PostMapping("/{id}/finish")
    public void completeUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        log.info("Received request to complete user challenge with id '{}' for user with login '{}'", id, login);
        userChallengeService.setUserChallengeFinishedTime(id);
    }

    @PostMapping("/{id}/finish-with-validation")
    public void completeUserChallengeWithValidation(@PathVariable String login, @PathVariable UUID id) {
        log.info("Received request to complete user challenge with validation with id '{}' for user with login '{}'", id, login);
        userChallengeService.completeUserChallengeIfValid(id);
    }

    @PatchMapping("/{id}/update-score/{score}")
    public void updateUserChallengeScore(@PathVariable String login, @PathVariable UUID id, @PathVariable Integer score) {
        log.info("Received request to update score of user challenge with id '{}' for user with login '{}'", id, login);
        if (score < 0) {
            log.warn("Request to update user score of user challenge with id '{}' had wrong value of '{}' ", id, score);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Score can be only positive value or zero");
        }
        userChallengeService.setUserChallengeScore(id, score);
    }

    @PatchMapping("/{id}/add-score/{score}")
    public void increaseUserChallengeScore(@PathVariable String login, @PathVariable UUID id, @PathVariable Integer score) {
        log.info("Received request to update score of user challenge with id '{}' for user with login '{}'", id, login);
        if (score < 0) {
            log.warn("Request to update user score of user challenge with id '{}' had wrong value of '{}' ", id, score);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Punkty (score) nie mogą być ujemne");
        }
        userChallengeService.increaseUserChallengeScore(id, score);
    }

    @DeleteMapping("/{id}")
    public void deleteUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        log.info("Received request to delete user challenge with id '{}' for user with login '{}'", id, login);
        userChallengeService.deleteUserChallenge(id);
    }

}
