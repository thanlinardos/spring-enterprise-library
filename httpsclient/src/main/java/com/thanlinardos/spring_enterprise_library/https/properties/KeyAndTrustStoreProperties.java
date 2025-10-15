package com.thanlinardos.spring_enterprise_library.https.properties;

import org.springframework.core.io.Resource;

/**
 * Properties for configuring key and trust stores.
 *
 * @param path     the resource path to the key or trust store.
 * @param password the password for the key or trust store.
 */
public record KeyAndTrustStoreProperties(Resource path, String password) {

    /**
     * Checks if both the path and password are set (not null).
     *
     * @return true if both path and password are set, false otherwise.
     */
    public boolean isEnabled() {
        return path != null && password != null;
    }
}
