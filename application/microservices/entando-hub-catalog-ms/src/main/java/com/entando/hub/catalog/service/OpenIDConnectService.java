package com.entando.hub.catalog.service;

import com.entando.hub.catalog.service.exception.InvalidCredentialsException;
import com.entando.hub.catalog.service.exception.OidcException;
import com.entando.hub.catalog.service.model.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;

@Service("oidcService")
public class OpenIDConnectService {

    private static final Logger log = LoggerFactory.getLogger(OpenIDConnectService.class);
    
    public static final String EN_APP_CLIENT_PUBLIC = "entando-app-client-is-public";
    public static final String EN_APP_STANDARD_FLOW_DISABLED = "standard-flow-disabled";
    public static final String EN_APP_CLIENT_CREDENTIALS = "invalid-client-credentials";
    public static final String EN_APP_CLIENT_FORBIDDEN = "entando-app-client-doesnt-have-roles-to-manage-users";

    private final KeycloakSpringBootProperties configuration;
    private final String authToken;

    @Autowired
    public OpenIDConnectService(final KeycloakSpringBootProperties configuration) {
        this.configuration = configuration;
        
        final String authData = configuration.getResource() + ":" + configuration.getCredentials().get("secret");
        authToken = Base64.getEncoder().encodeToString(authData.getBytes());
    }
    
    public AuthResponse authenticateAPI() throws OidcException {
        try {
            final ResponseEntity<AuthResponse> response = requestClient();
            return HttpStatus.OK.equals(response.getStatusCode()) ? response.getBody() : null;
        } catch (HttpClientErrorException e) {
            if (HttpStatus.BAD_REQUEST.equals(e.getStatusCode()) && e.getResponseBodyAsString().contains("unauthorized_client")) {
                log.error("Unable to validate token because the Client credentials are invalid. " +
                          "Please make sure the credentials from keycloak is correctly set in the params or environment variable." +
                          "For more details, refer to the wiki " + EN_APP_CLIENT_CREDENTIALS, e);
                throw new InvalidCredentialsException(e);
            }
            if (HttpStatus.UNAUTHORIZED.equals(e.getStatusCode())) {
                log.error("There was an error while trying to load user because the " +
                        "client on Keycloak doesn't have permission to do that. " +
                        "The client needs to have Service Accounts enabled and the permission 'realm-admin' on client 'realm-management'. " +
                        "For more details, refer to the wiki " + EN_APP_CLIENT_FORBIDDEN, e);
                throw new OidcException(e);
            }
            log.error("There was an error while trying to authenticate, " +
                            "this might indicate a misconfiguration on Keycloak {}",
                    e.getResponseBodyAsString(), e);
            throw new OidcException(e);
        }
    }
    
    private ResponseEntity<AuthResponse> requestClient() {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpEntity<MultiValueMap<String, String>> req = createApiAuthenticationRequest();
        final String url = String.format("%s/realms/%s/protocol/openid-connect/token", configuration.getAuthServerUrl(), configuration.getRealm());
        return restTemplate.postForEntity(url, req, AuthResponse.class);
    }
    
    private HttpEntity<MultiValueMap<String, String>> createApiAuthenticationRequest() {
        final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + authToken);
        return new HttpEntity<>(body, headers);
    }
    
}
