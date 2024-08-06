package com.codecool.kgp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/users")
public class UserChallengeController {

    @GetMapping("/{login}/challenges")
    public List<UserChallengeDao> getUserChallenges(@PathVariable String login) {
        return null;
    }

    @GetMapping("/{login}/challenges/{id}")
    public UserChallengeDao getUserChallenge(@PathVariable String login, @PathVariable UUID id) {
        return null;
    }

    @PostMapping("/{login}/challenges")
    public UserChallengeDao addUserChallenge(@PathVariable String login,
                                             @RequestBody UserChallengeRequestDto dto) {
        return null;
    }

    @PostMapping("/{login}/challenges/{id}")
    public void finishUserChallenge(@PathVariable String login, @PathVariable UUID id, @RequestBody FinishedAtDto dto) {
        // send finishedAt time-date
    }

    @PostMapping("/{login}/challenges/{challengeId}/summit/{summitId}")
    public void conquerSummit(@PathVariable String login, @PathVariable UUID challengeId, @PathVariable UUID summitId,
                               @RequestBody SummitRequestDto dto) {
        // conquer summit
        // add score to challenge
    }

    @DeleteMapping("/{login}/challenges/{id}")
    public void deleteUserChallenge(@PathVariable String login, @PathVariable UUID id) {

    }
}
