package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.handler;

import jakarta.annotation.Nonnull;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;

/**
 * A no-operation implementation of CompromisedPasswordChecker that always returns
 * a decision indicating the password is not compromised.
 */
public class NoOpCompromisedPasswordChecker implements CompromisedPasswordChecker {

    /**
     * Default constructor.
     */
    public NoOpCompromisedPasswordChecker() {
        // Default constructor
    }

    /**
     * Always returns a decision indicating the password is not compromised.
     *
     * @param password the password to check.
     * @return a CompromisedPasswordDecision indicating the password is not compromised.
     */
    @Override
    @Nonnull
    public CompromisedPasswordDecision check(String password) {
        return new CompromisedPasswordDecision(false);
    }
}
