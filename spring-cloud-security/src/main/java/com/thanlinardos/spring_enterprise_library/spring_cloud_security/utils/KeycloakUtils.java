package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

/**
 * Utility class for processing Keycloak-related information.
 */
public abstract class KeycloakUtils {

    private KeycloakUtils() {
    }

    /**
     * Extracts GrantedAuthority objects from the given JWT token
     * based on the roles found in the "realm_access" claim.
     *
     * @param jwt         the JWT token.
     * @param roleService the service to fetch roles and authorities.
     * @param <T>         the type of Role.
     * @return a collection of GrantedAuthority objects representing the roles, or an empty list if none found.
     */
    public static <T extends Role> Collection<GrantedAuthority> getGrantedAuthoritiesFromJwt(Jwt jwt, RoleService<T> roleService) {
        if (jwt.getClaims().get("realm_access") instanceof Map<?, ?> realmAccess && !realmAccess.isEmpty()) {
            return getSimpleGrantedAuthoritiesFromRealmAccess(realmAccess, roleService);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Extracts GrantedAuthority objects from the given realm access map
     * based on the roles found in the "roles" key.
     *
     * @param realmAccess the map containing realm access information.
     * @param roleService the service to fetch roles and authorities.
     * @param <T>         the type of Role.
     * @return a collection of GrantedAuthority objects representing the roles, or an empty list if none found.
     */
    public static <T extends Role> Collection<GrantedAuthority> getSimpleGrantedAuthoritiesFromRealmAccess(Map<?, ?> realmAccess, RoleService<T> roleService) {
        return parseRoleNamesStream(realmAccess, roleService).stream()
                .map(roleService::findGrantedAuthoritiesWithRole)
                .flatMap(Collection::stream)
                .toList();
    }

    private static <T extends Role> Collection<T> parseRoleNamesStream(Map<?, ?> realmAccess, RoleService<T> roleService) {
        return switch (realmAccess.get("roles")) {
            case List<?> roleList -> roleService.findRoles(roleList.stream()
                    .map(roleName -> ROLE_PREFIX + roleName)
                    .toList());
            case null, default -> Collections.emptyList();
        };
    }
}
