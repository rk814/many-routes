package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.service.ChallengeService;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static com.codecool.kgp.config.SpringSecurityConfig.USER;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService, ChallengeMapper challengeMapper) {
        this.challengeService = challengeService;
    }

    @GetMapping()
    @RolesAllowed({ADMIN, USER})
    public List<ChallengeDto> getChallenges(@RequestParam(required = false, defaultValue = "ACTIVE") Status status,
                                            @RequestParam(required = false) List<String> fields) {
        log.info("Received request for all challenges");
        return challengeService.getAllChallenges(status, fields);
    }

//    @GetMapping("/simplified")
//    @RolesAllowed({ADMIN, USER})
//    public List<ChallengeSimpleDto> getChallengesSimplified() {
//        log.info("Received request for all challenges simplified");
//        return challengeService.getAllChallengesSimplified();
//    }

    @GetMapping("/{id}")
    @RolesAllowed({ADMIN, USER})
    public ChallengeDto getChallenge(@PathVariable UUID id) {
        log.info("Received request for the challenge with id '{}'", id);
        return challengeService.getChallenge(id);
    }

    @PostMapping("/add-new")
    @RolesAllowed({ADMIN})
    public ChallengeDto addChallenge(@RequestBody ChallengeRequestDto dto) {
        log.info("Received request for a new challenge");
        return challengeService.addNewChallenge(dto);
    }

    @PostMapping("/{id}/attach-summit/{summitId}")
    @RolesAllowed({ADMIN})
    public ChallengeDto attachSummit(@PathVariable UUID id, @PathVariable UUID summitId) {
        log.info("Received request for attach summit with id '{}' to challenge with id '{}'", summitId, id);
        return challengeService.attachSummitToChallenge(summitId, id);
    }

    //TODO
    @PutMapping("/{id}")
    @RolesAllowed({ADMIN})
    public ChallengeDto updateChallenge() {
        throw new ResponseStatusException(HttpStatusCode.valueOf(501), "Not implemented yet");
    }

    //TODO
    @DeleteMapping("/{id}")
    @RolesAllowed({ADMIN})
    public ChallengeDto deleteChallenge() {
        throw new ResponseStatusException(HttpStatusCode.valueOf(501), "Not implemented yet");
    }
}
