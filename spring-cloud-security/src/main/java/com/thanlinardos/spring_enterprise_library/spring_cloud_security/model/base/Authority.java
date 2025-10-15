package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.AccessType;
import jakarta.annotation.Nullable;

/**
 * Interface representing an authority with a name, access type, URI, and optional expression.
 */
public interface Authority {

    /**
     * Gets the name of the authority.
     *
     * @return the name of the authority
     */
    String getName();

    /**
     * Gets the access type of the authority.
     *
     * @return the access type of the authority
     */
    AccessType getAccess();

    /**
     * Gets the URI associated with the authority.
     *
     * @return the URI associated with the authority
     */
    String getUri();

    /**
     * Gets the optional SPEL expression associated with the authority.
     *
     * @return the SPEL expression associated with the authority, or null if not applicable.
     */
    @Nullable
    String getExpression();
}
