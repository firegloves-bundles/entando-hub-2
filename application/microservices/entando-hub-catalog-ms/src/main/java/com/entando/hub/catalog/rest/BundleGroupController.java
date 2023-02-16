package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.persistence.ManyToOne;
import javax.transaction.Transactional;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.OrganisationService;
import com.entando.hub.catalog.service.exception.ConflictException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.security.SecurityHelperService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@RestController
@RequestMapping("/api/bundlegroups")
public class BundleGroupController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupService bundleGroupService;
    private final SecurityHelperService securityHelperService;
    private final BundleGroupVersionService bundleGroupVersionService;
    private final CatalogService catalogService;
    private final OrganisationService organisationService;

    public BundleGroupController(BundleGroupService bundleGroupService, SecurityHelperService securityHelperService, BundleGroupVersionService bundleGroupVersionService, CatalogService catalogService, OrganisationService organisationService) {
        this.bundleGroupService = bundleGroupService;
        this.securityHelperService = securityHelperService;
        this.bundleGroupVersionService = bundleGroupVersionService;
        this.catalogService = catalogService;
        this.organisationService = organisationService;
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle groups in the hub", description = "Public api, no authentication required. You can provide the organisationId.")
    @GetMapping(value = "/", produces = {"application/json"})
    public List<BundleGroupDTO> getBundleGroupsByOrganisationId(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get BundleGroups by organisation Id: {}", organisationId);
        return bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId)).stream().map(BundleGroupDTO::new).collect(Collectors.toList());
    }

    //PUBLIC
    @Operation(summary = "Get the bundleGroup details", description = "Public api, no authentication required. You have to provide the bundleGroupId")
    @GetMapping(value = "/{bundleGroupId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupDTO> getBundleGroup(@PathVariable Long bundleGroupId) {
        logger.debug("REST request to get BundleGroup by Id: {}", bundleGroupId);
        Optional<BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
            return new ResponseEntity<>(bundleGroupOptional.map(BundleGroupDTO::new).get(), HttpStatus.OK);
        } else {
            logger.warn("Requested bundleGroup '{}' does not exist", bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupDTO> createBundleGroup(@RequestBody BundleGroupNoId bundleGroup) throws NotFoundException {
        logger.debug("REST request to create BundleGroup: {}", bundleGroup);
        this.validateRequest(bundleGroup);
        BundleGroup entity = bundleGroup.createEntity(Optional.empty());
        BundleGroup saved = bundleGroupService.createBundleGroup(entity, bundleGroup);
        BundleGroupDTO bundleGroupDTO = new BundleGroupDTO(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(bundleGroupDTO);
    }

    protected void validateRequest(BundleGroupNoId bundleGroup) throws NotFoundException {
        if (!organisationService.existsById(bundleGroup.getOrganisationId())) {
            throw new NotFoundException(String.format("Organisation with ID %d not found", bundleGroup.getOrganisationId()));
        }

        if (securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroup.getOrganisationId())) {
            throw new AccessDeniedException(String.format("Only %s users can create bundle groups for any organisation, the other ones can create bundle groups only for their organisation", ADMIN));
        }

        if (bundleGroup.getIsPublic() == null || bundleGroup.getIsPublic()) {
            bundleGroup.setIsPublic(true);
            bundleGroup.setCatalogId(null);
        } else {
            if (bundleGroup.getCatalogId()==null) {
                throw new IllegalArgumentException("Catalog ID is required for non-public bundle groups");
            } else if (!catalogService.existCatalogById(bundleGroup.catalogId)) {
                throw new NotFoundException(String.format("Catalog with ID %d not found", bundleGroup.getCatalogId()));
            }
        }
    }

    @Operation(summary = "Update a bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupId identifying the bundleGroup")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleGroupId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupDTO> updateBundleGroup(@PathVariable Long bundleGroupId, @RequestBody BundleGroupNoId bundleGroup) throws NotFoundException {
        logger.debug("REST request to update BundleGroup with id {}: {}", bundleGroupId, bundleGroup);
        this.validateRequest(bundleGroup);
        this.validateExistingBundleGroup(bundleGroupId);
        BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.of(bundleGroupId)), bundleGroup);
        return new ResponseEntity<>(new BundleGroupDTO(saved), HttpStatus.OK);
    }

    protected void validateExistingBundleGroup(Long bundleGroupId) throws NotFoundException {
        Optional<BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (!bundleGroupOptional.isPresent()) {
            throw new NotFoundException(String.format("BundleGroup %s does not exist", bundleGroupId));
        } else if (!bundleGroupVersionService.isBundleGroupEditable(bundleGroupOptional.get())) {
            throw new ConflictException(String.format("BundleGroup %s is not editable", bundleGroupId));
        }
    }

    //This API is currently not in use. After data model changes for Bundle Group Version we do not allow users to directly delete Bundle Group from UI.
    //Although it can be accessed from Swagger or Postman
    //Should we keep this api or should delete ?
    @Operation(summary = "Delete a bundleGroup", description = "Protected api, only eh-admin and eh-manager can access it. A bundleGroup can be deleted only if it is in DELETE_REQ status  You have to provide the bundlegroupId identifying the category")
    @RolesAllowed({ADMIN, MANAGER})
    @DeleteMapping(value = "/{bundleGroupId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @Transactional
    public ResponseEntity<CategoryController.Category> deleteBundleGroup(@PathVariable Long bundleGroupId) {
        logger.debug("REST request to delete bundleGroup {}", bundleGroupId);
        Optional<BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (!bundleGroupOptional.isPresent()) {
            bundleGroupOptional.ifPresentOrElse(
                    bundleGroup -> logger.warn("Requested bundleGroup '{}' is not present", bundleGroupId),
                    () -> logger.warn("Requested bundleGroup '{}' does not exist", bundleGroupId)
            );
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            bundleGroupService.deleteBundleGroup(bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class BundleGroupDTO extends BundleGroupNoId {
        private final String bundleGroupId;

        public BundleGroupDTO(String bundleGroupId, String name, Long organizationId, Boolean isPublic, Long catalogId) {
            super(name, organizationId, isPublic, catalogId);
            this.bundleGroupId = bundleGroupId;
        }

        public BundleGroupDTO(BundleGroup entity) {
            super(entity);
            this.bundleGroupId = entity.getId().toString();
        }
    }

    @Data
    public static class BundleGroupNoId {

        @Schema(example = "bundle group name")
        protected final String name;
        protected Long organisationId;
        private Boolean isPublic;
        private Long catalogId;

        @Schema(example = "Entando")
        protected String organisationName;
        protected List<String> categories;
        protected BundleGroupVersionView versionDetails;

        public BundleGroupNoId(String name ,Long organisationId, Boolean isPublic, Long catalogId) {
            this.name = name;
            this.organisationId = organisationId;
            this.isPublic = isPublic;
            this.catalogId = catalogId;
        }

        public BundleGroupNoId(BundleGroup entity) {
            this.name = entity.getName();
            this.isPublic = entity.getIsPublic();
            this.catalogId = entity.getCatalogId();

            if (entity.getOrganisation() != null) {
                this.organisationId = entity.getOrganisation().getId();
                this.organisationName = entity.getOrganisation().getName();
            }
            if (entity.getCategories() != null) {
                this.categories = entity.getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
            }
        }

        public BundleGroup createEntity(Optional<Long> id) {
            BundleGroup entity = new BundleGroup();
            entity.setName(this.getName());
            entity.setIsPublic(this.isPublic);
            entity.setCatalogId(this.catalogId);
            if (this.organisationId != null) {
                Organisation organisation = new Organisation();
                organisation.setId(this.organisationId);
                organisation.setName(this.organisationName);
                entity.setOrganisation(organisation);
            }
            id.ifPresent(entity::setId);
            return entity;
        }
    }

    @ExceptionHandler({ AccessDeniedException.class, IllegalArgumentException.class, NotFoundException.class, ConflictException.class })
    public ResponseEntity<String> handleException(Exception exception) {
        HttpStatus status = null;
        if (exception instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
        } else if (exception instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof  ConflictException){
            status = HttpStatus.CONFLICT;
        }
        return ResponseEntity.status(status).body(String.format("{\"message\": \"%s\"}", exception.getMessage()));
    }
}
