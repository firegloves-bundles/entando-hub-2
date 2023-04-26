package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.rest.dto.RestUserRepresentationDto;
import com.entando.hub.catalog.rest.model.PortalUserResponseView;
import com.entando.hub.catalog.rest.model.UserOrganisationRequest;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/users")
public class PortalUserController {

    private final PortalUserService portalUserService;
    private final SecurityHelperService securityHelperService;

    public PortalUserController(PortalUserService portalUserService, SecurityHelperService securityHelperService) {
        this.portalUserService = portalUserService;
        this.securityHelperService = securityHelperService;
    }
    private final Logger logger = LoggerFactory.getLogger(PortalUserController.class);


    @Operation(summary = "Get all the portal users", description = "Protected api, only eh-admin can access it. You can provide the organisationId to filter the results")
    @RolesAllowed({ADMIN})
    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<List<RestUserRepresentationDto>> getUsers(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get users by organisation id: {}", organisationId);
        List<UserRepresentation> users;
        users = portalUserService.getUsersByOrganisation(organisationId);
        if (null == users) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users.stream().map(RestUserRepresentationDto::new).collect(Collectors.toList()), HttpStatus.OK);
    }
    @Operation(summary = "Add a Keycloak user to an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/{organisationId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<Map<String, Boolean>> addUserToOrganisation(@PathVariable String organisationId, @RequestBody UserOrganisationRequest request) {
        logger.debug("REST request to add user to organisation id: {}", organisationId);
        boolean result = this.portalUserService.addUserToOrganization(request.getUsername(), organisationId);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Remove a Keycloak user from an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId and the username")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{organisationId}/user/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<Map<String, Boolean>> deleteUserFromOrganisation(@PathVariable String organisationId, @PathVariable String username) {
        logger.debug("REST request to delete user from organisation id: {}", organisationId);
        boolean result = this.portalUserService.removeUserFromOrganization(username, organisationId);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Portal User", description = "Protected api, only eh-admin can access it. You have to provide the username")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable String username) {
        logger.debug("REST request to delete user: {}", username);
        boolean result = this.portalUserService.removeUser(username);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

	/**
	 * An API to get a portal user by username.
	 * @return a response view with portal user details.
	 */
    @Operation(summary = "Get a user details by username", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ ADMIN, AUTHOR, MANAGER })
    @GetMapping(value = "/details", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
	public ResponseEntity<PortalUserResponseView> getPortalUserByUsername() {
        logger.debug("REST request to get a user by username, retrieving username from token");
        String username = this.securityHelperService.getContextAuthenticationUsername();
		logger.debug("REST request to get a user by username : {}", username);
        PortalUserResponseView portalUser = portalUserService.getUserByUsername(username);
		if (portalUser == null) {
            return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(portalUser);
	}
}
