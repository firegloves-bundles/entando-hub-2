package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.service.model.UserRepresentation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.entando.hub.catalog.rest.model.PortalUserResponseView;

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
        if (result && Objects.nonNull(portalUser.getId())) {
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

    /**
     * Gets a portal user by username, returns null if user not found.
     * @param username
     * @return a response view with portal user details.
     */
	public PortalUserResponseView getUserByUsername(String username) {
		PortalUser portalUser = null;
		PortalUserResponseView portalUserResponseView = null;
		if (username != null) {
			portalUser = this.portalUserRepository.findByUsername(username);
			if (Objects.isNull(portalUser)) {
				logger.warn("user '{}' does not exists", username);
				return null;
			}
			portalUserResponseView = PortalUserToPortalUserResponseView(portalUser);
		}
		return portalUserResponseView;
	}

	/**
	 * Fetches values from PortalUser entity and set in PortalUserResponseView.
	 * @param portalUser
	 * @return an object of PortalUserResponseView
	 */
	private PortalUserResponseView PortalUserToPortalUserResponseView(PortalUser portalUser) {
		PortalUserResponseView portalUserResponseView = new PortalUserResponseView();

		portalUserResponseView.setId(portalUser.getId());
		portalUserResponseView.setEmail(portalUser.getEmail());
		portalUserResponseView.setUsername(portalUser.getUsername());

		if(CollectionUtils.isNotEmpty(portalUser.getOrganisations())) {
			portalUserResponseView.setOrganisationIds(portalUser.getOrganisations().stream()
	                .map(Organisation::getId).collect(Collectors.toSet()));
		}

		return portalUserResponseView;
	}
}
