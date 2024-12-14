package pl.manyroutes.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequestDto(
        @NotBlank
        String login,
        @NotBlank
        @Size(min = 6, message = "Hasło musi posiadać minimum 6 znaków")
        String password,
        @Email
        String email
) {
}
