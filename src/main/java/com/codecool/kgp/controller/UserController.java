package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static com.codecool.kgp.config.SpringSecurityConfig.USER;

// TODO validate if user is fetching only data belonging to him

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RolesAllowed({ADMIN})
    public List<UserDto> getUsers() {
        log.info("Received request for all users");
        return userService.getUsers();
    }

    @GetMapping("/{login}") // in use
    @RolesAllowed({USER, ADMIN})
    public UserDto getUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String login) {
        log.info("Received request for user with login '{}'", login);
        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.getAuthorities());
        return userService.getUser(login);
    }

    @GetMapping("/{login}/score") // in use
    @RolesAllowed({USER, ADMIN})
    public int getUserScore(@PathVariable String login) {
        log.info("Received request for user score with login '{}'", login);
        return userService.getUserScore(login);
    }

//    @PostMapping // in use
//    public UserDto registerUser(@Validated({UserRegister.class, UserBasic.class}) @RequestBody UserRequestDto dto) {
//        log.info("Received request for user registration with login '{}'", dto.login());
//        return userService.setUser(dto);
//    }
//
//    @PostMapping("/{login}") // in use
//    public UserDto loginUser(@PathVariable String login, @Validated({UserLogin.class, UserBasic.class}) @RequestBody UserRequestDto dto) {
//        log.info("Received request for user sign in with login '{}'", login);
//        return userService.logInUser(login, dto);
//    }

    @PutMapping("/{login}") // in use
    @RolesAllowed({USER, ADMIN})
    public UserDto updateUser(@PathVariable String login, @Valid @RequestBody UserRequestDto dto) {
        log.info("Received request for user update with login '{}'", login);
        return userService.updateUser(login, dto);
    }

    @RolesAllowed({ADMIN})
    @DeleteMapping("/{login}/hard")
    public void deleteUser(@PathVariable String login) {
        log.info("Received request to delete user with login '{}'", login);
        userService.deleteUser(login);
    }

    @RolesAllowed({USER, ADMIN})
    @DeleteMapping("/{login}")
    public void softDeleteUser(@PathVariable String login) {
        log.info("Received request to soft delete user with login '{}'", login);
        userService.softDeleteUser(login);
    }

    // ??? validate if user requests only for self content?
}
