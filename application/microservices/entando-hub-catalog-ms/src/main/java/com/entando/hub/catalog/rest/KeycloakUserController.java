package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.rest.dto.RestUserRepresentationDto;
import com.entando.hub.catalog.rest.model.SearchKeycloackUserRequest;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @GetMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public List<RestUserRepresentationDto> searchUsers(@ParameterObject SearchKeycloackUserRequest request) {
        logger.debug("REST request to get users by filters: {}", request);
        Map<String, String> map = (null != request) ? request.getParams() : new HashMap<>();
        return this.keycloakService.searchUsers(map).stream().map(RestUserRepresentationDto::new).collect(Collectors.toList());
    }

    @Operation(summary = "Search on keycloak for specific user having provided username", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the username")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value= "/users/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<RestUserRepresentationDto> getUser(@PathVariable String username) {
        logger.debug("REST request to get user by username: {}", username);
        UserRepresentation user = this.keycloakService.getUser(username);
        if (null == user) {
            logger.warn("Requested user '{}' does not exist", username);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new RestUserRepresentationDto(user), HttpStatus.OK);
    }


}
