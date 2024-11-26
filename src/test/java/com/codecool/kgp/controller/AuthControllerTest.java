package com.codecool.kgp.controller;

import com.codecool.kgp.config.SpringSecurityConfig;
import com.codecool.kgp.config.WithMockCustomUser;
import com.codecool.kgp.controller.dto.RegistrationRequestDto;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.jwt.JwtTokenRequestDto;
import com.codecool.kgp.controller.dto.jwt.JwtTokenResponseDto;
import com.codecool.kgp.repository.UserRepository;
import com.codecool.kgp.service.AuthService;
import com.codecool.kgp.service.CustomUserDetailsService;
import com.codecool.kgp.service.JwtTokenService;
import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SpringSecurityConfig.class, CustomUserDetailsService.class})
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    private final Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;


    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void login() throws Exception {
        //given:
        JwtTokenRequestDto credentials = new JwtTokenRequestDto("adam_wanderlust", "safe-password123");
        JwtTokenResponseDto dto = new JwtTokenResponseDto("dummy_token");
        Mockito.when(jwtTokenService.generateJwtTokenDto("adam_wanderlust")).thenReturn(dto);

        //when:
        ResultActions response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(credentials))
        );

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
        response.andExpect(jsonPath("$.token").value("dummy_token"));

        ArgumentCaptor<UsernamePasswordAuthenticationToken> token = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        Mockito.verify(authenticationManager).authenticate(token.capture());
        Assertions.assertThat(token.getValue()).isNotNull();
        Assertions.assertThat(token.getValue().getPrincipal()).isEqualTo("adam_wanderlust");

        ArgumentCaptor<String> login = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jwtTokenService).generateJwtTokenDto(login.capture());
        Assertions.assertThat(login.getValue()).isEqualTo("adam_wanderlust");
    }

    @Test
    void registerUser() throws Exception {
        //given:
        RegistrationRequestDto dto = new RegistrationRequestDto("new_login", "password", "new_login@email.com");
        UserDto userDto = Instancio.of(UserDto.class)
                .set(field(UserDto::login), "new_login")
                .set(field(UserDto::email), "new_login@email.com")
                .create();
        Mockito.when(authService.registerNewUser(dto)).thenReturn(userDto);

        //when:
        ResultActions response = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(dto))
        );

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
        response.andExpect(jsonPath("$.login").value("new_login"));

        Mockito.verify(authService, Mockito.times(1)).registerNewUser(dto);
    }
}