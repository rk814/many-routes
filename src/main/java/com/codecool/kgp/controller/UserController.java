package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/got/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }
}
