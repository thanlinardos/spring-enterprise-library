package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import java.util.Collection;

import static org.springframework.util.StringUtils.hasText;

/**
 * Utility class for configuring security authorization based on authorities.
 */
class SecurityAuthorization {

    /**
     * Configures the authorization registry with the given authorities and public URLs.
     *
     * @param authRegistry    the authorization manager request matcher registry to configure
     * @param authorities     the collection of authorities to configure
     * @param publicReadUrls  the array of public read URLs (accessible via GET requests)
     * @param publicWriteUrls the array of public write URLs (accessible via POST requests)
     */
    protected static void configureAuthRegistry(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRegistry, Collection<Authority> authorities, String[] publicReadUrls, String[] publicWriteUrls) {
        authorities.forEach(authority -> authority.getAccess().getMethods()
                .forEach(method -> addRequestMatcherForAuthorityUriAndMethod(authRegistry, authority, method)));
        authRegistry.requestMatchers(HttpMethod.GET, publicReadUrls).permitAll()
                .requestMatchers(HttpMethod.POST, publicWriteUrls).permitAll()
                .anyRequest().denyAll();
    }

    private static void addRequestMatcherForAuthorityUriAndMethod(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRegistry,
                                                                  Authority authority,
                                                                  HttpMethod method) {
        if (hasText(authority.getExpression())) {
            authRegistry.requestMatchers(method, authority.getUri()).access(new WebExpressionAuthorizationManager(authority.getExpression()));
        } else {
            authRegistry.requestMatchers(method, authority.getUri()).hasAuthority(authority.getName());
        }
    }

    private SecurityAuthorization() {
    }
}
