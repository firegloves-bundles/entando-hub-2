package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.rest.KeycloakUserController.RestUserRepresentation;
import com.entando.hub.catalog.rest.model.UserOrganisationRequest;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class PortalUserController {
    
    private final Logger logger = LoggerFactory.getLogger(PortalUserController.class);

    @Autowired
    private PortalUserService portalUserService;
    
    @CrossOrigin
    @GetMapping("/{organisationName}")
    public ResponseEntity<List<RestUserRepresentation>> getUsersByOrganisation(@PathVariable String organisationName) {
        logger.debug("REST request to get users by organisation name: {}", organisationName);
        List<UserRepresentation> users = this.portalUserService.getUsersByOrganisazion(organisationName);
        if (null == users) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users.stream().map(RestUserRepresentation::new).collect(Collectors.toList()), HttpStatus.OK);
    }
    
    @CrossOrigin
    @PostMapping("/{organisationName}")
    public ResponseEntity<Map<String, Boolean>> addUserByOrganisation(@PathVariable String organisationName, @RequestBody UserOrganisationRequest request) {
        logger.debug("REST request to add user to organisation name: {}", organisationName);
        if (!organisationName.equals(request.getOrganisationName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "organisationName invalid");
        }
        boolean result = this.portalUserService.addUserToOrganization(request.getUsername(), request.getOrganisationName());
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }
    
    @CrossOrigin
    @DeleteMapping("/{organisationName}/user/{username}")
    public ResponseEntity<Map<String, Boolean>> deleteUserFromOrganisation(@PathVariable String organisationName, @PathVariable String username) {
        logger.debug("REST request to delete user from organisation name: {}", organisationName);
        boolean result = this.portalUserService.removeUserFromOrganization(username, organisationName);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }
    
}
