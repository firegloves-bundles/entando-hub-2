package com.entando.hub.catalog.config.filter;

import static com.entando.hub.catalog.config.ApplicationConstants.API_KEY_HEADER;
import static com.entando.hub.catalog.config.ApplicationConstants.CATALOG_ID_PARAM;
import static com.entando.hub.catalog.config.ApplicationConstants.INVALID_API_KEY_MSG;

import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import com.entando.hub.catalog.service.security.ApiKeyCatalogIdValidator;
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

    private PrivateCatalogApiKeyService privateCatalogApiKeyService;
    private ApiKeyCatalogIdValidator appBuilderCatalogValidator;

    public ApiKeyFilter(PrivateCatalogApiKeyService service,
            ApiKeyCatalogIdValidator appBuilderCatalogValidator) {
        this.privateCatalogApiKeyService = service;
        this.appBuilderCatalogValidator = appBuilderCatalogValidator;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        String catalogIdParam = request.getParameter(CATALOG_ID_PARAM);
        Long catalogId = null;
        if (StringUtils.isNotEmpty(catalogIdParam)) {
            catalogId = Long.valueOf(catalogIdParam);
        }
        if (StringUtils.isEmpty(apiKey) || privateCatalogApiKeyService.doesApiKeyExist(apiKey)) {
            boolean validateApiKeyCatalogId = appBuilderCatalogValidator.validateApiKeyCatalogId(apiKey, catalogId);
            if (Boolean.FALSE.equals(validateApiKeyCatalogId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_API_KEY_MSG);
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_API_KEY_MSG);
        }
    }
}
