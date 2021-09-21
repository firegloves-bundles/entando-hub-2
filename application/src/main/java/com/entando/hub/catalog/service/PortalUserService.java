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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author E.Santoboni
 */
@Service
public class PortalUserService {
    
    private static final Logger logger = LoggerFactory.getLogger(PortalUserService.class);
    
    @Autowired
    private KeycloakService keycloakService;
    
    @Autowired
    private OrganisationRepository organisationRepository;
    
    @Autowired
    private PortalUserRepository portalUserRepository;
    
    public List<UserRepresentation> getUsersByOrganisazion(String orgName) {
        Organisation org = this.organisationRepository.findByName(orgName);
        if (null == org) {
            logger.warn("Requested organisation '{}' does not exists", orgName);
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
    
    public boolean addUserToOrganization(String username, String orgName) {
        Organisation org = this.organisationRepository.findByName(orgName);
        if (null == org) {
            logger.warn("Organisation '" + orgName + "' does not exists");
            return false;
        }
        UserRepresentation user = keycloakService.getUser(username);
        if (null == user) {
            logger.warn("User '" + username + "' does not exists");
            return false;
        }
        PortalUser portalUser = this.portalUserRepository.findByUsername(username);
        if (null == portalUser) {
            portalUser = new PortalUser();
            portalUser.setUsername(username);
        }
        portalUser.setEmail(user.getEmail());
        long isPresent = portalUser.getOrganisations().stream().filter(p -> p.getName().equals(orgName)).count();
        if (isPresent == 0) {
            portalUser.getOrganisations().add(org);
        } else {
            return false;
        }
        this.portalUserRepository.save(portalUser);
        return true;
    }
    
    public boolean removeUserFromOrganization(String username, String orgName) {
        Organisation org = this.organisationRepository.findByName(orgName);
        if (null == org) {
            logger.warn("Organisation '" + orgName + "' does not exists");
            return false;
        }
        UserRepresentation user = keycloakService.getUser(username);
        if (null == user) {
            logger.warn("User '" + username + "' does not exists");
            return false;
        }
        PortalUser portalUser = this.portalUserRepository.findByUsername(username);
        if (null == portalUser) {
            logger.info("Organisation '{}' doesn't include User '" + username + "'", orgName, username);
            return false;
        }
        portalUser.setEmail(user.getEmail());
        boolean result = portalUser.getOrganisations().removeIf(o -> o.getName().equals(orgName));
        if (result) {
            this.portalUserRepository.save(portalUser);
        }
        return result;
    }
    
}
