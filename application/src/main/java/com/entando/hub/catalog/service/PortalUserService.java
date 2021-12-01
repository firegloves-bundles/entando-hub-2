package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.service.model.UserRepresentation;

import java.util.*;
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

    public List<UserRepresentation> getUsersByOrganisation(String orgId) {
        Collection<PortalUser> users;
        if (orgId != null) {
            Long orgIdLong = Long.valueOf(orgId);
            Optional<Organisation> org = this.organisationRepository.findById(orgIdLong);
            if (!org.isPresent()) {
                logger.warn("Organisation '{}' does not exists", orgIdLong);
                return null;
            }
            users = org.get().getPortalUsers();
            if (null == users) {
                return new ArrayList<>();
            }
        } else {
            users = this.portalUserRepository.findAll();
        }

        //TODO maybe this could be improved by caching
        return users.stream()
                .filter(u -> null != this.keycloakService.getUser(u.getUsername()))
                .map(u -> {
                    UserRepresentation userRepresentation = this.keycloakService.getUser(u.getUsername());
                    userRepresentation.setOrganisationIds(u.getOrganisations().stream().map(Organisation::getId).collect(Collectors.toSet()));
                    return userRepresentation;
                })
                .collect(Collectors.toList());
    }


    public boolean addUserToOrganization(String username, String orgId) {
        Long orgIdLong = Long.valueOf(orgId);
        Optional<Organisation> org = this.organisationRepository.findById(orgIdLong);
        if (!org.isPresent()) {
            logger.warn("Organisation '" + orgId + "' does not exists");
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
        long isPresent = portalUser.getOrganisations().stream().filter(p -> p.getId().equals(orgIdLong)).count();
        if (isPresent == 0) {
            portalUser.getOrganisations().add(org.get());
        } else {
            return false;
        }
        this.portalUserRepository.save(portalUser);
        return true;
    }

    public boolean removeUserFromOrganization(String username, String orgId) {
        Long orgIdLong = Long.valueOf(orgId);
        Optional<Organisation> org = this.organisationRepository.findById(orgIdLong);
        if (!org.isPresent()) {
            logger.warn("Organisation '" + orgIdLong + "' does not exists");
            return false;
        }
        UserRepresentation user = keycloakService.getUser(username);
        if (null == user) {
            logger.warn("User '" + username + "' does not exists");
            return false;
        }
        PortalUser portalUser = this.portalUserRepository.findByUsername(username);
        if (null == portalUser) {
            logger.info("Organisation '{}' doesn't include User '" + username + "'", orgId, username);
            return false;
        }
        boolean result = portalUser.getOrganisations().removeIf(o -> o.getId().equals(orgIdLong));
        if (result) {
			/** Delete the user if it is not associated with any organization */
			this.portalUserRepository.deleteById(portalUser.getId());
        }

        return true;
    }

    public boolean removeUser(String username) {
        PortalUser portalUser = this.portalUserRepository.findByUsername(username);
        if(portalUser==null) return false;
        this.portalUserRepository.delete(portalUser);
        return true;
    }
}
