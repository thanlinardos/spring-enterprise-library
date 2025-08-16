package com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;

import java.util.Collection;
import java.util.Map;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.KeycloakUtils.getSimpleGrantedAuthoritiesFromRealmAccess;

@RequiredArgsConstructor
public class KeycloakOpaqueRoleConverter<T extends Role> implements OpaqueTokenAuthenticationConverter {

    private final RoleService<T> roleService;

    @Override
    public Authentication convert(String introspectedToken, OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
        Map<String, Object> realmAccess = authenticatedPrincipal.getAttribute("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) {
            return null;
        }

        Collection<GrantedAuthority> grantedAuthorities = getSimpleGrantedAuthoritiesFromRealmAccess(realmAccess, roleService);
        return new UsernamePasswordAuthenticationToken(authenticatedPrincipal.getName(), null, grantedAuthorities);
    }
}
