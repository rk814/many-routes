package pl.manyroutes.controller;

import pl.manyroutes.config.swagger.ApiCreateResponses;
import pl.manyroutes.config.swagger.ApiGeneralResponses;
import pl.manyroutes.config.swagger.ApiRetrieveUpdateDeleteResponses;
import pl.manyroutes.controller.dto.ChallengeDto;
import pl.manyroutes.controller.dto.ChallengeRequestDto;
import pl.manyroutes.entity.enums.ChallengeFilter;
import pl.manyroutes.entity.Challenge;
import pl.manyroutes.auth.CustomUserDetails;
import pl.manyroutes.entity.enums.Status;
import pl.manyroutes.mappers.ChallengeMapper;
import pl.manyroutes.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static pl.manyroutes.config.SpringSecurityConfig.ADMIN;
import static pl.manyroutes.config.SpringSecurityConfig.USER;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/challenges")
@Transactional
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ChallengeMapper challengeMapper;

    public ChallengeController(ChallengeService challengeService, ChallengeMapper challengeMapper) {
        this.challengeService = challengeService;
        this.challengeMapper = challengeMapper;
    }


    @GetMapping(value = "/")
    @Operation(summary = "Retrieve list of challenges (default ACTIVE)")
    @ApiGeneralResponses
    @RolesAllowed({ADMIN, USER})
    public List<ChallengeDto> getChallenges(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Challenge status. Available options: 'ACTIVE', 'DEVELOP', 'REMOVED'")
            @RequestParam(required = false, defaultValue = "ACTIVE") Status status,
            @Parameter(description = "A comma-separated list of field names to customize the fields returned in the ChallengeDto response. " +
                    "If not provided, will return all fields, but summit list ", example = "name,id")
            @RequestParam(required = false, defaultValue = "id, name, description, status") List<String> fields,
            @Parameter(description = "Optional filter for challenges. Available options: 'unstarted' and 'all'.")
            @RequestParam(required = false, defaultValue = "ALL") ChallengeFilter filter) {

        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();

        log.info("Received request for all challenges from user with id '{}'", userId);

        List<Challenge> challenges = switch (filter) {
            case ALL -> challengeService.getAllChallenges(status, fields);
            case UNSTARTED -> challengeService.getUnstartedChallenges(userId, status, fields);
        };

        return challenges.stream().map(challenge -> challengeMapper.mapEntityToDto(challenge, fields)).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Receive challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({ADMIN, USER})
    public ChallengeDto getChallenge(@PathVariable UUID id) {
        log.info("Received request for the challenge with id '{}'", id);
        Challenge challenge = challengeService.getChallenge(id);
        return challengeMapper.mapEntityToDto(challenge);
    }

    @PostMapping("/add-new")
    @Operation(summary = "Create new challenge")
    @ApiCreateResponses
    @RolesAllowed({ADMIN})
    public ChallengeDto addChallenge(@RequestBody @Valid ChallengeRequestDto dto) {
        log.info("Received request for a new challenge");
        Challenge challenge = challengeMapper.mapRequestDtoToEntity(dto);
        Challenge savedChallenge = challengeService.addNewChallenge(challenge);
        return challengeMapper.mapEntityToDto(savedChallenge);
    }

    @PostMapping("/{id}/attach-summit/{summitId}")
    @Operation(summary = "Attach summit to challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({ADMIN})
    public ChallengeDto attachSummit(@PathVariable UUID id, @PathVariable UUID summitId) {
        log.info("Received request for attach summit with id '{}' to challenge with id '{}'", summitId, id);
        Challenge challenge = challengeService.attachSummitToChallenge(summitId, id);
        return challengeMapper.mapEntityToDto(challenge);
    }

    @PostMapping("/{id}/detach-summit/{summitId}")
    @Operation(summary = "Detach summit from challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({ADMIN})
    public ChallengeDto detachSummit(@PathVariable UUID id, @PathVariable UUID summitId) {
        log.info("Received request for detach summit with id '{}' to challenge with id '{}'", summitId, id);
        Challenge challenge = challengeService.detachSummitFromChallenge(summitId, id);
        return challengeMapper.mapEntityToDto(challenge);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({ADMIN})
    public ChallengeDto updateChallenge(@PathVariable UUID id, @RequestBody @Valid ChallengeRequestDto dto) {
        log.info("Received request for update challenge with id '{}'", id);
        Challenge challenge = challengeMapper.mapRequestDtoToEntity(dto);
        Challenge savedChallenge = challengeService.updateChallenge(id, challenge);
        return challengeMapper.mapEntityToDto(savedChallenge);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({ADMIN})
    public void deleteChallenge(@PathVariable UUID id) {
        log.info("Received request for delete challenge with id '{}'", id);
        challengeService.deleteChallenge(id);
    }
}
