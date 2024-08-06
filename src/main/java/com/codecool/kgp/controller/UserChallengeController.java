package com.codecool.kgp.controller;

import org.springframework.web.bind.annotation.*;
import com.codecool.kgp.controller.dto.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/users/{login}/user-challenges")
public class UserChallengeController {

    @GetMapping("/")
    public List<UserChallengeDao> getUserChallenges(@PathVariable String login) {
        return null;
    }

    @GetMapping("/{id}")
    public UserChallengeDao getUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        return null;
    }

    @PostMapping(value = "/add-new/{challengeId}")
    public UserChallengeDao addUserChallenge(@PathVariable String login, @PathVariable UUID challengeId) {
        return null;
    }

    @PostMapping("/{id}/finish")
    public void finishUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        // send finishedAt time-date (generate on backend)
    }

    @PatchMapping("/{id}/update-score/{score}")
    public void updateUserChallengeScore(@PathVariable String login, @PathVariable UUID id, @PathVariable Integer score) {

    }

    @DeleteMapping("/{id}")
    public void deleteUserChallenge(@PathVariable String login, @PathVariable UUID id) {

    }

}
