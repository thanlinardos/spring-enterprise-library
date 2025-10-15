package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Interface representing a role with a name, privilege level, and associated authorities.
 */
public interface Role {

    /**
     * Gets the name of the role.
     *
     * @return the name of the role
     */
    String getName();

    /**
     * Gets the privilege level of the role.
     * The lower the level, the higher the privilege.
     *
     * @return Integer value of the privilege level
     */
    int getPrivilegeLvl();

    /**
     * Gets the list of authorities associated with the role.
     *
     * @param <T> the type of authority
     * @return the list of authorities associated with the role
     */
    <T extends Authority> List<T> getAuthorities();

    /**
     * Gets the collection of granted authorities associated with the role.
     *
     * @return the collection of granted authorities
     */
    Collection<GrantedAuthority> getGrantedAuthorities();
}
