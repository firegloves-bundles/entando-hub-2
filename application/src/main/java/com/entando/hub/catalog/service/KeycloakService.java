package com.entando.hub.catalog.service;

import com.entando.hub.catalog.service.exception.OidcException;
import com.entando.hub.catalog.service.model.AuthResponse;
import com.entando.hub.catalog.service.model.UserRepresentation;
import org.apache.commons.lang3.StringUtils;
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
    
    
    
/*
    public void removeUser(final String uuid) {
        final String url = String.format("%s/admin/realms/%s/users/%s", configuration.getAuthServerUrl(), configuration.getRealm(), uuid);
        this.executeRequest(url, HttpMethod.DELETE, createEntity());
    }

    public void resetPassword(final String uuid, final String password, final Boolean temporary) {
        final String url = String.format("%s/admin/realms/%s/users/%s/reset-password", configuration.getAuthServerUrl(), configuration.getRealm(), uuid);
        final Map<String, Object> body = new HashMap<>();
        body.put("value", password);
        body.put("temporary", temporary);
        body.put("type", "password");
        this.executeRequest(url, HttpMethod.PUT, createEntity(body));
    }
*/

    //public String createUser(final UserRepresentation user) {
    //    final String url = String.format("%s/admin/realms/%s/users", configuration.getAuthServerUrl(), configuration.getRealm());
    //    final ResponseEntity<Void> response = this.executeRequest(url, HttpMethod.POST, createEntity(user));
    //    return Optional.ofNullable(response.getHeaders().getLocation())
    //            .map(location -> location.getPath().replaceAll(".*/([^/]+)$", "$1"))
    //            .orElseThrow(() -> new RuntimeException("User id response shouldn't return null from Keycloak"));
    //}
    /*
    public void updateUser(final UserRepresentation user) {
        final String url = String.format("%s/admin/realms/%s/users/%s", configuration.getAuthServerUrl(), configuration.getRealm(), user.getId());
        this.executeRequest(url, HttpMethod.PUT, createEntity(user));
    }
    */
    
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
    
    /*
    private <T> ResponseEntity<Void> executeRequest(final String url, final HttpMethod method, final HttpEntity<T> entity) {
        return this.executeRequest(url, method, entity, Void.class, Collections.emptyMap());
    }
    */
    private <T, Y> ResponseEntity<Y> executeRequest(final String url, final HttpMethod method, final HttpEntity<T> entity,
                                                    final Class<Y> result, final Map<String, String> params) {
        return executeRequest(url, method, entity, result, params, 0);
    }

    private <T, Y> ResponseEntity<Y> executeRequest(final String url, final HttpMethod method, final HttpEntity<T> entity,
                                                    final Class<Y> result, final Map<String, String> params, int retryCount) {
        this.authenticate();
        final RestTemplate restTemplate = new RestTemplate();
        try {
            final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            params.forEach(builder::queryParam);

            return restTemplate.exchange(builder.build().toUri(), method, createEntity(entity.getBody()), result);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.FORBIDDEN.equals(e.getStatusCode()) || (HttpStatus.UNAUTHORIZED.equals(e.getStatusCode()) && retryCount > 10)) {
                throw new RuntimeException("There was an error while trying to load user because the " +
                        "client on Keycloak doesn't have permission to do that. " +
                        "The client needs to have Service Accounts enabled and the permission 'realm-admin' on client 'realm-management'. " +
                        "For more details, refer to the wiki " + OpenIDConnectService.EN_APP_CLIENT_FORBIDDEN, e);
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
