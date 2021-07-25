package com.mycompany.myapp.security.apiKey;

import com.mycompany.myapp.web.rest.ApiKeyController;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
public class KeyFilter extends GenericFilterBean {

    public static final String APIKEY_HEADER = "ApiKey";

    private final ApiKeyController keyController;

    public KeyFilter(ApiKeyController keyController) { this.keyController = keyController; }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String apiKey = resolveToken(httpServletRequest);
        if (StringUtils.hasText(apiKey)) {
            keyController.setContextAuthentification(keyController.fetchUserByKey(apiKey).get());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(APIKEY_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
