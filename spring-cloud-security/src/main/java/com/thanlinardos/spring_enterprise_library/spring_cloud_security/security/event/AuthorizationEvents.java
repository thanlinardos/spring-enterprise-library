package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;

//@Component
@Slf4j
public class AuthorizationEvents {

//    @EventListener
    public <T> void onAuthenticationFailure(AuthorizationDeniedEvent<T> event) {
        log.error("Authorization failed for the user '{}' due to: {}",
                event.getAuthentication().get().getName(),
                event.getAuthorizationResult().toString());
    }
}
