package com.codecool.kgp.auth;

import com.codecool.kgp.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String AUTHORIZATION = "Authorization";
    private final String BEARER = "Bearer";

    private final JwtTokenService tokenService;
    private final UserDetailsService userDetailsService;

    public JwtTokenFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.tokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String jwtToken = authHeader.substring(BEARER.length() + 1);

            try {
                String userName = tokenService.getUserNameFromToken(jwtToken);
                authenticateUser(jwtToken, userName);
            } catch (IllegalArgumentException e) {
                log.error("Unable to parse JWT token: {}", jwtToken);
            } catch (ExpiredJwtException e) {
                log.warn("JWT token '{}' has expired", jwtToken);
            }
        }

        filterChain.doFilter(request,response);
    }

    private void authenticateUser(String jwtToken, String userName) {
        if (userName!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            if (tokenService.validateToken(jwtToken, userDetails)) {
                var userToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(userToken);
            }
        }
    }
}
