package pl.manyroutes.controller.dto.jwt;

import jakarta.validation.constraints.NotBlank;

public record JwtTokenRequestDto(
        @NotBlank(message = "Pole 'login' jest obowiązkowe")
        String login,

        @NotBlank(message = "Pole 'hasło' jest obowiązkowe")
        String password
) {
}
