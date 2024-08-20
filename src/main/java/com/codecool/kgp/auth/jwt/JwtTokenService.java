package com.codecool.kgp.auth.jwt;

import com.codecool.kgp.auth.config.AuthConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

public class JwtTokenService {

    private final AuthConfigProperties authConfigProperties;

    public JwtTokenService(AuthConfigProperties authConfigProperties) {
        this.authConfigProperties = authConfigProperties;
    }


    public String generateJwtToken(String userName) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plus(authConfigProperties.validity());

        return Jwts.builder()
                .subject(userName)
                .issuedAt(new LdtToDateAdapter(now))
                .expiration(new LdtToDateAdapter(expiration))
                .signWith(getKey())
                .compact();
    }

    public String getUserNameFromToken(String jwtToken) {
        return  extractClaims(jwtToken).getSubject();
    }

    public boolean validateToken(String jwtToken, UserDetails userDetails) {
        boolean isExpired = getExpirationDateFromToken(jwtToken).before(new Date());
        return !isExpired;
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
