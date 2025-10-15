package com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;

import java.util.Collection;
import java.util.Map;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.KeycloakUtils.getSimpleGrantedAuthoritiesFromRealmAccess;

/**
 * A converter that extracts roles from a Keycloak opaque token's introspection response
 * and converts them into Spring Security GrantedAuthority objects.
 *
 * @param <T> the type of Role
 */
public class KeycloakOpaqueRoleConverter<T extends Role> implements OpaqueTokenAuthenticationConverter {

    private final RoleService<T> roleService;

    /**
     * Constructor to initialize the KeycloakOpaqueRoleConverter with a RoleService.
     *
     * @param roleService the RoleService to manage roles.
     */
    public KeycloakOpaqueRoleConverter(RoleService<T> roleService) {
        this.roleService = roleService;
    }

    /**
     * Converts the introspected token and authenticated principal into an Authentication object
     * with roles extracted from the "realm_access" claim.
     *
     * @param introspectedToken      the introspected token.
     * @param authenticatedPrincipal the authenticated principal.
     * @return an Authentication object with roles as GrantedAuthority, or null if no roles found.
     */
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
