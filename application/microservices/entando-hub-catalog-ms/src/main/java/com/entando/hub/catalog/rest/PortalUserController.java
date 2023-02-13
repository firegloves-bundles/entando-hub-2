package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.rest.domain.RestUserRepresentation;
import com.entando.hub.catalog.rest.model.PortalUserResponseView;
import com.entando.hub.catalog.rest.model.UserOrganisationRequest;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    public PortalUserController(PortalUserService portalUserService) {
        this.portalUserService = portalUserService;
    }
    private final Logger logger = LoggerFactory.getLogger(PortalUserController.class);


    @Operation(summary = "Get all the portal users", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You can provide the organisationId to filter the results")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value = "/", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<RestUserRepresentation>> getUsers(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get users by organisation id: {}", organisationId);
        List<UserRepresentation> users;
        users = portalUserService.getUsersByOrganisation(organisationId);
        if (null == users) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users.stream().map(RestUserRepresentation::new).collect(Collectors.toList()), HttpStatus.OK);
    }
    @Operation(summary = "Add a Keycloak user to an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/{organisationId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Map<String, Boolean>> addUserToOrganisation(@PathVariable String organisationId, @RequestBody UserOrganisationRequest request) {
        logger.debug("REST request to add user to organisation id: {}", organisationId);

/*
        if (!organisationId.equals(request.getOrganisationId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrganisationId is not valid");
        }
*/
        boolean result = this.portalUserService.addUserToOrganization(request.getUsername(), organisationId);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Remove a Keycloak user from an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId and the username")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{organisationId}/user/{username}", produces = "application/json")
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Map<String, Boolean>> deleteUserFromOrganisation(@PathVariable String organisationId, @PathVariable String username) {
        logger.debug("REST request to delete user from organisation id: {}", organisationId);
        boolean result = this.portalUserService.removeUserFromOrganization(username, organisationId);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Portal User", description = "Protected api, only eh-admin can access it. You have to provide the username")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{username}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable String username) {
        logger.debug("REST request to delete user: {}", username);
        boolean result = this.portalUserService.removeUser(username);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

	/**
	 * An API to get a portal user by username.
	 * @param username
	 * @return a response view with portal user details.
	 */
    @Operation(summary = "Get a user by username", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ ADMIN, AUTHOR, MANAGER })
	@GetMapping(value = "/{username}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
	public ResponseEntity<PortalUserResponseView> getPortalUserByUsername(@PathVariable("username") String username) {
		logger.debug("REST request to get a user by username: {}", username);
		PortalUserResponseView portalUser;
		portalUser = portalUserService.getUserByUsername(username);
		if (null == portalUser) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<PortalUserResponseView>(portalUser, HttpStatus.OK);
	}
}
