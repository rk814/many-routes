package pl.manyroutes.service;

import pl.manyroutes.auth.LdtToDateAdapter;
import pl.manyroutes.config.AuthConfigProperties;
import pl.manyroutes.controller.dto.jwt.JwtTokenResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtTokenService {

    private final AuthConfigProperties authConfigProperties;

    public JwtTokenService(AuthConfigProperties authConfigProperties) {
        this.authConfigProperties = authConfigProperties;
    }


    public String getUserNameFromToken(String jwtToken) {
        return extractClaims(jwtToken).getSubject();
    }

    public boolean validateToken(String jwtToken, UserDetails userDetails) throws ExpiredJwtException {
        boolean isExpired = getExpirationDateFromToken(jwtToken).before(new Date());
        return !isExpired;
    }

    public JwtTokenResponseDto generateJwtTokenDto(String login) {
        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", user.id());
        return new JwtTokenResponseDto(generateJwtToken(login, claims));
    }

    private String generateJwtToken(String subject, Map<String, Object> claims) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plus(authConfigProperties.validity());

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new LdtToDateAdapter(now))
                .expiration(new LdtToDateAdapter(expiration))
                .signWith(getKey())
                .compact();
    }

//    public UUID getUserIdFromToken(String jwtToken) {
//        return (UUID) extractClaims(jwtToken).get("user-id");
//    }

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
