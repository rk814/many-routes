package com.codecool.kgp.controller;

import com.codecool.kgp.config.swagger.ApiCreateResponses;
import com.codecool.kgp.config.swagger.ApiGeneralResponses;
import com.codecool.kgp.config.swagger.ApiRetrieveUpdateDeleteResponses;
import com.codecool.kgp.entity.CustomUserDetails;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import com.codecool.kgp.entity.enums.UserChallengeFilter;
import com.codecool.kgp.mappers.UserChallengeMapper;
import com.codecool.kgp.service.UserChallengeService;
import com.codecool.kgp.validators.UserChallengeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.codecool.kgp.controller.dto.*;

import java.util.List;
import java.util.UUID;

import static com.codecool.kgp.config.SpringSecurityConfig.USER;


@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/users/me/user-challenges")
@Transactional
public class UserChallengeController {

    private final UserChallengeService userChallengeService;

    private final UserChallengeValidator userChallengeValidator;

    private final UserChallengeMapper userChallengeMapper;


    public UserChallengeController(UserChallengeService userChallengeService, UserChallengeValidator userChallengeValidator, UserChallengeMapper userChallengeMapper) {
        this.userChallengeService = userChallengeService;
        this.userChallengeValidator = userChallengeValidator;
        this.userChallengeMapper = userChallengeMapper;

    }

    @GetMapping("/")
    @Operation(summary = "Retrieve all user challenges")
    @ApiGeneralResponses
    @RolesAllowed({USER})
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
    @RolesAllowed({USER})
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
    @RolesAllowed({USER})
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
    @RolesAllowed({USER})
    public UserChallengeDto conquerSummit(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID userChallengeId,
                                          @PathVariable UUID userSummitId, @PathVariable int score) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request to conquer user summit with id '{}' and id '{}'", userSummitId, userId);
        userChallengeValidator.validateScore(UserSummit.class, userSummitId, score);
        UserChallenge userChallenge = userChallengeService.setSummitConquered(userChallengeId, userSummitId, score);
        return userChallengeMapper.mapEntityToDto(userChallenge);
    }

    @PatchMapping("/{userChallengeId}/update-score/{score}")
    @Operation(summary = "Update user challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({USER})
    public void updateUserChallengeScore(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable UUID userChallengeId, @PathVariable Integer score) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request to update score of user challenge with id '{}' for user with id '{}'", userChallengeId, userId);
        userChallengeValidator.validateScore(UserChallenge.class, userChallengeId, score);
        userChallengeService.setUserChallengeScore(userChallengeId, score);
    }

    @DeleteMapping("/{userChallengeId}")
    @Operation(summary = "Delete user challenge")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({USER})
    public void deleteUserChallenge(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID userChallengeId) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request to delete user challenge with id '{}' for user with id '{}'", userChallengeId, userId);
        userChallengeService.deleteUserChallenge(userChallengeId);
    }
}
