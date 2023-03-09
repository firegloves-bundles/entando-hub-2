package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;
import com.entando.hub.catalog.rest.validation.BundleGroupValidator;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.ForbiddenException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;
import static com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.PUBLISHED;

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
    private BundleGroupValidator bundleGroupValidator;

    public BundleGroupVersionController(BundleGroupVersionService bundleGroupVersionService, BundleGroupService bundleGroupService, CategoryService categoryService, SecurityHelperService securityHelperService, BundleGroupValidator bundleGroupValidator) {
    	this.bundleGroupVersionService = bundleGroupVersionService;
    	this.bundleGroupService = bundleGroupService;
    	this.categoryService = categoryService;
    	this.securityHelperService = securityHelperService;
        this.bundleGroupValidator = bundleGroupValidator;
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

    @Operation(summary = "Get all the private bundle group versions in the hub for the selected catalog, provides filter functionality", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You can provide the catalogId, the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value = "catalog/{catalogId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getPrivateBundleGroupsAndFilterThem(@PathVariable Long catalogId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
        logger.debug("REST request to get bundle group versions by catalog Id: {}, categoryIds {}, statuses {}", catalogId, categoryIds, statuses);

        if (!this.securityHelperService.isAdmin() && !this.securityHelperService.userCanAccessTheCatalog(catalogId)){
            throw new ForbiddenException(String.format("Only %s users can get bundle groups for any catalog, the other ones can get bundle groups only for their catalog", ADMIN));
        }

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
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
            com.entando.hub.catalog.persistence.entity.BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.of(bundleGroupVersionId), bundleGroupVersionOptional.get().getBundleGroup()), bundleGroupVersionView);
            return new ResponseEntity<>(new BundleGroupVersion(saved), HttpStatus.OK);
        }
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub filtered by bundleGroupId and statuses", description = "Public api, no authentication required. You can provide the bundleGroupId, the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping(value = "/versions/{bundleGroupId}", produces = {"application/json"})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getBundleGroupVersions(@PathVariable Long bundleGroupId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] statuses) {
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
        String[] statusFilterValues = statuses;
        PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> pagedContent = null;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
            pagedContent = bundleGroupVersionService.getBundleGroupVersions(sanitizedPageNum, pageSize, statuses, bundleGroupOptional.get());
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
    public ResponseEntity<BundleGroupVersionView> getBundleGroupVersion(@PathVariable String bundleGroupVersionId, @RequestParam(required = false) Long catalogId) {
        boolean isUserAuthenticated = securityHelperService.isUserAuthenticated();

        // If not Authenticated that request a private catalog
        if (null!=catalogId && Boolean.FALSE.equals(isUserAuthenticated))  {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (bundleGroupVersionOptional.isPresent()) {
            com.entando.hub.catalog.persistence.entity.BundleGroupVersion version = bundleGroupVersionOptional.get();
            BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(version);
            Boolean isBundleGroupPublicCatalog = version.getBundleGroup().getPublicCatalog();
            // If Authenticated -> return OK after validations
            if (Boolean.TRUE.equals(isUserAuthenticated)) {
                bundleGroupValidator.validateBundleGroupVersionPrivateCatalogRequest(catalogId, bundleGroupVersionId);
                return new ResponseEntity<>(bundleGroupVersionView, HttpStatus.OK);
            }
            // Not Authenticated
            else {
                // If not Authenticated with e public bundle group and status published -> return OK
                if (version.getStatus().equals(PUBLISHED)
                        && Boolean.TRUE.equals(isBundleGroupPublicCatalog)){
                    return new ResponseEntity<>(bundleGroupVersionView, HttpStatus.OK);
                }
                // If not Authenticated and bundle group not public -> return 404
                // If not Authenticated and bundle group is public and not published -> return 404
                else {
                    throw new NotFoundException("Not Found");
                }
            }
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
    @Accessors(chain = true)
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

        public BundleGroupVersionView() {
        }

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
            if (entity.getBundleGroup() != null) {
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

    @ExceptionHandler({ NotFoundException.class, AccessDeniedException.class, IllegalArgumentException.class, ConflictException.class })
    public ResponseEntity<String> handleException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
        } else if (exception instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof  ConflictException){
            status = HttpStatus.CONFLICT;
        }
        return ResponseEntity.status(status).body(String.format("{\"message\": \"%s\"}", exception.getMessage()));
    }
}
