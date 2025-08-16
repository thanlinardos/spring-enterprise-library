package com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.KeycloakUtils.getGrantedAuthoritiesFromJwt;

@RequiredArgsConstructor
public class KeycloakRoleConverter<T extends Role> implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RoleService<T> roleService;

    @Override
    public Collection<GrantedAuthority> convert(@NotNull Jwt jwt) {
        return getGrantedAuthoritiesFromJwt(jwt, roleService);
    }
}
