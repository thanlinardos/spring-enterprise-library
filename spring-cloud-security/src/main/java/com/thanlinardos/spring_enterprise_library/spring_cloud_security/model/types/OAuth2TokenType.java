package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OAuth2TokenType {

    JWT("JWT"),
    OPAQUE("OPAQUE");

    private final String value;

    OAuth2TokenType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OAuth2TokenType fromValue(String value) {
        for (OAuth2TokenType t : OAuth2TokenType.values()) {
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
