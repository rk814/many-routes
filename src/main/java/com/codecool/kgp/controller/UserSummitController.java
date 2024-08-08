package com.codecool.kgp.controller;

import com.codecool.kgp.service.UserChallengeService;
import com.codecool.kgp.service.UserSummitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/users/{login}/user-challenges/{userChallengeId}/user-summits/")
public class UserSummitController {

    private final UserSummitService userSummitService;
    private final UserChallengeService userChallengeService;

    public UserSummitController(UserSummitService userSummitService, UserChallengeService userChallengeService) {
        this.userSummitService = userSummitService;
        this.userChallengeService = userChallengeService;
    }

    @PostMapping("/{id}/conquer/{score}")
    public void conquerUserSummit(@PathVariable String login, @PathVariable UUID userChallengeId,
                                  @PathVariable UUID id, @PathVariable int score) {
        log.info("Received request to conquer user summit with id '{}' and login '{}'", id, login);
        if (score < 0) {
            log.warn("Request for conquer of user summit with id '{}' has invalid score value of {}", id, score);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Punkty (score) nie mogą być ujemne");
        }
        userSummitService.setConquered(id, score);
        userChallengeService.completeUserChallengeIfValid(userChallengeId);
        userChallengeService.increaseUserChallengeScore(userChallengeId, score);
        // ??? Add notification to user challenge finish
    }
}
