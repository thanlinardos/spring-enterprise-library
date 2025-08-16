package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface Role {

    String getName();

    int getPrivilegeLvl();

    <T extends Authority> List<T> getAuthorities();

    Collection<GrantedAuthority> getGrantedAuthorities();
}
