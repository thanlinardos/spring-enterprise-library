package com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface RoleService<T extends Role> {

    T findRole(String name);

    /**
     * Finds roles by their names.
     *
     * @param names the names of the roles to find, with the ROLE_ prefix.
     * @return a collection of roles that match the given names.
     */
    Collection<T> findRoles(Collection<String> names);

    Collection<GrantedAuthority> findGrantedAuthoritiesWithRole(T role);

    Collection<GrantedAuthority> findGrantedAuthoritiesWithRoles(Collection<String> roleNames);

    Collection<Authority> getAllAuthorities();

    int getPrivilegeLevelFromRoleNames(Collection<String> roleNames);

    Collection<T> getAllRoles();
}
