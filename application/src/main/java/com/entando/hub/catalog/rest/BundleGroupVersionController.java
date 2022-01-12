package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
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

/*
 * Controller for Bundle Group Version operations
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

	@Operation(summary = "Create a new Bundle Group Version", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping("/")
    public ResponseEntity<BundleGroupVersion> createBundleGroupVersion(@RequestBody BundleGroupVersionView bundleGroupVersionView) {
        logger.debug("REST request to create BundleGroupVersion: {}", bundleGroupVersionView);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupVersionView.getBundleGroupId().toString());
        if (bundleGroupOptional.isPresent()) {
        	logger.debug("BundleGroup is present with id: {}", bundleGroupOptional.get().getId());
            List<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersions = bundleGroupVersionService.getBundleGroupVersions(bundleGroupOptional.get(), bundleGroupVersionView.getVersion());
            if (CollectionUtils.isEmpty(bundleGroupVersions)) {
            	logger.info("Bundle group version list found with size: {}", bundleGroupVersions.size());
		        com.entando.hub.catalog.persistence.entity.BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroupOptional.get()), bundleGroupVersionView);
		        return new ResponseEntity<>(new BundleGroupVersion(saved), HttpStatus.CREATED);
            } else {
            	logger.warn("Bundle group version list found with size: {}", bundleGroupVersions.size());
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
        	logger.warn("Requested bundleGroupVersion '{}' does not exists", bundleGroupVersionView.getBundleGroupId().toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
	
	//PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub, provides filter functionality", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping("/filtered")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
    	logger.debug("REST request to get bundle group versions by organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        String[] categoryIdFilterValues = categoryIds;
        if (categoryIdFilterValues == null) {
            categoryIdFilterValues = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        String[] statusFilterValues = statuses;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("Organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
//        PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> pagedContent = bundleGroupVersionService.getBundleGroupVersions(sanitizedPageNum, pageSize, Optional.ofNullable(organisationId), categoryIdFilterValues, statuses, Optional.ofNullable(searchText));
        PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> pagedContent = bundleGroupVersionService.searchBundleGroupVersions(sanitizedPageNum, pageSize, Optional.ofNullable(organisationId), categoryIdFilterValues, statuses, Optional.ofNullable(searchText));
        return pagedContent;
    }

    @Operation(summary = "Update a Bundle Group Version", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupVersionId identifying the bundleGroupVersion")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping("/{bundleGroupVersionId}")
    public ResponseEntity<BundleGroupVersion> updateBundleGroupVersion(@PathVariable String bundleGroupVersionId, @RequestBody BundleGroupVersionView bundleGroupVersionView) {
        logger.debug("REST request to update BundleGroupVersion with id {}, request object: {}", bundleGroupVersionId, bundleGroupVersionView);
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
    
    //PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub filtered by bundleGroupId and statuses", description = "Public api, no authentication required. You can provide the bundleGroupId, the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping("/versions/{bundleGroupId}")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getBundleGroupVersions(@PathVariable String bundleGroupId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] statuses) {
    	logger.debug("REST request to get bundle group versions by bundleGroupId: {} and statuses {}", bundleGroupId, statuses);
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
        String[] statusFilterValues = statuses;
        PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> pagedContent = null;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
        	pagedContent = bundleGroupVersionService.getBundleGroupVersions(sanitizedPageNum, pageSize, statuses,bundleGroupOptional.get());
            return pagedContent;
        } else {
            logger.warn("Requested bundleGroup '{}' does not exists", bundleGroupId);
            return pagedContent;
        }
    }
    
    @Operation(summary = "Delete a Bundle Group Version  by id", description = "Protected api, only eh-admin and eh-manager can access it. A Bundle Group Version can be deleted only if it is in DELETE_REQ status, you have to provide the bundlegroupVersionId")
    @RolesAllowed({ADMIN, MANAGER})
    @DeleteMapping("/{bundleGroupVersionId}")
    @Transactional
    public ResponseEntity<BundleGroupVersionView> deleteBundleGroupVersion(@PathVariable String bundleGroupVersionId) {
        logger.debug("REST request to delete BundleGroupVersion by id: {}", bundleGroupVersionId);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent() || !bundleGroupVersionOptional.get().getStatus().equals(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.DELETE_REQ)) {
            bundleGroupVersionOptional.ifPresentOrElse(
                    bundleGroupVersion -> logger.warn("Requested BundleGroupVersion '{}' is not in DELETE_REQ status: {}", bundleGroupVersionId, bundleGroupVersion.getStatus()),
                    () -> logger.warn("Requested bundleGroupVersion '{}' does not exists", bundleGroupVersionId)
            );
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
        	bundleGroupVersionService.deleteBundleGroupVersion(bundleGroupVersionOptional);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }
    
	// PUBLIC
	@Operation(summary = "Get the BundleGroupVersion details by id", description = "Public api, no authentication required. You have to provide the bundleGroupVersionId")
	@GetMapping("/{bundleGroupVersionId}")
	public ResponseEntity<BundleGroupVersionView> getBundleGroupVersion(@PathVariable String bundleGroupVersionId) {
		logger.debug("REST request to get BundleGroupVersion by Id: {}", bundleGroupVersionId);
		Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
		if (bundleGroupVersionOptional.isPresent()) {
			BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupVersionOptional.get());
			return new ResponseEntity<>(bundleGroupVersionView, HttpStatus.OK);
		} else {
			logger.warn("Requested bundleGroupVersion '{}' does not exists", bundleGroupVersionOptional);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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
	    protected String version;
	    protected com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status status;
	    protected Long organisationId;
        protected String organisationName;
        protected String name;
        protected LocalDateTime lastUpdate;
        protected List<String> categories;
        protected List<String> children;
        protected String bundleGroupVersionId;
	    
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
            if (entity.getBundles() != null) {
                this.children = entity.getBundles().stream().map((children) -> children.getId().toString()).collect(Collectors.toList());
            }
       }

        public com.entando.hub.catalog.persistence.entity.BundleGroupVersion createEntity(Optional<String> id, com.entando.hub.catalog.persistence.entity.BundleGroup bundleGroup) {
            com.entando.hub.catalog.persistence.entity.BundleGroupVersion bundleBroupVersion = new com.entando.hub.catalog.persistence.entity.BundleGroupVersion();
            bundleBroupVersion.setDescription(this.getDescription());
            bundleBroupVersion.setDescriptionImage(this.getDescriptionImage());
            bundleBroupVersion.setDocumentationUrl(this.getDocumentationUrl());
            bundleBroupVersion.setStatus(this.getStatus());
            bundleBroupVersion.setVersion(this.getVersion());
            bundleBroupVersion.setBundleGroup(bundleGroup);
            id.map(Long::valueOf).ifPresent(bundleBroupVersion::setId);
            return bundleBroupVersion;
        }
    }

}
