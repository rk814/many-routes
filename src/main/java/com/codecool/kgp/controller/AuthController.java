package com.codecool.kgp.controller;

import com.codecool.kgp.service.JwtTokenService;
import com.codecool.kgp.controller.dto.jwt.JwtTokenRequestDto;
import com.codecool.kgp.controller.dto.jwt.JwtTokenResponseDto;
import com.codecool.kgp.service.AuthService;
import com.codecool.kgp.controller.dto.RegistrationRequestDto;
import com.codecool.kgp.controller.dto.UserDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
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
        log.info("Received request for sign in with login '{}'", dto.login());

        var userToken = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        authenticationManager.authenticate(userToken);
//        return new JwtTokenResponseDto(jwtTokenService.generateJwtToken(dto.login()));
        return jwtTokenService.generateJwtTokenDto(dto.login());
    }

    @PostMapping("/register")
    public UserDto registerUser(@Valid @RequestBody RegistrationRequestDto dto) {
        log.info("Received request for user registration with login '{}'", dto.login());
        return authService.registerNewUser(dto);
    }
}
