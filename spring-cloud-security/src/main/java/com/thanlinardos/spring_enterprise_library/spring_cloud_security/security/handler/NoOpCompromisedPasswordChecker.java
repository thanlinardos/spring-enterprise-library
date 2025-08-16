package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.handler;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;

public class NoOpCompromisedPasswordChecker implements CompromisedPasswordChecker {

    @Override
    @NonNull
    public CompromisedPasswordDecision check(String password) {
        return new CompromisedPasswordDecision(false);
    }
}
