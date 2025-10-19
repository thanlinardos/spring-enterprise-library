package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum representing different types of access permissions.
 */
public enum AccessType {

    /** CRUD operation types mapped to HTTP methods. */
    CREATE("CREATE", HttpMethod.POST),
    /** Read operations. */
    READ("READ", HttpMethod.GET, HttpMethod.OPTIONS, HttpMethod.TRACE, HttpMethod.HEAD),
    /** Update operations. */
    UPDATE("UPDATE", HttpMethod.PUT),
    /** Patch operations. */
    PATCH("PATCH", HttpMethod.PATCH),
    /** Delete operations. */
    DELETE("DELETE", HttpMethod.DELETE),
    /** All operation types. */
    ALL("ALL", HttpMethod.values());

    private final String value;
    @Getter
    private final List<HttpMethod> methods;

    /**
     * Constructor for AccessType.
     *
     * @param value   the string value of the access type.
     * @param methods the associated HTTP methods.
     */
    AccessType(String value, HttpMethod... methods) {
        this.value = value;
        this.methods = Collections.unmodifiableList(Arrays.asList(methods));
    }

    /**
     * Returns the string value of the AccessType.
     *
     * @return the string value.
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Creates an AccessType enum from a string value.
     *
     * @param value the string value
     * @return the corresponding AccessType enum
     * @throws IllegalArgumentException if the value does not correspond to any AccessType
     */
    @JsonCreator
    public static AccessType fromValue(String value) {
        for (AccessType b : AccessType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
