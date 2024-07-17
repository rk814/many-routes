package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @GetMapping("/{login}")
    public UserDto getUser(@PathVariable String login) {
        return userService.getUserByLogin(login);
    }

    @PostMapping
    public UserDto registerUser(@Valid @RequestBody UserRequestDto dto) {
        return userService.setUser(dto);
    }

    @PostMapping("/{login}")
    public UserDto loginUser(@PathVariable String login, @Valid @RequestBody UserRequestDto dto) {
        return userService.logInUser(login, dto);
    }

    // TODO Valid groups one for register and second for update and third for login
    @PutMapping("/{login}")
    public UserDto updateUser(@PathVariable String login, @Valid @RequestBody UserRequestDto dto) {
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
