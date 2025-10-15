package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom handler for access denied exceptions, providing a JSON response with details.
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Default constructor.
     */
    public CustomAccessDeniedHandler() {
        // Default constructor
    }

    /**
     * Handles an access denied failure.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException      when an input or output exception occurs.
     * @throws ServletException when a servlet exception occurs.
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (accessDeniedException != null && accessDeniedException.getMessage() != null)
                ? accessDeniedException.getMessage()
                : "Access Denied";
        String path = request.getRequestURI();

        response.setHeader("Access-Denied-Reason", "Access Denied");
        response.setHeader("Content-Type", "application/json");
        String jsonResponse = String
                .format("{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                        currentTimeStamp,
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        message,
                        path);
        response.getWriter().write(jsonResponse);
    }
}
