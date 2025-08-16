package com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

public class SpringAuthServerRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // jwt.getClaims().get("scope")
        if (jwt.getClaims().get("roles") instanceof ArrayList<?> scopes && !scopes.isEmpty() && scopes.getFirst() instanceof String) {
            return scopes.stream()
                    .map(role -> ROLE_PREFIX + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } else {
            return new ArrayList<>();
        }
    }
}
