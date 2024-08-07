package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.controller.validation.UserBasic;
import com.codecool.kgp.controller.validation.UserLogin;
import com.codecool.kgp.controller.validation.UserRegister;
import com.codecool.kgp.controller.validation.UserUpdate;
import com.codecool.kgp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<UserDto> getUsers() {
        log.info("Received request for all users");
        return userService.getUsers();
    }

    @GetMapping("/{login}") // in use
    public UserDto getUser(@PathVariable String login) {
        log.info("Received request for user with login '{}'", login);
        return userService.getUser(login);
    }

    @GetMapping("/{login}/score") // in use
    public int getUserScore(@PathVariable String login) {
        log.info("Received request for user score with login '{}'", login);
        return userService.getUserScore(login);
    }

    @PostMapping // in use
    public UserDto registerUser(@Validated({UserRegister.class, UserBasic.class}) @RequestBody UserRequestDto dto) {
        log.info("Received request for user registration with login '{}'", dto.login());
        return userService.setUser(dto);
    }

    @PostMapping("/{login}") // in use
    public UserDto loginUser(@PathVariable String login, @Validated({UserLogin.class, UserBasic.class}) @RequestBody UserRequestDto dto) {
        log.info("Received request for user sign in with login '{}'", login);
        return userService.logInUser(login, dto);
    }

    @PutMapping("/{login}") // in use
    public UserDto updateUser(@PathVariable String login, @Validated({UserUpdate.class, UserBasic.class}) @RequestBody UserRequestDto dto) {
        log.info("Received request for user update with login '{}'", login);
        return userService.updateUser(login, dto);
    }

    @DeleteMapping("/{login}/hard")
    public void deleteUser(@PathVariable String login) {
        log.info("Received request to delete user with login '{}'", login);
        userService.deleteUser(login);
    }

    @DeleteMapping("/{login}")
    public void softDeleteUser(@PathVariable String login) {
        log.info("Received request to soft delete user with login '{}'", login);
        userService.softDeleteUser(login);
    }

    // TODO user lists endpoints
}
