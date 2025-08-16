package com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants;

import java.util.List;

public class SecurityCommonConstants {

    private SecurityCommonConstants() {
    }

    public static final String ROLE_PREFIX = "ROLE_";

    public static final List<String> PUBLIC_READ_URLS = List.of(
            "/assets/**",
            "/error",
            "/login/**",
            "/invalidSession",
            "/contacts",
            "/home",
            "/",
            "/notices",
            "/favicon.ico",
            "/h2-console/**",
            "/actuator/**"
    );

    public static final List<String> PUBLIC_WRITE_URLS = List.of(
            "/contact",
            "/saveMsg",
            "/customers",
            "/actuator/**"
    );
}
