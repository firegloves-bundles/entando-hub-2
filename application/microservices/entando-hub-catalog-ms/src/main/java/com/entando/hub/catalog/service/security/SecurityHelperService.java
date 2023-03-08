package com.entando.hub.catalog.service.security;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;

import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.PortalUser;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityHelperService {
    private final PortalUserRepository portalUserRepository;
    private final CatalogRepository catalogRepository;

    public SecurityHelperService(PortalUserRepository portalUserRepository, CatalogRepository catalogRepository) {
        this.portalUserRepository = portalUserRepository;
        this.catalogRepository = catalogRepository;
    }

    public Boolean isUserAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken));
    }

    public Boolean userIsInTheOrganisation(Long organisationId) {
        String preferredUsername = ((KeycloakPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername();
        PortalUser portalUser = portalUserRepository.findByUsername(preferredUsername);
        return portalUser != null && portalUser.getOrganisations() != null && portalUser.getOrganisations().stream().anyMatch(organisation -> organisation.getId().equals(organisationId));
    }

    public boolean userCanAccessTheCatalog(Long catalogId) {
        String preferredUsername = ((KeycloakPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername();
        PortalUser portalUser = portalUserRepository.findByUsername(preferredUsername);
        Optional<Catalog> catalog = catalogRepository.findById(catalogId);

        if (!catalog.isPresent()) {
            return false;
        } else {
            Long organisationId = catalog.get().getOrganisation().getId();
            return portalUser != null && portalUser.getOrganisations() != null && portalUser.getOrganisations().stream().anyMatch(organisation -> organisation.getId().equals(organisationId));
        }

    }

    public Set<String> getUserRoles(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).map(this::getAuthorityFromRole).collect(Collectors.toSet());
    }

    private String getRoleFromAuthority(String authority) {
        return authority.substring(authority.indexOf("_")+1);
    }

    private String getAuthorityFromRole(String role){
        return "ROLE_"+role;
    }

    public Boolean hasRoles(Set<String> roles){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).map(this::getRoleFromAuthority).anyMatch(roles::contains);
    }

    //TRUE if user is not admin AND doesn't belong to the organisation
    public Boolean userIsNotAdminAndDoesntBelongToOrg(Long organisationId) {
        Boolean isAdmin = hasRoles(Set.of(ADMIN));
        if (isAdmin) {
            return false;
        }
        return !userIsInTheOrganisation(organisationId);

    }

    public String getContextAuthenticationUsername() {
        return ((KeycloakPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getKeycloakSecurityContext()
                .getToken()
                .getPreferredUsername();
    }

    public boolean isAdmin() {
        return this.hasRoles(Set.of(ADMIN));
    }
}
