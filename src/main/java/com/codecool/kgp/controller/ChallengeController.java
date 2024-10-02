package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.ChallengeDto;
import com.codecool.kgp.controller.dto.ChallengeRequestDto;
import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.errorhandling.ErrorResponseDto;
import com.codecool.kgp.mappers.ChallengeMapper;
import com.codecool.kgp.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static com.codecool.kgp.config.SpringSecurityConfig.USER;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ChallengeMapper challengeMapper;

    public ChallengeController(ChallengeService challengeService, ChallengeMapper challengeMapper) {
        this.challengeService = challengeService;
        this.challengeMapper = challengeMapper;
    }


    @Operation(summary = "Get list of challenges (default ACTIVE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Challenges found"),
            @ApiResponse(responseCode = "400", description = "Invalid status supplied")
    })
    @GetMapping(produces = "application/json")
    @RolesAllowed({ADMIN, USER})
    public List<ChallengeDto> getChallenges(
            @Parameter(description = "Challenge status")
            @RequestParam(required = false, defaultValue = "ACTIVE") Status status,
            @Parameter(description = "A comma-separated list of field names to customize the fields returned in the ChallengeDto response", example = "name,id")
            @RequestParam(required = false) List<String> fields) {
        log.info("Received request for all challenges");
        List<Challenge> challenges;
        if (fields != null && !fields.contains("summits")) {
            challenges = challengeService.getAllChallengesWithoutSummitLists(status);
        } else {
            challenges = challengeService.getAllChallenges(status);
        }

        return challenges.stream().map(challenge ->
                (fields != null) ? challengeMapper.mapEntityToDto(challenge, fields)
                        : challengeMapper.mapEntityToDto(challenge)
        ).toList();
    }

    @GetMapping("/{id}")
    @RolesAllowed({ADMIN, USER})
    public ChallengeDto getChallenge(@PathVariable UUID id) {
        log.info("Received request for the challenge with id '{}'", id);
        Challenge challenge = challengeService.getChallenge(id);
        return challengeMapper.mapEntityToDto(challenge);
    }

    @PostMapping("/add-new")
    @RolesAllowed({ADMIN})
    public ChallengeDto addChallenge(@RequestBody ChallengeRequestDto dto) {
        log.info("Received request for a new challenge");
        Challenge challenge = challengeMapper.mapRequestDtoToEntity(dto);
        Challenge savedChallenge = challengeService.addNewChallenge(challenge);
        return  challengeMapper.mapEntityToDto(savedChallenge);
    }

    @PostMapping("/{id}/attach-summit/{summitId}")
    @RolesAllowed({ADMIN})
    public ChallengeDto attachSummit(@PathVariable UUID id, @PathVariable UUID summitId) {
        log.info("Received request for attach summit with id '{}' to challenge with id '{}'", summitId, id);
        Challenge challenge = challengeService.attachSummitToChallenge(summitId, id);
        return challengeMapper.mapEntityToDto(challenge);
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
