package com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;

public interface PrivilegedResourceService {

    /**
     * Check if the currently authenticated principal can access the resource.
     *
     * @param resource - the resource to access
     * @return true if the current principal can access the resource, false otherwise
     */
    boolean canCurrentPrincipalAccessResource(PrivilegedResource resource);
}
