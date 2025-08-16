package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.RoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Role;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.OAuth2AuthServerType;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.OAuth2TokenType;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter.KeycloakOpaqueRoleConverter;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter.KeycloakRoleConverter;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.roleconverter.SpringAuthServerRoleConverter;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.filter.CsrfCookieFilter;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.handler.CustomAccessDeniedHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.PUBLIC_READ_URLS;
import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.PUBLIC_WRITE_URLS;

@Getter
public class SecurityCommonConfig<T extends Role> {

    @Value("${angular-ui.url}")
    private String angularUiUrl;
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}")
    private String introspectionUri;
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String clientSecret;
    @Value("${oauth2.keycloak.token}")
    private OAuth2TokenType tokenType;
    @Value("${oauth2.auth-server}")
    private OAuth2AuthServerType authServer;

    private final RoleService<T> roleService;

    public SecurityCommonConfig(RoleService<T> roleService) {
        this.roleService = roleService;
    }

    protected Collection<Authority> getAuthorities() {
        return Collections.emptyList();
    }

    protected String[] getAllPublicReadUrls() {
        return PUBLIC_READ_URLS.toArray(new String[0]);
    }

    protected String[] getAllPublicWriteUrls() {
        return PUBLIC_WRITE_URLS.toArray(new String[0]);
    }

    protected CorsConfiguration buildCorsConfiguration(List<String> allowedMethods, List<String> exposedHeaders, long maxAge) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList(getAngularUiUrl()));
        config.setAllowedMethods(allowedMethods);
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setExposedHeaders(exposedHeaders);   // expose Auth header from backend to UI
        config.setMaxAge(maxAge);
        return config;
    }

    protected SecurityFilterChain userLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        http.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // controls if this cookie is hidden from scripts on the client side
                .ignoringRequestMatchers("/contact", "/customers"));
        http.authorizeHttpRequests(registry -> SecurityAuthorization.configureAuthRegistry(registry, getAuthorities(), getAllPublicReadUrls(), getAllPublicWriteUrls()));
        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        // oauth2
        http.oauth2Login(o2l -> o2l.loginPage("/login"));
        switch (tokenType) {
            case JWT -> configureJwtToken(http);
            case OPAQUE -> configureOpaqueToken(http);
            default ->
                    throw new IllegalArgumentException("Unexpected value for the configured OAuth2 token type: " + tokenType);
        }
        http.requiresChannel(rc -> rc.anyRequest().requiresSecure());
        return http.build();
    }

    private void configureOpaqueToken(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(rsc ->
                rsc.opaqueToken(otc ->
                        otc.authenticationConverter(new KeycloakOpaqueRoleConverter<>(roleService))
                                .introspectionUri(introspectionUri)
                                .introspectionClientCredentials(clientId, clientSecret)));
    }

    private void configureJwtToken(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(getRoleConverter(authServer));
        http.oauth2ResourceServer(rsc ->
                rsc.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    }

    private Converter<Jwt, Collection<GrantedAuthority>> getRoleConverter(OAuth2AuthServerType authServer) {
        return switch (authServer) {
            case KEYCLOAK -> new KeycloakRoleConverter<>(roleService);
            case SPRING_OAUTH2_SERVER -> new SpringAuthServerRoleConverter();
        };
    }
}
