package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;
import com.entando.hub.catalog.service.*;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controller for Bundle Group Version operations
 *
 */
@RestController
@RequestMapping("/api/bundlegroupversions")
public class BundleGroupVersionController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupVersionController.class);

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
    @PostMapping(value = "/", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupVersion> createBundleGroupVersion(@RequestBody BundleGroupVersionView bundleGroupVersionView) {
        logger.debug("REST request to create BundleGroupVersion: {}", bundleGroupVersionView);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(Long.parseLong(bundleGroupVersionView.getBundleGroupId()));
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
        	logger.warn("Requested bundleGroupVersion '{}' does not exist", bundleGroupVersionView.getBundleGroupId().toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

	//PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub, provides filter functionality", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping(value = "/filtered", produces = {"application/json"})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) Long organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
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
        return bundleGroupVersionService.searchBundleGroupVersions(sanitizedPageNum, pageSize, organisationId, categoryIdFilterValues, statuses, searchText);
    }

    @Operation(summary = "Get all the private bundle group versions in the hub for the selected catalog, provides filter functionality", description = "Public api, no authentication required. You can provide the catalogId, the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping(value = "catalog/{catalogId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getPrivateBundleGroupsAndFilterThem(@PathVariable Long catalogId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
        logger.debug("REST request to get bundle group versions by catalog Id: {}, categoryIds {}, statuses {}", catalogId, categoryIds, statuses);
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        if (categoryIds == null) {
            categoryIds = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        if (statuses == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("Catalog Id: {}, categoryIds {}, statuses {}", catalogId, categoryIds, statuses);
        return bundleGroupVersionService.searchPrivateBundleGroupVersions(sanitizedPageNum, pageSize, catalogId, categoryIds, statuses, searchText);
    }

    @Operation(summary = "Update a Bundle Group Version", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupVersionId identifying the bundleGroupVersion")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleGroupVersionId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupVersion> updateBundleGroupVersion(@PathVariable String bundleGroupVersionId, @RequestBody BundleGroupVersionView bundleGroupVersionView) {
        logger.debug("REST request to update BundleGroupVersion with id {}, request object: {}", bundleGroupVersionId, bundleGroupVersionView);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent()) {
            logger.warn("BundleGroupVersion '{}' does not exist", bundleGroupVersionId);
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
    @GetMapping(value = "/versions/{bundleGroupId}",produces = {"application/json"})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getBundleGroupVersions(@PathVariable Long bundleGroupId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] statuses) {
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
            // TODO check the impact on the FE if we return a non null object
            logger.warn("Requested bundleGroup '{}' does not exist", bundleGroupId);
            return pagedContent;
        }
    }

    @Operation(summary = "Delete a Bundle Group Version  by id", description = "Protected api, only eh-admin and eh-manager can access it. A Bundle Group Version can be deleted only if it is in DELETE_REQ status, you have to provide the bundlegroupVersionId")
    @RolesAllowed({ADMIN, MANAGER})
    @DeleteMapping(value = "/{bundleGroupVersionId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @Transactional
    public ResponseEntity<BundleGroupVersionView> deleteBundleGroupVersion(@PathVariable String bundleGroupVersionId) {
        logger.debug("REST request to delete BundleGroupVersion by id: {}", bundleGroupVersionId);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent() || !bundleGroupVersionOptional.get().getStatus().equals(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.DELETE_REQ)) {
            bundleGroupVersionOptional.ifPresentOrElse(
                    bundleGroupVersion -> logger.warn("Requested BundleGroupVersion '{}' is not in DELETE_REQ status: {}", bundleGroupVersionId, bundleGroupVersion.getStatus()),
                    () -> logger.warn("Requested bundleGroupVersion '{}' does not exist", bundleGroupVersionId)
            );
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
        	bundleGroupVersionService.deleteBundleGroupVersion(bundleGroupVersionOptional);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

	// PUBLIC
	@Operation(summary = "Get the BundleGroupVersion details by id", description = "Public api, no authentication required. You have to provide the bundleGroupVersionId")
	@GetMapping(value = "/{bundleGroupVersionId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
	public ResponseEntity<BundleGroupVersionView> getBundleGroupVersion(@PathVariable String bundleGroupVersionId) {
		logger.debug("REST request to get BundleGroupVersion by Id: {}", bundleGroupVersionId);
		Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
		if (bundleGroupVersionOptional.isPresent()) {
		    com.entando.hub.catalog.persistence.entity.BundleGroupVersion version = bundleGroupVersionOptional.get();
		    //Prevent this view unless the user is authenticated or the version is published
		    if (securityHelperService.isUserAuthenticated() || version.getStatus().equals(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.PUBLISHED)) {
                BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(version);
                return new ResponseEntity<>(bundleGroupVersionView, HttpStatus.OK);
            }
            logger.warn("Requested bundleGroupVersion '{}' exists but is protected", bundleGroupVersionOptional);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			logger.warn("Requested bundleGroupVersion '{}' does not exist", bundleGroupVersionOptional);
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

        @Schema(example = "a brief description")
	    protected String description;

        @Schema(example = "data:image/png;base64,base64code")
	    @ToString.Exclude
	    protected String descriptionImage;

        @Schema(example = "https://github.com/organization/sample-bundle#read-me")
	    protected String documentationUrl;

        @Schema(example = "1.0.0")
	    protected String version;
	    protected com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status status;
	    protected Long organisationId;

        @Schema(example = "Entando")
        protected String organisationName;

        @Schema(example = "simple bundle name")
        protected String name;
        protected LocalDateTime lastUpdate;
        protected List<String> categories;
        protected List<Long> children;
        protected String bundleGroupVersionId;
        protected List<BundleNoId> bundles;
        protected Boolean displayContactUrl;

        @Schema(example = "https://yoursite.com/contact-us")
        protected String contactUrl;

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
            	this.children = entity.getBundles().stream().map(Bundle::getId).collect(Collectors.toList());
            }
            this.displayContactUrl = entity.getDisplayContactUrl();
            this.contactUrl = entity.getContactUrl();
       }

        public com.entando.hub.catalog.persistence.entity.BundleGroupVersion createEntity(Optional<String> id, com.entando.hub.catalog.persistence.entity.BundleGroup bundleGroup) {
            com.entando.hub.catalog.persistence.entity.BundleGroupVersion bundleGroupVersion = new com.entando.hub.catalog.persistence.entity.BundleGroupVersion();
            bundleGroupVersion.setDescription(this.getDescription());
            bundleGroupVersion.setDescriptionImage(this.getDescriptionImage());
            bundleGroupVersion.setDocumentationUrl(this.getDocumentationUrl());
            bundleGroupVersion.setStatus(this.getStatus());
            bundleGroupVersion.setVersion(this.getVersion());
            bundleGroupVersion.setBundleGroup(bundleGroup);
            bundleGroupVersion.setDisplayContactUrl(this.getDisplayContactUrl());
            bundleGroupVersion.setContactUrl(this.getContactUrl());
            id.map(Long::valueOf).ifPresent(bundleGroupVersion::setId);
            return bundleGroupVersion;
        }
    }

}
