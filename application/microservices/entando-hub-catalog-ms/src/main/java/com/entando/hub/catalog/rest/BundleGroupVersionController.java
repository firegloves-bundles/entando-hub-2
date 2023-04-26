package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;
import com.entando.hub.catalog.rest.validation.BundleGroupValidator;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.mapper.BundleGroupVersionMapper;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final BundleGroupValidator bundleGroupValidator;

    private final BundleGroupVersionMapper bundleGroupVersionMapper;

    public BundleGroupVersionController(BundleGroupVersionService bundleGroupVersionService, BundleGroupService bundleGroupService, CategoryService categoryService, SecurityHelperService securityHelperService, BundleGroupValidator bundleGroupValidator, BundleGroupVersionMapper bundleGroupVersionMapper) {
    	this.bundleGroupVersionService = bundleGroupVersionService;
    	this.bundleGroupService = bundleGroupService;
    	this.categoryService = categoryService;
    	this.securityHelperService = securityHelperService;
        this.bundleGroupValidator = bundleGroupValidator;
        this.bundleGroupVersionMapper = bundleGroupVersionMapper;
    }

	@Operation(summary = "Create a new Bundle Group Version", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupVersionDto> createBundleGroupVersion(@RequestBody BundleGroupVersionDto bundleGroupVersionView) {
        logger.debug("REST request to create BundleGroupVersion: {}", bundleGroupVersionView);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(Long.parseLong(bundleGroupVersionView.getBundleGroupId()));

        if (bundleGroupOptional.isPresent()) {
        	logger.debug("BundleGroupDto is present with id: {}", bundleGroupOptional.get().getId());
            List<BundleGroupVersion> bundleGroupVersions = bundleGroupVersionService.getBundleGroupVersions(bundleGroupOptional.get(), bundleGroupVersionView.getVersion());
            if (CollectionUtils.isEmpty(bundleGroupVersions)) {
            	logger.info("Bundle group version list found with size: {}", bundleGroupVersions.size());

              final BundleGroup bundleGroup = bundleGroupOptional.get();
              BundleGroupVersion bundleGroupVersionEntity = bundleGroupVersionMapper.toEntity(bundleGroupVersionView, bundleGroup);

              bundleGroupVersionEntity.setId(null); // EHUB-296 impose expected null id
              BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionEntity, bundleGroupVersionView);
              BundleGroupVersionDto dto = bundleGroupVersionMapper.toDto(saved);
		        return new ResponseEntity<>(dto, HttpStatus.CREATED);
            } else {
            	logger.warn("Bundle group version list found with size: {}", bundleGroupVersions.size());
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
        	logger.warn(REQUESTED_BUNDLE_GROUP_VERSION_DOES_NOT_EXIST, bundleGroupVersionView.getBundleGroupId());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

	//PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub, provides filter functionality", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping(value = "/filtered", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) Long organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
    	logger.debug("REST request to get bundle group versions by organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        String[] categoryIdFilterValues = categoryIds;
        if (categoryIdFilterValues == null) {
            categoryIdFilterValues = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        String[] statusFilterValues = statuses;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("Organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        return bundleGroupVersionService.searchBundleGroupVersions(sanitizedPageNum, pageSize, organisationId, null, categoryIdFilterValues, statuses, searchText, true);
    }

    @Operation(summary = "Get all the private bundle group versions in the hub for the selected catalog, provides filter functionality", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You can provide the catalogId, the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value = "catalog/{catalogId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> getPrivateBundleGroupsAndFilterThem(@PathVariable Long catalogId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
        logger.debug("REST request to get bundle group versions by catalog Id: {}, categoryIds {}, statuses {}", catalogId, categoryIds, statuses);

        if (!this.securityHelperService.isAdmin() && !this.securityHelperService.userCanAccessTheCatalog(catalogId)){
            throw new AccessDeniedException(String.format("Only %s users can get bundle groups for any catalog, the other ones can get bundle groups only for their catalog", ADMIN));
        }

        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        if (categoryIds == null) {
            categoryIds = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        if (statuses == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("Catalog Id: {}, categoryIds {}, statuses {}", catalogId, categoryIds, statuses);
        return bundleGroupVersionService.searchBundleGroupVersions(sanitizedPageNum, pageSize, null, catalogId, categoryIds, statuses, searchText, null);
    }

    @Operation(summary = "Update a Bundle Group Version", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupVersionId identifying the bundleGroupVersion")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleGroupVersionId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupVersionDto> updateBundleGroupVersion(@PathVariable String bundleGroupVersionId, @RequestBody BundleGroupVersionDto bundleGroupVersionView) {
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent()) {
            logger.warn("BundleGroupVersionDto '{}' does not exist", bundleGroupVersionId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            //if the user is not ADMIN
            if (!securityHelperService.hasRoles(Set.of(ADMIN))) {
                //I'm going to check the organisation
                BundleGroupVersion bundleGroupVersionEntity = bundleGroupVersionOptional.get();

                //must exist and the user mat be in it
                if (bundleGroupVersionEntity.getBundleGroup().getOrganisation() == null || !securityHelperService.userIsInTheOrganisation(bundleGroupVersionEntity.getBundleGroup().getOrganisation().getId())) {
                    logger.warn("Only {} users can update bundle groups for any organisation, the other ones can update bundle groups only for their organisation", ADMIN);
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
            BundleGroup bundleGroup = bundleGroupVersionOptional.get().getBundleGroup();
            BundleGroupVersion entity = bundleGroupVersionMapper.toEntity(bundleGroupVersionView, bundleGroup);
            BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(entity, bundleGroupVersionView);
            BundleGroupVersionDto dto = bundleGroupVersionMapper.toDto(saved);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub filtered by bundleGroupId and statuses", description = "Public api, no authentication required. You can provide the bundleGroupId, the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping(value = "/versions/{bundleGroupId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> getBundleGroupVersions(@PathVariable Long bundleGroupId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] statuses) {
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
        String[] statusFilterValues = statuses;
        PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> pagedContent = null;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }
        Optional<BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
            pagedContent = bundleGroupVersionService.getBundleGroupVersions(sanitizedPageNum, pageSize, statuses, bundleGroupOptional.get());
        } else {
            // TODO check the impact on the FE if we return a non null object
            logger.warn("Requested bundleGroup '{}' does not exist", bundleGroupId);
        }
        return pagedContent;
    }

    @Operation(summary = "Delete a Bundle Group Version  by id", description = "Protected api, only eh-admin and eh-manager can access it. A Bundle Group Version can be deleted only if it is in DELETE_REQ status, you have to provide the bundlegroupVersionId")
    @RolesAllowed({ADMIN, MANAGER})
    @DeleteMapping(value = "/{bundleGroupVersionId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @Transactional
    public ResponseEntity<BundleGroupVersionDto> deleteBundleGroupVersion(@PathVariable String bundleGroupVersionId) {
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent() || !bundleGroupVersionOptional.get().getStatus().equals(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.DELETE_REQ)) {
            bundleGroupVersionOptional.ifPresentOrElse(
                    bundleGroupVersion -> logger.warn("Requested BundleGroupVersionDto '{}' is not in DELETE_REQ status: {}", bundleGroupVersionId, bundleGroupVersion.getStatus()),
                    () -> logger.warn(REQUESTED_BUNDLE_GROUP_VERSION_DOES_NOT_EXIST, bundleGroupVersionId)
            );
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            bundleGroupVersionService.deleteBundleGroupVersion(bundleGroupVersionOptional);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    // PUBLIC
    @Operation(summary = "Get the BundleGroupVersion details by id", description = "Public api, no authentication required. You have to provide the bundleGroupVersionId")
    @GetMapping(value = "/{bundleGroupVersionId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupVersionDto> getBundleGroupVersion(@PathVariable String bundleGroupVersionId, @RequestParam(required = false) Long catalogId) {
        boolean isUserAuthenticated = securityHelperService.isUserAuthenticated();

        // If not Authenticated that request a private catalog
        if (null!=catalogId && Boolean.FALSE.equals(isUserAuthenticated))  {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (bundleGroupVersionOptional.isPresent()) {
            com.entando.hub.catalog.persistence.entity.BundleGroupVersion version = bundleGroupVersionOptional.get();
            BundleGroupVersionDto bundleGroupVersionView = bundleGroupVersionMapper.toViewDto(version);
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
            logger.warn(REQUESTED_BUNDLE_GROUP_VERSION_DOES_NOT_EXIST, bundleGroupVersionOptional);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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


    public static final String REQUESTED_BUNDLE_GROUP_VERSION_DOES_NOT_EXIST = "Requested bundleGroupVersion '{}' does not exist";
}
