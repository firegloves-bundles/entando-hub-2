package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.rest.dto.BundleGroupDto;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.OrganisationService;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.mapper.BundleGroupMapper;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/bundlegroups")
public class BundleGroupController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupService bundleGroupService;
    private final SecurityHelperService securityHelperService;
    private final BundleGroupVersionService bundleGroupVersionService;
    private final OrganisationService organisationService;
    private final BundleGroupMapper bundleGroupMapper;

    public BundleGroupController(BundleGroupService bundleGroupService, SecurityHelperService securityHelperService, BundleGroupVersionService bundleGroupVersionService, OrganisationService organisationService, BundleGroupMapper bundleGroupMapper) {
        this.bundleGroupService = bundleGroupService;
        this.securityHelperService = securityHelperService;
        this.bundleGroupVersionService = bundleGroupVersionService;
        this.organisationService = organisationService;
        this.bundleGroupMapper = bundleGroupMapper;
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle groups in the hub", description = "Public api, no authentication required. You can provide the organisationId.")
    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<BundleGroupDto>> getBundleGroupsByOrganisationId(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get BundleGroups by organisation Id: {}", organisationId);
        List<BundleGroupDto> bundleGroupList = bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId))
                .stream()
                .map(bundleGroupMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(bundleGroupList, HttpStatus.OK);
    }

    //PUBLIC
    @Operation(summary = "Get the bundleGroup details", description = "Public api, no authentication required. You have to provide the bundleGroupId")
    @GetMapping(value = "/{bundleGroupId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<BundleGroupDto> getBundleGroup(@PathVariable Long bundleGroupId) {
        logger.debug("REST request to get BundleGroup by Id: {}", bundleGroupId);
        return bundleGroupService.getBundleGroup(bundleGroupId)
                .map(bundleGroupMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<BundleGroupDto> createBundleGroup(@RequestBody BundleGroupDto bundleGroup) {
        logger.debug("REST request to create BundleGroup: {}", bundleGroup);
        this.validateRequest(bundleGroup);
        BundleGroup entity = bundleGroupMapper.toNewEntity(bundleGroup);
        BundleGroup saved = bundleGroupService.createBundleGroup(entity, bundleGroup);
        BundleGroupDto bundleGroupDto = bundleGroupMapper.toDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(bundleGroupDto);
    }

    protected void validateRequest(BundleGroupDto bundleGroup) {
        if (!organisationService.existsById(bundleGroup.getOrganisationId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Organisation with ID %d not found", bundleGroup.getOrganisationId()));
        }
        if (securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroup.getOrganisationId())) {
            throw new AccessDeniedException(String.format("Only %s users can create bundle groups for any organisation, the other ones can create bundle groups only for their organisation", ADMIN));
        }
        if(bundleGroup.getPublicCatalog() == null) {
             bundleGroup.setPublicCatalog(true);
        }
    }

    @Operation(summary = "Update a bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupId identifying the bundleGroup")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleGroupId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<BundleGroupDto> updateBundleGroup(@PathVariable Long bundleGroupId, @RequestBody BundleGroupDto bundleGroup) {
        logger.debug("REST request to update BundleGroup with id {}: {}", bundleGroupId, bundleGroup);
        this.validateRequest(bundleGroup);
        this.validateExistingBundleGroup(bundleGroupId);
        bundleGroup.setBundleGroupId(bundleGroupId.toString());
        BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroupMapper.toEntity(bundleGroup), bundleGroup);
        return new ResponseEntity<>(bundleGroupMapper.toDto(saved), HttpStatus.OK);
    }

    protected void validateExistingBundleGroup(Long bundleGroupId) {
        boolean isPresent = bundleGroupService.existsById(bundleGroupId);
        if (!isPresent) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("BundleGroup %s does not exist", bundleGroupId));
        } else {
            Optional<BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
            if (bundleGroupOptional.isPresent() && !bundleGroupVersionService.isBundleGroupEditable(bundleGroupOptional.get())) {
                throw new ConflictException(String.format("BundleGroup %s is not editable", bundleGroupId));
            }
        }
    }

    @Operation(summary = "Delete a bundleGroup", description = "Protected api, only eh-admin and eh-manager can access it. A bundleGroup can be deleted only if it is in DELETE_REQ status  You have to provide the bundlegroupId identifying the category")
    @RolesAllowed({ADMIN, MANAGER})
    @DeleteMapping(value = "/{bundleGroupId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.NO_CONTENT_RESPONSE_CODE, description = SwaggerConstants.NO_CONTENT_DESCRIPTION, content = @Content)
    @Transactional
    public ResponseEntity<Void> deleteBundleGroup(@PathVariable Long bundleGroupId) {
        logger.debug("REST request to delete bundleGroup {}", bundleGroupId);
        boolean bundleGroupExist = bundleGroupService.existsById(bundleGroupId);
        if (!bundleGroupExist) {
           logger.warn("Requested bundleGroup '{}' does not exist", bundleGroupId);
           return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            bundleGroupService.deleteBundleGroup(bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

}
