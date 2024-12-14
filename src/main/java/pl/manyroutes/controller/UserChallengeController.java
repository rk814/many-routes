package pl.manyroutes.controller;

import pl.manyroutes.config.swagger.ApiCreateResponses;
import pl.manyroutes.config.swagger.ApiGeneralResponses;
import pl.manyroutes.config.swagger.ApiRetrieveUpdateDeleteResponses;
import pl.manyroutes.auth.CustomUserDetails;
import pl.manyroutes.entity.UserChallenge;
import pl.manyroutes.entity.enums.UserChallengeFilter;
import pl.manyroutes.mappers.UserChallengeMapper;
import pl.manyroutes.service.UserChallengeService;
import pl.manyroutes.validators.ValidScore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.manyroutes.config.SpringSecurityConfig;
import pl.manyroutes.controller.dto.UserChallengeDto;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/users/me/user-challenges")
@Transactional
public class UserChallengeController {

    private final UserChallengeService userChallengeService;

    private final UserChallengeMapper userChallengeMapper;


    public UserChallengeController(UserChallengeService userChallengeService, UserChallengeMapper userChallengeMapper) {
        this.userChallengeService = userChallengeService;
        this.userChallengeMapper = userChallengeMapper;

    }

    @GetMapping("/")
    @Operation(summary = "Retrieve all user challenges")
    @ApiGeneralResponses
    @RolesAllowed({SpringSecurityConfig.USER})
    public List<UserChallengeDto> getUserChallenges(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Optional filter for user challenges. Available options: 'completed', 'uncompleted'. Use 'all' or leave blank for no filter.")
            @RequestParam(required = false, defaultValue = "ALL") UserChallengeFilter filter) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request for all user challenges of the user with id '{}'", userId);

        List<UserChallenge> userChallenges = switch (filter) {
            case COMPLETED -> userChallengeService.getCompletedUserChallenges(userId);
            case UNCOMPLETED -> userChallengeService.getUncompletedUserChallenges(userId);
            case ALL -> userChallengeService.getUserChallenges(userId);
        };
        return userChallenges.stream().map(userChallengeMapper::mapEntityToDto).toList();
    }

    @GetMapping("/{userChallengeId}")
    @Operation(summary = "Retrieve user challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({SpringSecurityConfig.USER})
    public UserChallengeDto getUserChallenge(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID userChallengeId) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request for user challenge with id '{}' of the user with id '{}'", userChallengeId, userId);
        UserChallenge userChallenge = userChallengeService.getUserChallenge(userChallengeId);
        return userChallengeMapper.mapEntityToDto(userChallenge);
    }

    @PostMapping(value = "/add-new/{challengeId}")
    @Operation(summary = "Create new user challenge")
    @ApiCreateResponses
    @RolesAllowed({SpringSecurityConfig.USER})
    public UserChallengeDto addUserChallenge(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID challengeId) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request to add new user challenge with id '{}' for user with id '{}'", challengeId, userId);
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(userId, challengeId);
        return userChallengeMapper.mapEntityToDto(userChallenge);
    }

    @PostMapping(value = "/{userChallengeId}/user-summits/{userSummitId}/conquer/{score}")
    @Operation(summary = "Mark user summit as conquer")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({SpringSecurityConfig.USER})
    public UserChallengeDto conquerSummit(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID userChallengeId,
                                          @PathVariable UUID userSummitId, @PathVariable @ValidScore int score) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request to conquer user summit with id '{}' and id '{}'", userSummitId, userId);
        UserChallenge userChallenge = userChallengeService.setSummitConquered(userChallengeId, userSummitId, score);
        return userChallengeMapper.mapEntityToDto(userChallenge);
    }

    @PatchMapping("/{userChallengeId}/update-score/{score}")
    @Operation(summary = "Update user challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({SpringSecurityConfig.USER})
    public void updateUserChallengeScore(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable UUID userChallengeId, @PathVariable @ValidScore Integer score) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request to update score of user challenge with id '{}' for user with id '{}'", userChallengeId, userId);
        userChallengeService.setUserChallengeScore(userChallengeId, score);
    }

    @DeleteMapping("/{userChallengeId}")
    @Operation(summary = "Delete user challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({SpringSecurityConfig.USER})
    public void deleteUserChallenge(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID userChallengeId) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request to delete user challenge with id '{}' for user with id '{}'", userChallengeId, userId);
        userChallengeService.deleteUserChallenge(userChallengeId);
    }
}
