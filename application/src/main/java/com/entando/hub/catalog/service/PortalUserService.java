/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.service.model.UserRepresentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author E.Santoboni
 */
@Service
public class PortalUserService {
    
    @Autowired
    private KeycloakService keycloakService;
    
    @Autowired
    private OrganisationRepository organisationRepository;
    
    @Autowired
    private PortalUserRepository portalUserRepository;
    
    public List<UserRepresentation> getUsersByOrganisazion(String orgName) {
        Organisation org = this.organisationRepository.findByName(orgName);
        if (null == org) {
            return null;
        }
        Set<PortalUser> users = org.getPortalUsers();
        if (null == users) {
            return new ArrayList<>();
        }
        return users.stream()
                .filter(u -> null != this.keycloakService.getUser(u.getUsername()))
                .map(u -> this.keycloakService.getUser(u.getUsername()))
                .collect(Collectors.toList());
    }
    
}
