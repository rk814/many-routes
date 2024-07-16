package com.codecool.kgp.controller.dto;

public record UserRequestDto(
    String login,
    String password,
    String email
) {

}
