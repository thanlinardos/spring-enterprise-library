package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

/**
 * Utils for string and formatting operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    /**
     * Formats a message with the provided arguments if the message contains placeholders.
     *
     * @param message the message to format.
     * @param args    the arguments to insert into the message.
     * @return the formatted message if placeholders are present and args are provided; otherwise, the original message.
     */
    public static String formatMessageWithArgs(String message, Object... args) {
        return CollectionUtils.isNotEmpty(args) && message.contains("{0") ? MessageFormat.format(message, args) : message;
    }

    /**
     * Formats an error message by including the error code and name, along with the provided message and arguments.
     *
     * @param errorCode the error code to include in the message.
     * @param message   the base message to format.
     * @param args      the arguments to insert into the base message.
     * @return the formatted error message including the error code, name, and formatted base message.
     */
    public static String formatErrorMessageWithArgs(ErrorCode errorCode, String message, Object[] args) {
        String baseMessage = formatMessageWithArgs(message, args);
        return formatMessageWithArgs("[{0}-{1}] {2}", errorCode.getCode(), errorCode.getName(), baseMessage);
    }
}
