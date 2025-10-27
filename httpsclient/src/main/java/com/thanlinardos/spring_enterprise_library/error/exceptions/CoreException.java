package com.thanlinardos.spring_enterprise_library.error.exceptions;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import lombok.Getter;

import static com.thanlinardos.spring_enterprise_library.objects.utils.StringUtils.formatErrorMessageWithArgs;

/**
 * Core exception class that includes an {@link ErrorCode} and supports message formatting with arguments.
 */
@Getter
public class CoreException extends RuntimeException {

    private final ErrorCode errorCode;

    public CoreException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CoreException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public CoreException(ErrorCode errorCode, String message, Object... args) {
        super(formatErrorMessageWithArgs(errorCode, message, args));
        this.errorCode = errorCode;
    }

    public CoreException(ErrorCode errorCode, String message, Throwable cause, Object... args) {
        super(formatErrorMessageWithArgs(errorCode, message, args), cause);
        this.errorCode = errorCode;
    }
}
