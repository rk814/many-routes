package com.codecool.kgp.mockuser;

import com.codecool.kgp.entity.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;
import java.util.UUID;


public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UUID id = UUID.randomUUID();

        CustomUserDetails principal = new CustomUserDetails(
                id,
                annotation.username(),
                annotation.password(),
                List.of(new SimpleGrantedAuthority("ROLE_" + annotation.role()))
        );

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
