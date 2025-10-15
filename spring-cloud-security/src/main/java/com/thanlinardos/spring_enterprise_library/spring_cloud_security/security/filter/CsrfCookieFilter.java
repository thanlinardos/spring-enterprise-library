package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Nonnull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that ensures the CSRF token is loaded and rendered to a cookie for each request.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    /**
     * Default constructor.
     */
    public CsrfCookieFilter() {
        // Default constructor
    }

    /**
     * Ensures the CSRF token is loaded and rendered to a cookie for each request.
     * Hooks into the servlet {@link FilterChain} to process the request and response.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet exception occurs
     * @throws IOException      if an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        // Render the token value to a cookie by causing the deferred token to be loaded
        csrfToken.getToken();
        filterChain.doFilter(request, response);
    }
}
