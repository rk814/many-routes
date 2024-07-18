package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.controller.validation.UserLogin;
import com.codecool.kgp.controller.validation.UserRegister;
import com.codecool.kgp.controller.validation.UserUpdate;
import com.codecool.kgp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return userService.getUsers();
    }

    @GetMapping("/{login}") // in use
    public UserDto getUser(@PathVariable String login) {
        return userService.getUserByLogin(login);
    }

    @PostMapping // in use
    public UserDto registerUser(@Valid @Validated(UserRegister.class) @RequestBody UserRequestDto dto) {
        return userService.setUser(dto);
    }

    @PostMapping("/{login}") // in use
    public UserDto loginUser(@PathVariable String login, @Valid @Validated(UserLogin.class) @RequestBody UserRequestDto dto) {
        return userService.logInUser(login, dto);
    }

    @PutMapping("/{login}") // in use
    public UserDto updateUser(@PathVariable String login, @Valid @Validated(UserUpdate.class) @RequestBody UserRequestDto dto) {
        return userService.updateUser(login, dto);
    }

    @DeleteMapping("/{login}/hard")
    public void deleteUser(@PathVariable String login) {
        userService.deleteUser(login);
    }

    @DeleteMapping("/{login}")
    public void softDeleteUser(@PathVariable String login) {
        userService.softDeleteUser(login);
    }

    // TODO user lists endpoints
}
