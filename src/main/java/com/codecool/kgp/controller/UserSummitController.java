package com.codecool.kgp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/users/{login}/user-summits/")
public class UserSummitController {

    @PostMapping("/{id}/conquer/{score}")
    public void conquerSummit(@PathVariable String login, @PathVariable UUID id, @PathVariable Integer score) {
        // conquer summit
        // add score to challenge -> on backend
        // check to challenge finish -> on backend
    }
}
