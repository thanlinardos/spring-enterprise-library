package com.thanlinardos.spring_enterprise_library.spring_cloud_security.exception;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.OperationType;

/**
 * Custom exception class for handling Keycloak-related errors.
 */
public class KeycloakException extends Exception {

    /**
     * Default constructor.
     */
    public KeycloakException() {
        super();
    }

    /**
     * Constructor with a custom error message.
     *
     * @param message the error message.
     */
    public KeycloakException(String message) {
        super(message);
    }

    /**
     * Constructor with a custom error message and a cause.
     *
     * @param operationType the type of operation being performed.
     * @param inputClazz    the class of the input object.
     * @param cause         the cause of the exception.
     */
    public KeycloakException(OperationType operationType, Class<?> inputClazz, Throwable cause) {
        super(getErrorMsg(operationType, inputClazz.getSimpleName()) + " and cause: ", cause);
    }

    /**
     * Constructor with a custom error message and a cause.
     *
     * @param operationType the type of operation being performed.
     * @param input         the input object.
     * @param cause         the cause of the exception.
     */
    public KeycloakException(OperationType operationType, Object input, Throwable cause) {
        super(getErrorMsg(operationType, input) + " and cause: ", cause);
    }

    /**
     * Constructor with operation type and input object type.
     *
     * @param operationType the type of operation being performed.
     * @param inputClazz    the class of the input object.
     */
    public KeycloakException(OperationType operationType, Class<?> inputClazz) {
        super(getErrorMsg(operationType, inputClazz.getSimpleName()));
    }

    private static String getErrorMsg(OperationType operationType, String inputClassName) {
        return "Failed to execute Keycloak operationType: " + operationType + " with input type: " + inputClassName;
    }

    private static String getErrorMsg(OperationType operationType, Object input) {
        return "Failed to execute Keycloak operationType: " + operationType + " with input: " + input;
    }
}
