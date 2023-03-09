package com.entando.hub.catalog.rest.validation;

import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class BundleGroupValidator {

    private static final String CATALOG_NOT_FOUND_MSG = "Catalog not found";
    private static final String BUNDLE_GROUP_NOT_FOUND_MSG = "Bundle group not found";

    final BundleGroupService bundleGroupService;

    final BundleGroupVersionService bundleGroupVersionService;

    final SecurityHelperService securityHelperService;

    final
    CatalogService catalogService;
    private final PortalUserRepository portalUserRepository;
    private PortalUserService portalUserService;

    public BundleGroupValidator(BundleGroupService bundleGroupService, BundleGroupVersionService bundleGroupVersionService, SecurityHelperService securityHelperService, CatalogService catalogService,
                                PortalUserRepository portalUserRepository, PortalUserService portalUserService) {
        this.bundleGroupService = bundleGroupService;
        this.bundleGroupVersionService = bundleGroupVersionService;
        this.securityHelperService = securityHelperService;
        this.catalogService = catalogService;
        this.portalUserRepository = portalUserRepository;
        this.portalUserService = portalUserService;
    }

    public boolean validateBundlePrivateCatalogRequest(Long catalogId) {
        boolean isAdmin = securityHelperService.isAdmin();
        if (null != catalogId && Boolean.FALSE.equals(isAdmin)) {
                checkUserCatalog(catalogId);
            }
        return true;
    }

    public boolean validateBundleGroupVersionPrivateCatalogRequest(Long catalogId, String bundleGroupVersionId) {
        Optional<BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        BundleGroupVersion bundleGroupVersion;

        if (bundleGroupVersionOptional.isPresent()) {
            bundleGroupVersion = bundleGroupVersionOptional.get();
        } else {
            throw new NotFoundException(BUNDLE_GROUP_NOT_FOUND_MSG);
        }

        BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();

        if (null != catalogId) {
            if (!securityHelperService.isAdmin()) {
                checkUserCatalog(catalogId);
            }
            // verify that the requested bundle group belongs to the specified catalog (if no => return 404)
            if (!bundleGroup.getCatalogId().equals(catalogId)) {
                throw new NotFoundException(CATALOG_NOT_FOUND_MSG);
            }
        } else {
            if (!securityHelperService.isAdmin()) {
                //if the bundle group is not in public catalog checks the organization permissions
                if (!bundleGroup.getPublicCatalog()) {
                    Set<Organisation> userOrganizations = portalUserService.getUserOrganizations();
                    Organisation bundleGroupOrganisation = bundleGroup.getOrganisation();
                    if (!userOrganizations.contains(bundleGroupOrganisation)) {
                        throw new NotFoundException(BUNDLE_GROUP_NOT_FOUND_MSG);
                    }
                }
            }

        }

        return true;
    }


    //verify that the current user has access to the catalog (if no => return 404)
    private Catalog checkUserCatalog(Long catalogId) {
        Catalog catalog = catalogService.getCatalogById(catalogId);

        String username = securityHelperService.getContextAuthenticationUsername();

        // verify that the current user has access to the catalog (if no => return 404)
        boolean userIsAdmin = securityHelperService.isAdmin();
        List<Catalog> userCatalogs = catalogService.getCatalogs(username, userIsAdmin);
        if (!userCatalogs.contains(catalog)) {
            throw new NotFoundException(CATALOG_NOT_FOUND_MSG);
        }
        return catalog;
    }
}
