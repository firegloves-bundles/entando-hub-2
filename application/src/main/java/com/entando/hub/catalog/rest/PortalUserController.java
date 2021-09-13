/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.rest.KeycloakUserController.RestUserRepresentation;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class PortalUserController {
    
    @Autowired
    private PortalUserService portalUserService;
    
    @CrossOrigin
    @GetMapping("/{organisationName}")
    public ResponseEntity<List<RestUserRepresentation>> getUsersByOrganisation(@PathVariable String organisationName) {
        List<UserRepresentation> users = this.portalUserService.getUsersByOrganisazion(organisationName);
        if (null == users) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users.stream().map(RestUserRepresentation::new).collect(Collectors.toList()), HttpStatus.OK);
    }
    
}
