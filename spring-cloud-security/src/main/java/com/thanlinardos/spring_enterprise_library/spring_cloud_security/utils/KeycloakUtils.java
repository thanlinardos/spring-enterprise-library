package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

public class KeycloakUtils {

    private KeycloakUtils() {
    }

    public static <T extends Role> Collection<GrantedAuthority> getGrantedAuthoritiesFromJwt(Jwt jwt, RoleService<T> roleService) {
        if (jwt.getClaims().get("realm_access") instanceof Map<?, ?> realmAccess && !realmAccess.isEmpty()) {
            return getSimpleGrantedAuthoritiesFromRealmAccess(realmAccess, roleService);
        } else {
            return Collections.emptyList();
        }
    }

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
