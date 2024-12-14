package pl.manyroutes.controller;

import pl.manyroutes.errorhandling.ErrorResponseDto;
import pl.manyroutes.service.JwtTokenService;
import pl.manyroutes.controller.dto.jwt.JwtTokenRequestDto;
import pl.manyroutes.controller.dto.jwt.JwtTokenResponseDto;
import pl.manyroutes.service.AuthService;
import pl.manyroutes.controller.dto.RegistrationRequestDto;
import pl.manyroutes.controller.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request has succeeded"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public JwtTokenResponseDto login(@Valid @RequestBody JwtTokenRequestDto dto) {
        log.info("Received request for sign in with login '{}'", dto.login());

        var userToken = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        authenticationManager.authenticate(userToken);
//        return new JwtTokenResponseDto(jwtTokenService.generateJwtToken(dto.login()));
        JwtTokenResponseDto jwtTokenResponseDto = jwtTokenService.generateJwtTokenDto(dto.login());
        log.info("User '{}' signed in successfully", dto.login());
        return jwtTokenResponseDto;
    }

    @PostMapping("/register")
    @Operation(summary = "Register user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request has succeeded"),
            @ApiResponse(responseCode = "409", description = "Name conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public UserDto registerUser(@Valid @RequestBody RegistrationRequestDto dto) {
        log.info("Received request for user registration with login '{}'", dto.login());
        return authService.registerNewUser(dto);
    }
}
