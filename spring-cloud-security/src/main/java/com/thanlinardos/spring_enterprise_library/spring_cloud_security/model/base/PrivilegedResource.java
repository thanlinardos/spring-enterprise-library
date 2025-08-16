package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

public interface PrivilegedResource {

    /**
     * The privilege level required to access the resource.
     * The lower the level, the higher the privilege.
     *
     * @return Integer value of the privilege level required to access the resource.
     */
    int getPrivilegeLevel();

    /**
     * The principal name that owns the resource.
     *
     * @return String value of the principal name.
     */
    String getPrincipalName();

    @JsonIgnore
    @SuppressWarnings("unchecked")
    default Boolean samePrivilegeLevelCheck(Jwt principal) {
        Map<String, Object> claims = principal.getClaims();
        Map<String, Object> resourceAccess = Optional.ofNullable(claims.get("resource_access"))
                .map(Map.class::cast)
                .orElse(Collections.emptyMap());
        return matchesPrincipalNameToEmail(resourceAccess, claims) || matchesPrincipalNameToClientId(principal, claims, resourceAccess);
    }

    private Boolean matchesPrincipalNameToClientId(Jwt principal, Map<String, Object> claims, Map<String, Object> resourceAccess) {
        return Optional.ofNullable(claims.get("client_id"))
                .map(Object::toString)
                .filter(resourceAccess::containsKey)
                .map(clientID -> clientID.equals(this.getPrincipalName()))
                .orElse(principal.getSubject().equals(this.getPrincipalName()));
    }

    private boolean matchesPrincipalNameToEmail(Map<String, Object> resourceAccess, Map<String, Object> claims) {
        return resourceAccess.get("account") != null && claims.get("email") != null && claims.get("email").equals(this.getPrincipalName());
    }

    /**
     * The maximum privilege level that can access the resource.
     *
     * @return Integer value of the maximum privilege level that can access the resource.
     */
    @JsonIgnore
    default Integer getMaxPrivilegeLevel() {
        return Integer.MAX_VALUE;
    }

    static <T extends Role> int calcPrivilegeLvlFromRoles(List<T> roles) {
        return roles.stream()
                .map(Role::getPrivilegeLvl)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }
}
