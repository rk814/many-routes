package com.codecool.kgp.controller;

import com.codecool.kgp.service.UserChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.codecool.kgp.controller.dto.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/users/{login}/user-challenges")
public class UserChallengeController {

    private final UserChallengeService userChallengeService;

    public UserChallengeController(UserChallengeService userChallengeService) {
        this.userChallengeService = userChallengeService;
    }

    @GetMapping("/")
    public List<UserChallengeDao> getUserChallenges(@PathVariable String login) {
        return userChallengeService.getUserChallenges(login);
    }

    @GetMapping("/{id}")
    public UserChallengeDao getUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        return userChallengeService.getUserChallenge(id);
    }

    @PostMapping(value = "/add-new/{challengeId}")
    public UserChallengeDao addUserChallenge(@PathVariable String login, @PathVariable UUID challengeId) {
        return userChallengeService.saveUserChallenge(login, challengeId);
    }

    @PostMapping("/{id}/finish")
    public void completeUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        userChallengeService.setUserChallengeFinishedTime(id);
    }

    @PostMapping("/{id}/finish-with-validation")
    public void completeUserChallengeIfValid(@PathVariable String login, @PathVariable UUID id) {
        userChallengeService.completeUserChallengeIfValid(id);
    }

    @PatchMapping("/{id}/update-score/{score}")
    public void updateUserChallengeScore(@PathVariable String login, @PathVariable UUID id, @PathVariable Integer score) {
        if (score < 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Score can be only positive value or zero");
        }
        userChallengeService.setUserChallengeScore(id, score);
    }

    @PatchMapping("/{id}/add-score/{score}")
    public void increaseUserChallengeScore(@PathVariable String login, @PathVariable UUID id, @PathVariable Integer score) {
        if (score < 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Punkty (score) nie mogą być ujemne");
        }
        userChallengeService.increaseUserChallengeScore(id, score);
    }

    @DeleteMapping("/{id}")
    public void deleteUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        userChallengeService.deleteUserChallenge(id);
    }

}
