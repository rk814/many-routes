package com.codecool.kgp.auth;

import com.codecool.kgp.entity.CustomUserDetails;
import com.codecool.kgp.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;


class JwtTokenFilterTest {

    private final JwtTokenService tokenService = Mockito.mock();
    private final UserDetailsService userDetailsService = Mockito.mock();

    private final HttpServletResponse response = Mockito.mock();
    private final HttpServletRequest request = Mockito.mock();
    private final FilterChain filterChain = Mockito.mock();

    private final JwtTokenFilter tokenFilter = new JwtTokenFilter(tokenService, userDetailsService);


    @Test
    void doFilterInternal_withValidToken() throws ServletException, IOException {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock();
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when((securityContext.getAuthentication())).thenReturn(null);

            //given:
            String validToken = "valid_token";
            String validUserName = "user_name";
            UserDetails userDetails = Instancio.of(CustomUserDetails.class)
                    .set(field(CustomUserDetails::getUsername), validUserName)
                    .create();
            Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            Mockito.when(tokenService.getUserNameFromToken(validToken)).thenReturn(validUserName);
            Mockito.when(userDetailsService.loadUserByUsername(validUserName)).thenReturn(userDetails);
            Mockito.when(tokenService.validateToken(validToken, userDetails)).thenReturn(true);

            //when:
            tokenFilter.doFilterInternal(request, response, filterChain);

            //then:
            Mockito.verify(tokenService, Mockito.times(1))
                    .getUserNameFromToken(validToken);
            Mockito.verify(tokenService, Mockito.times(1))
                    .validateToken(validToken, userDetails);
            Mockito.verify(userDetailsService, Mockito.times(1))
                    .loadUserByUsername(validUserName);

            ArgumentCaptor<UsernamePasswordAuthenticationToken> captorToken = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            Mockito.verify(securityContext, Mockito.times(1)).setAuthentication(captorToken.capture());
            Assertions.assertThat(captorToken).extracting(t -> t.getValue().getPrincipal())
                    .isEqualTo(userDetails);
            Assertions.assertThat(captorToken).extracting(t -> t.getValue().isAuthenticated()).isEqualTo(true);
        }
    }

    @Test
    void doFilterInternal_withExpiredToken() throws ServletException, IOException {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock();
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            //given:
            String expiredToken = "expired_token";
            String validUserName = "user_name";
            UserDetails userDetails = Instancio.of(CustomUserDetails.class)
                    .set(field(CustomUserDetails::getUsername), validUserName)
                    .create();
            Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
            Mockito.when(tokenService.getUserNameFromToken(expiredToken)).thenReturn(validUserName);
            Mockito.when(userDetailsService.loadUserByUsername(validUserName)).thenReturn(userDetails);
            Mockito.when(tokenService.validateToken(expiredToken, userDetails)).thenThrow(Instancio.create(ExpiredJwtException.class));

            //when:
            tokenFilter.doFilterInternal(request, response, filterChain);

            //then:
            Mockito.verify(tokenService, Mockito.times(1))
                    .getUserNameFromToken(expiredToken);
            Mockito.verify(tokenService, Mockito.times(1))
                    .validateToken(expiredToken, userDetails);
            Mockito.verify(userDetailsService, Mockito.times(1))
                    .loadUserByUsername(validUserName);

            Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
        }
    }

    @Test
    void doFilterInternal_withNullUserName() throws ServletException, IOException {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock();
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            //given:
            String token = "token";
            String userName = null;
            UserDetails userDetails = Instancio.of(CustomUserDetails.class)
                    .set(field(CustomUserDetails::getUsername), userName)
                    .create();
            Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            Mockito.when(tokenService.getUserNameFromToken(token)).thenReturn(userName);

            //when:
            tokenFilter.doFilterInternal(request, response, filterChain);

            //then:
            Mockito.verify(tokenService, Mockito.times(1)).getUserNameFromToken(token);
            Mockito.verify(tokenService, Mockito.never()).validateToken(token, userDetails);
            Mockito.verify(userDetailsService, Mockito.never()).loadUserByUsername(userName);
            Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
        }
    }

    @Test
    void doFilterInternal_withMissingBearer() throws ServletException, IOException {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock();
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            //given:
            String token = "token";
            String userName = "user_name";
            UserDetails userDetails = Instancio.of(CustomUserDetails.class)
                    .set(field(CustomUserDetails::getUsername), userName)
                    .create();
            Mockito.when(request.getHeader("Authorization")).thenReturn(" " + token);
            Mockito.when(tokenService.getUserNameFromToken(token)).thenReturn(userName);

            //when:
            tokenFilter.doFilterInternal(request, response, filterChain);

            //then:
            Mockito.verify(tokenService, Mockito.never()).getUserNameFromToken(token);
            Mockito.verify(tokenService, Mockito.never()).validateToken(token, userDetails);
            Mockito.verify(userDetailsService, Mockito.never()).loadUserByUsername(userName);
            Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
        }
    }

    @Test
    void doFilterInternal_withMissingClaims() throws ServletException, IOException {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock();
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            //given:
            String token = "token";
            String userName = "user_name";
            UserDetails userDetails = Instancio.of(CustomUserDetails.class)
                    .set(field(CustomUserDetails::getUsername), userName)
                    .create();
            Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            Mockito.when(tokenService.getUserNameFromToken(token)).thenThrow(new IllegalArgumentException());

            //when:
            tokenFilter.doFilterInternal(request, response, filterChain);

            //then:
            Mockito.verify(tokenService, Mockito.times(1)).getUserNameFromToken(token);
            Mockito.verify(tokenService, Mockito.never()).validateToken(token, userDetails);
            Mockito.verify(userDetailsService, Mockito.never()).loadUserByUsername(userName);
            Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
        }
    }

    @Test
    void doFilterInternal_withAuthenticationNotNull() throws ServletException, IOException {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock();
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when((securityContext.getAuthentication())).thenReturn(Instancio.create(UsernamePasswordAuthenticationToken.class));

            //given:
            String token = "token";
            String userName = "user_name";
            UserDetails userDetails = Instancio.of(CustomUserDetails.class)
                    .set(field(CustomUserDetails::getUsername), userName)
                    .create();
            Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            Mockito.when(tokenService.getUserNameFromToken(token)).thenReturn(userName);

            //when:
            tokenFilter.doFilterInternal(request, response, filterChain);

            //then:
            Mockito.verify(tokenService, Mockito.times(1)).getUserNameFromToken(token);
            Mockito.verify(userDetailsService, Mockito.never()).loadUserByUsername(userName);
            Mockito.verify(tokenService, Mockito.never()).validateToken(token, userDetails);
            Mockito.verify(securityContext, Mockito.never()).setAuthentication(any());
        }
    }
}