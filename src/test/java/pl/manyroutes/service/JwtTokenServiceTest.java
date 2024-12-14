package pl.manyroutes.service;

import pl.manyroutes.config.AuthConfigProperties;
import pl.manyroutes.controller.dto.jwt.JwtTokenResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtTokenServiceTest {

    private final String SECRET = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsb2dpbiIsImlhdCI6MTczMjc5MjA4NiwiZXhwIjoxODE5MTg4NDg2fQ.6l4zNsZ35c42QvK4ylFjL3tQ1D4DZj_-2C5F8RthNWwKbodpsXiF45lvmsKs-Gilow_HLLefBqkSaiPIU7bV7A";

    private final AuthConfigProperties authConfigProperties = Mockito.mock();
    private final JwtTokenService jwtTokenService = new JwtTokenService(authConfigProperties);


    @Test
    @Order(1)
    void getUserNameFromToken_shouldGetNameFromToken() {
        //given:
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsb2dpbiIsImlhdCI6MTczMjc5MjM1OSwiZXhwIjoxMDM3Mjc4ODc1OX0.ENUHGbpWHQUE7O50NncNKw9_zAd90Vkv25HTpwJ2zkukkSIZ8Pd5naT6nHB2plEuXJU2aL5gEZUHLS_-SW6mpg";
        Mockito.when(authConfigProperties.secret()).thenReturn(SECRET);

        //when:
        String actual = jwtTokenService.getUserNameFromToken(token);

        //then:
        Assertions.assertThat(actual).isEqualTo("login");
    }

    @Test
    @Order(2)
    void validateToken_shouldReturnTrue() {
        //given:
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsb2dpbiIsImlhdCI6MTczMjc5MjM1OSwiZXhwIjoxMDM3Mjc4ODc1OX0.ENUHGbpWHQUE7O50NncNKw9_zAd90Vkv25HTpwJ2zkukkSIZ8Pd5naT6nHB2plEuXJU2aL5gEZUHLS_-SW6mpg";
        Mockito.when(authConfigProperties.secret()).thenReturn(SECRET);

        //when:
        boolean actual = jwtTokenService.validateToken(token, null);

        //then:
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @Order(3)
    void validateToken_shouldThrowExpiredJwtException() {
        //given:
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsb2dpbiIsImlhdCI6MTczMjc5ODY4OCwiZXhwIjoxNzMyNzk4Njg5fQ.4SSr2ayJs0xyVrptfmJyKHHyhxWA8ib3g_1vfzHfwWzjjN5M6-ZKI5QY6ZVIScA-JAnagw2vKgX1Qb8fP8X69g";
        Mockito.when(authConfigProperties.secret()).thenReturn(SECRET);

        //when:
        Throwable actual = Assertions.catchThrowable(() -> jwtTokenService.validateToken(token, null));

        //then:
        Assertions.assertThat(actual).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @Order(4)
    void generateJwtTokenDto_shouldGenerateTokenDto() {
        //given:
        String login = "login";

        Mockito.when(authConfigProperties.validity()).thenReturn(Duration.of(100_000, ChronoUnit.DAYS));
        Mockito.when(authConfigProperties.secret()).thenReturn(SECRET);

        //when:
        JwtTokenResponseDto actual = jwtTokenService.generateJwtTokenDto(login);

        //then:
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).extracting(JwtTokenResponseDto::token)
                .isNotNull();
        System.out.println(actual.token());
    }
}