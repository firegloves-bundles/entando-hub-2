package com.entando.hub.catalog.config.filter;

import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import liquibase.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;


@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    private PrivateCatalogApiKeyService service;

    public ApiKeyFilter(PrivateCatalogApiKeyService service) {
        this.service = service;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        String apiKey = request.getHeader("Entando-hub-api-key");

        if (StringUtils.isEmpty(apiKey) || service.doesApiKeyExist(apiKey)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
        }
    }
}
