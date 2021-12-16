package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroup;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroupNoId;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.security.SecurityHelperService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;

@RestController
@RequestMapping("/api/bundlegroup")
public class BundleGroupRestController {
	
	private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupService bundleGroupService;
    private final CategoryService categoryService;
    private final SecurityHelperService securityHelperService;

    public BundleGroupRestController(BundleGroupService bundleGroupService, CategoryService categoryService, SecurityHelperService securityHelperService) {
        this.bundleGroupService = bundleGroupService;
        this.categoryService = categoryService;
        this.securityHelperService = securityHelperService;
    }

    @Operation(summary = "Create a new bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @PostMapping("/")
    public ResponseEntity<BundleGroup> createBundleGroup(@RequestBody BundleGroupNoId bundleGroup) {
        logger.debug("REST request to create BundleGroup: {}", bundleGroup);
        //if not admin the organisationid of the bundle must be the same of the user
        if (securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroup.getOrganisationId())) {
            logger.warn("Only {} users can create bundle groups for any organisation, the other ones can create bundle groups only for their organisation", ADMIN);
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.empty()), bundleGroup);
        return new ResponseEntity<>(new BundleGroup(saved), HttpStatus.CREATED);
    }
    
    @Data
    public static class BundleGroupView {
        protected final String name;
        protected List<String> children;
        protected String organisationId;
        protected String organisationName;
        protected List<String> categories;
        protected List<String> version;

        public BundleGroupView(String name) {
            this.name = name;
        }


        public BundleGroupView(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
             this.name = entity.getName();
            if (entity.getOrganisation() != null) {
                this.organisationId = entity.getOrganisation().getId().toString();
                this.organisationName = entity.getOrganisation().getName();
            }
            //todo one single iteration
            if (entity.getBundles() != null) {
                this.children = entity.getBundles().stream().map((children) -> children.getId().toString()).collect(Collectors.toList());
            }
            if (entity.getCategories() != null) {
                this.categories = entity.getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
            }
        }

       
        public com.entando.hub.catalog.persistence.entity.BundleGroup createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.BundleGroup ret = new com.entando.hub.catalog.persistence.entity.BundleGroup();
              ret.setName(this.getName());
              if (this.organisationId != null) {
                Organisation organisation = new Organisation();
                organisation.setId(Long.parseLong(this.organisationId));
                organisation.setName(this.organisationName);
                ret.setOrganisation(organisation);
            }
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }
    }
    
    
    
    
}
