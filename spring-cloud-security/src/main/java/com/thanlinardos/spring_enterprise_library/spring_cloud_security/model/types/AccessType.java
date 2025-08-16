package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum AccessType {

    CREATE("CREATE", HttpMethod.POST),
    READ("READ", HttpMethod.GET, HttpMethod.OPTIONS, HttpMethod.TRACE, HttpMethod.HEAD),
    UPDATE("UPDATE", HttpMethod.PUT),
    PATCH("PATCH", HttpMethod.PATCH),
    DELETE("DELETE", HttpMethod.DELETE),
    ALL("ALL", HttpMethod.values());

    private final String value;
    @Getter
    private final List<HttpMethod> methods;

    AccessType(String value, HttpMethod... methods) {
        this.value = value;
        this.methods = Collections.unmodifiableList(Arrays.asList(methods));
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AccessType fromValue(String value) {
        for (AccessType b : AccessType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
