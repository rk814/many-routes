package com.codecool.kgp.auth;

import com.codecool.kgp.auth.jwt.JwtTokenService;
import com.codecool.kgp.auth.dto.*;
import com.codecool.kgp.auth.registration.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/got/v1/auth")
@Slf4j
public class AuthController {

    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    public AuthController(JwtTokenService jwtTokenService, AuthenticationManager authenticationManager, AuthService authService) {
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/login")
    public JwtTokenResponseDto login(@Valid @RequestBody JwtTokenRequestDto dto) {
        log.info("Received request for login with username: {}", dto.username());

        var userToken = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        authenticationManager.authenticate(userToken);
        return new JwtTokenResponseDto(jwtTokenService.generateJwtToken(dto.username()));
    }

    @PostMapping("/register")
    public RegisterUserDataDto registerUser(@Valid @RequestBody NewUserRegistrationDto dto) {
        return authService.registerNewUser(dto);
    }
}
