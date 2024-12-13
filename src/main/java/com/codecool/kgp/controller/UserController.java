package com.codecool.kgp.controller;

import com.codecool.kgp.config.swagger.ApiGeneralResponses;
import com.codecool.kgp.config.swagger.ApiRetrieveUpdateDeleteResponses;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.entity.CustomUserDetails;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.service.UserService;
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

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static com.codecool.kgp.config.SpringSecurityConfig.USER;


@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/users")
@Transactional
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @GetMapping("/")
    @Operation(summary = "Retrieve all users")
    @ApiGeneralResponses
    @RolesAllowed({ADMIN})
    public List<UserDto> getUsers() {
        log.info("Received request for all users");
        List<User> users = userService.getUsers();
        return users.stream().map(userMapper::mapEntityToDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve user by id")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed(ADMIN)
    public UserDto getUser(@PathVariable UUID id) {
        log.info("Received request for user with id '{}'", id);
        User user = userService.getUser(id);
        return userMapper.mapEntityToDto(user);
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve user")
    @ApiGeneralResponses
    @RolesAllowed({USER, ADMIN})
    public UserDto getUser(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID id = cud.getUserId();
        log.info("Received request for user with login '{}'", id);
        User user = userService.getUser(id);
        return userMapper.mapEntityToDto(user);
    }

    @GetMapping("/me/score")
    @Operation(summary = "Retrieve user score")
    @ApiGeneralResponses
    @RolesAllowed({USER, ADMIN})
    public int getUserScore(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID id = cud.getUserId();
        log.info("Received request for user score with id '{}'", id);
        return userService.getUserScore(id);
    }

    @PutMapping("/me")
    @Operation(summary = "Update user")
    @ApiGeneralResponses
    @RolesAllowed({USER, ADMIN})
    public UserDto updateUser(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserRequestDto dto) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID id = cud.getUserId();
        log.info("Received request for user update with id '{}'", id);
        User user = userService.updateUser(id, dto);
        return userMapper.mapEntityToDto(user);
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete user")
    @ApiGeneralResponses
    @RolesAllowed({USER, ADMIN})
    public void deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                           @Parameter(description = "If true, user will be hard deleted")
                           @RequestParam(required = false, defaultValue = "false") boolean hardDelete) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID id = cud.getUserId();
        log.info("Received request to delete user with id '{}'", id);
        deleteOrSoftDeleteUser(id, hardDelete);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed(ADMIN)
    public void deleteUser(@PathVariable UUID id,
                           @Parameter(description = "If true, user will be hard deleted")
                           @RequestParam(required = false, defaultValue = "false") boolean hardDelete) {
        log.info("Received request to soft delete user with id '{}'", id);
        deleteOrSoftDeleteUser(id, hardDelete);
    }

    private void deleteOrSoftDeleteUser(UUID id, boolean hardDelete) {
        if (hardDelete) {
            userService.deleteUser(id);
        } else {
            userService.softDeleteUser(id);
        }
    }
}
