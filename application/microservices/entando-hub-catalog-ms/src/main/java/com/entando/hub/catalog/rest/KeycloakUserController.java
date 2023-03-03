package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.rest.dto.RestUserRepresentation;
import com.entando.hub.catalog.rest.model.SearchKeycloackUserRequest;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.model.UserRepresentation;

import java.util.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/keycloak")
public class KeycloakUserController {
    
    private final Logger logger = LoggerFactory.getLogger(KeycloakUserController.class);

    private final KeycloakService keycloakService;

    public KeycloakUserController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @Operation(summary = "Search on keycloak for specific users", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value = "/users", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public List<RestUserRepresentation> searchUsers(@ParameterObject SearchKeycloackUserRequest request) {
        logger.debug("REST request to get users by filters: {}", request);
        Map<String, String> map = (null != request) ? request.getParams() : new HashMap<>();
        return this.keycloakService.searchUsers(map).stream().map(RestUserRepresentation::new).collect(Collectors.toList());
    }

    @Operation(summary = "Search on keycloak for specific user having provided username", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the username")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value= "/users/{username}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<RestUserRepresentation> getUser(@PathVariable String username) {
        logger.debug("REST request to get user by username: {}", username);
        UserRepresentation user = this.keycloakService.getUser(username);
        if (null == user) {
            logger.warn("Requested user '{}' does not exist", username);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new RestUserRepresentation(user), HttpStatus.OK);
    }


}
