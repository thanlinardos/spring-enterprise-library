package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

import java.util.Optional;

public class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    public static String getPrincipalNameFromAuthentication(Authentication authentication) {
        Jwt principal = (Jwt) authentication.getPrincipal();
        return Optional.of(principal)
                .map(JwtClaimAccessor::getSubject)
                .or(() -> Optional.ofNullable(principal.getClaimAsString("email")))
                .or(() -> Optional.ofNullable(principal.getClaimAsString("client_id")))
                .orElse(null);
    }
}
