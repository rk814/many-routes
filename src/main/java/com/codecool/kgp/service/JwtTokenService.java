package com.codecool.kgp.service;

import com.codecool.kgp.auth.LdtToDateAdapter;
import com.codecool.kgp.config.AuthConfigProperties;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.jwt.JwtTokenResponseDto;
import com.codecool.kgp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
public class JwtTokenService {

    private final AuthConfigProperties authConfigProperties;
    private final UserService userService;

    public JwtTokenService(AuthConfigProperties authConfigProperties, UserService userService) {
        this.authConfigProperties = authConfigProperties;
        this.userService = userService;
    }


    public String getUserNameFromToken(String jwtToken) {
        return  extractClaims(jwtToken).getSubject();
    }

    public boolean validateToken(String jwtToken, UserDetails userDetails) {
        boolean isExpired = getExpirationDateFromToken(jwtToken).before(new Date());
        return !isExpired;
    }

    public JwtTokenResponseDto generateJwtTokenDto(String login) {
        UserDto user = userService.getUser(login);
        return new JwtTokenResponseDto(
                generateJwtToken(login),
                user.id(),
                user.login(),
                user.name(),
                user.role()
        );
    }

    private String generateJwtToken(String login) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plus(authConfigProperties.validity());

        return Jwts.builder()
                .subject(login)
                .issuedAt(new LdtToDateAdapter(now))
                .expiration(new LdtToDateAdapter(expiration))
                .signWith(getKey())
                .compact();
    }

    private Date getExpirationDateFromToken(String jwtToken) {
        return extractClaims(jwtToken).getExpiration();
    }

    private Claims extractClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(authConfigProperties.secret().getBytes());
    }
}