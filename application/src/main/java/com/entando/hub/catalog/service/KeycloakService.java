package com.entando.hub.catalog.service;

import com.entando.hub.catalog.service.exception.OidcException;
import com.entando.hub.catalog.service.model.AuthResponse;
import com.entando.hub.catalog.service.model.UserRepresentation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;

@Service
public class KeycloakService {

    private final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    private OpenIDConnectService oidcService;
    private KeycloakSpringBootProperties configuration;

    private String token;

    @Autowired
    public KeycloakService(final KeycloakSpringBootProperties configuration, final OpenIDConnectService oidcService) {
        this.configuration = configuration;
        this.oidcService = oidcService;
    }

    public List<UserRepresentation> listUsers() {
        return listUsers(new HashMap<>());
    }

    public List<UserRepresentation> searchUsers(Map<String, String> params) {
        return listUsers(params);
    }

    public List<UserRepresentation> listUsers(final String text) {
        final Map<String, String> params = StringUtils.isEmpty(text)
                ? Collections.emptyMap()
                : Collections.singletonMap("username", text);
        return this.listUsers(params);
    }
    
    public UserRepresentation getUser(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        final Map<String, String> params = Collections.singletonMap("username", username);
        List<UserRepresentation> list = this.listUsers(params);
        return list.stream().filter(ur -> ur.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }
    
    private List<UserRepresentation> listUsers(Map<String, String> params) {
        final String url = String.format("%s/admin/realms/%s/users", configuration.getAuthServerUrl(), configuration.getRealm());
        final ResponseEntity<UserRepresentation[]> response = this.executeRequest(url,
                HttpMethod.GET, createEntity(), UserRepresentation[].class, params);
        return response.getBody() != null ? Arrays.asList(response.getBody()) : Collections.emptyList();
    }
    
    private <T> HttpEntity<T> createEntity() {
        return createEntity(null);
    }

    private <T> HttpEntity<T> createEntity(final T body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        if (body != null) {
            headers.add("Content-Type", "application/json");
        }
        return new HttpEntity<>(body, headers);
    }
    
    private <T, Y> ResponseEntity<Y> executeRequest(final String url, final HttpMethod method, final HttpEntity<T> entity,
                                                    final Class<Y> result, final Map<String, String> params) {
        return executeRequest(url, method, entity, result, params, 0);
    }

    private <T, Y> ResponseEntity<Y> executeRequest(final String url, final HttpMethod method, final HttpEntity<T> entity,
                                                    final Class<Y> result, final Map<String, String> params, int retryCount) {
        logger.debug("Service call at: {}", url);
        this.authenticate();
        final RestTemplate restTemplate = new RestTemplate();
        try {
            final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            params.forEach(builder::queryParam);

            return restTemplate.exchange(builder.build().toUri(), method, createEntity(entity.getBody()), result);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.FORBIDDEN.equals(e.getStatusCode()) || (HttpStatus.UNAUTHORIZED.equals(e.getStatusCode()) && retryCount > 10)) {
                String message = String.format("There was an error while trying to load user because the " +
                        "client on Keycloak doesn't have permission to do that. " +
                        "The client needs to have Service Accounts enabled and the permission 'realm-admin' on client 'realm-management'. " +
                        "For more details, refer to the wiki %s. Error: %s", OpenIDConnectService.EN_APP_CLIENT_FORBIDDEN, e.getMessage());
                logger.warn("Service failure: {}", message);
                throw new RuntimeException(message, e);
            }
            if (HttpStatus.UNAUTHORIZED.equals(e.getStatusCode())) {
                invalidateToken();
                return this.executeRequest(url, method, entity, result, params, retryCount + 1);
            }
            throw e;
        }
    }

    private void authenticate() {
        if (token == null) {
            try {
                final AuthResponse authResponse = oidcService.authenticateAPI();
                token = authResponse.getAccessToken();
            } catch (OidcException e) {
                throw new RuntimeException("Error message", e);
            }
        }
    }
    private void invalidateToken() {
        this.token = null;
    }

}
