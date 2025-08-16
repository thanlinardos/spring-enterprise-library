package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OAuth2AuthServerType {

    KEYCLOAK("KEYCLOAK"),
    SPRING_OAUTH2_SERVER("SPRING_OAUTH2_SERVER");

    private final String value;

    OAuth2AuthServerType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OAuth2AuthServerType fromValue(String value) {
        for (OAuth2AuthServerType t : OAuth2AuthServerType.values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
