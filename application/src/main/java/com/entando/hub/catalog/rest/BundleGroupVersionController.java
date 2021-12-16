package com.entando.hub.catalog.rest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.security.SecurityHelperService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

/*
 * Controller for Bundle group version operations
 * 
 */
@RestController
@RequestMapping("/api/bundlegroupversions")
public class BundleGroupVersionController {
	
    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);
    
    private final BundleGroupVersionService bundleGroupVersionService;
    
    private final BundleGroupService bundleGroupService;
    
    private final CategoryService categoryService;
    
    private final SecurityHelperService securityHelperService;

    public BundleGroupVersionController(BundleGroupVersionService bundleGroupVersionService, BundleGroupService bundleGroupService, CategoryService categoryService, SecurityHelperService securityHelperService) {
    	this.bundleGroupVersionService = bundleGroupVersionService;
    	this.bundleGroupService = bundleGroupService;
    	this.categoryService = categoryService;
    	this.securityHelperService = securityHelperService;
    }
    

	@Operation(summary = "Create a new bundleGroupVersion", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @PostMapping("/")
    public ResponseEntity<BundleGroupVersion> createBundleGroup(@RequestBody BundleGroupVersionView bundleGroupVersionView) {
        logger.debug("REST request to create BundleGroupVersion: {}", bundleGroupVersionView);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupVersionView.getBundleGroupId().toString());
        if (bundleGroupOptional.isPresent()) {
	        com.entando.hub.catalog.persistence.entity.BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroupOptional.get()), bundleGroupVersionView);
	        return new ResponseEntity<>(new BundleGroupVersion(saved), HttpStatus.CREATED);
        }else {
        	logger.warn("Requested bundleGroupVersion '{}' does not exists", bundleGroupVersionView.getBundleGroupId().toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);        
        }
    }
	
	
	//PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @CrossOrigin
    @GetMapping("/filtered")
    public PagedContent<BundleGroupVersion, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses) {
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        String[] categoryIdFilterValues = categoryIds;
        if (categoryIdFilterValues == null) {
            categoryIdFilterValues = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        String[] statusFilterValues = statuses;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("REST request to get BundleGroupsversions by organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Page<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupsPage = bundleGroupVersionService.getBundleGroupVersions(sanitizedPageNum, pageSize, Optional.ofNullable(organisationId), categoryIdFilterValues, statuses);
        PagedContent<BundleGroupVersion, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> pagedContent = new PagedContent<>(bundleGroupsPage.getContent().stream().map(BundleGroupVersion::new).sorted(Comparator.comparing(BundleGroupVersion::getName,String::compareToIgnoreCase)).collect(Collectors.toList()), bundleGroupsPage);
        return pagedContent;
    }

    @Operation(summary = "Update a bundleGroupVersion", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupVersionId identifying the bundleGroupVersion")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @PostMapping("/{bundleGroupVersionId}")
    public ResponseEntity<BundleGroupVersion> updateBundleGroupVersion(@PathVariable String bundleGroupVersionId, @RequestBody BundleGroupVersionView bundleGroupVersionView) {
        logger.debug("REST request to update BundleGroupVersion with id {}: {}", bundleGroupVersionId, bundleGroupVersionView);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent()) {
            logger.warn("BundleGroupVersion '{}' does not exists", bundleGroupVersionId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            //if the user is not ADMIN
            if (!securityHelperService.hasRoles(Set.of(ADMIN))) {
                //I'm going to check the organisation
                com.entando.hub.catalog.persistence.entity.BundleGroupVersion bundleGroupVersionEntity = bundleGroupVersionOptional.get();

                //must exist and the user mat be in it
                if (bundleGroupVersionEntity.getBundleGroup().getOrganisation() == null || !securityHelperService.userIsInTheOrganisation(bundleGroupVersionEntity.getBundleGroup().getOrganisation().getId())) {
                    logger.warn("Only {} users can update bundle groups for any organisation, the other ones can update bundle groups only for their organisation", ADMIN);
                    return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
                }
            }
            com.entando.hub.catalog.persistence.entity.BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.of(bundleGroupVersionId),bundleGroupVersionOptional.get().getBundleGroup()), bundleGroupVersionView);
            return new ResponseEntity<>(new BundleGroupVersion(saved), HttpStatus.OK);
        }
    }
    

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class BundleGroupVersion extends BundleGroupVersionView {
    	
        private final String bundleGroupVersionId;
        
        public BundleGroupVersion(com.entando.hub.catalog.persistence.entity.BundleGroupVersion entity) {
            super(entity);
            this.bundleGroupVersionId = entity.getId().toString();
        }
    }
    
	@Data
    public static class BundleGroupVersionView {
		
	    protected String bundleGroupId;
	    protected String description;
	    protected String descriptionImage;
	    protected String documentationUrl;
	    protected String bundleGroupUrl;
	    protected String version;
	    protected com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status status;
	    protected Long organisationId;
        protected String organisationName;
        protected String name;
        protected LocalDateTime lastUpdate;
        protected List<String> categories;
        protected List<String> children;
	    
	    public BundleGroupVersionView(String bundleGroupId, String description, String descriptionImage, String version) {
            this.bundleGroupId = bundleGroupId;
            this.description = description;
            this.descriptionImage = descriptionImage;
            this.version = version;
         }


        public BundleGroupVersionView(com.entando.hub.catalog.persistence.entity.BundleGroupVersion entity) {
       	 	this.description = entity.getDescription();
       	 	this.descriptionImage = entity.getDescriptionImage();
            this.status = entity.getStatus();
            this.documentationUrl = entity.getDocumentationUrl();
            this.bundleGroupUrl = entity.getBundleGroupUrl();
            this.version = entity.getVersion();
            if (entity.getBundleGroup()!= null) {
            	this.bundleGroupId = entity.getBundleGroup().getId().toString();
            }
            if (entity.getBundleGroup().getOrganisation() != null) {
	            this.organisationId = entity.getBundleGroup().getOrganisation().getId();
	            this.organisationName = entity.getBundleGroup().getOrganisation().getName();
            }
            this.name = entity.getBundleGroup().getName();
            this.lastUpdate = entity.getLastUpdated();
            if (entity.getBundleGroup().getCategories() != null) {
                this.categories = entity.getBundleGroup().getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
            }
            if (entity.getBundleGroup().getBundles() != null) {
                this.children = entity.getBundleGroup().getBundles().stream().map((children) -> children.getId().toString()).collect(Collectors.toList());
            }

       }

        public com.entando.hub.catalog.persistence.entity.BundleGroupVersion createEntity(Optional<String> id, com.entando.hub.catalog.persistence.entity.BundleGroup bundleGroup) {
            com.entando.hub.catalog.persistence.entity.BundleGroupVersion bundleBroupVersion = new com.entando.hub.catalog.persistence.entity.BundleGroupVersion();
            bundleBroupVersion.setDescription(this.getDescription());
            bundleBroupVersion.setDescriptionImage(this.getDescriptionImage());
            bundleBroupVersion.setDocumentationUrl(this.getDocumentationUrl());
            bundleBroupVersion.setBundleGroupUrl(this.getBundleGroupUrl());
            bundleBroupVersion.setStatus(this.getStatus());
            bundleBroupVersion.setVersion(this.getVersion());
            bundleBroupVersion.setBundleGroup(bundleGroup);
            id.map(Long::valueOf).ifPresent(bundleBroupVersion::setId);
            return bundleBroupVersion;
        }
        
        
    }

}
