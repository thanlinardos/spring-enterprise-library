package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication() != null ? event.getAuthentication().getName() : "null";
        log.info("Login successful for user: {}", username);
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication() != null ? event.getAuthentication().getName() : "null";
        String message = event.getException() != null ? event.getException().getMessage() : "null";
        log.error("Login failed for user: {} due to: {}", username, message);
    }
}
