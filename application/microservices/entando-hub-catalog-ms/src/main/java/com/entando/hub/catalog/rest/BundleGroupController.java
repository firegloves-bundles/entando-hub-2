package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.rest.dto.CategoryDto;
import com.entando.hub.catalog.rest.dto.BundleGroupDto;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.mapper.BundleGroupMapper;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/bundlegroups")
public class BundleGroupController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupService bundleGroupService;
    private final SecurityHelperService securityHelperService;
    private final BundleGroupVersionService bundleGroupVersionService;
    private final BundleGroupMapper bundleGroupMapper;

    public BundleGroupController(BundleGroupService bundleGroupService, SecurityHelperService securityHelperService, BundleGroupVersionService bundleGroupVersionService, BundleGroupMapper bundleGroupMapper) {
        this.bundleGroupService = bundleGroupService;
        this.securityHelperService = securityHelperService;
        this.bundleGroupVersionService = bundleGroupVersionService;
        this.bundleGroupMapper = bundleGroupMapper;
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle groups in the hub", description = "Public api, no authentication required. You can provide the organisationId.")
    @GetMapping(value = "/", produces = {"application/json"})
    public List<BundleGroupDto> getBundleGroupsByOrganisationId(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get BundleGroups by organisation Id: {}", organisationId);
        return bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId))
          .stream()
          .map(bundleGroupMapper::toDto)
          .collect(Collectors.toList());
    }

    //PUBLIC
    @Operation(summary = "Get the bundleGroup details", description = "Public api, no authentication required. You have to provide the bundleGroupId")
    @GetMapping(value = "/{bundleGroupId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupDto> getBundleGroup(@PathVariable String bundleGroupId) {
        logger.debug("REST request to get BundleGroupDto by Id: {}", bundleGroupId);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
            BundleGroupDto dto = bundleGroupMapper.toDto(bundleGroupOptional.orElse(null));

            return new ResponseEntity<>(dto, HttpStatus.OK);
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
    public ResponseEntity<BundleGroupDto> createBundleGroup(@RequestBody BundleGroupDto bundleGroupDto) {
        logger.debug("REST request to create BundleGroupDto: {}", bundleGroupDto);
        //if not admin the organisationid of the bundle must be the same of the user
        if (securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroupDto.getOrganisationId())) {
            logger.warn("Only {} users can create bundle groups for any organisation, the other ones can create bundle groups only for their organisation", ADMIN);
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        // com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.empty()), bundleGroup);
        com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroupMapper.toEntity(bundleGroupDto), bundleGroupDto);
        BundleGroupDto dto = bundleGroupMapper.toDto(saved);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupId identifying the bundleGroup")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleGroupId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupDto> updateBundleGroup(@PathVariable String bundleGroupId, @RequestBody BundleGroupDto bundleGroup) {
        logger.debug("REST request to update BundleGroupDto with id {}: {}", bundleGroupId, bundleGroup);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);

		if (!bundleGroupOptional.isPresent()) {
			logger.warn("BundleGroupDto '{}' does not exist", bundleGroupId);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else if (!bundleGroupVersionService.isBundleGroupEditable(bundleGroupOptional.get())) {
			logger.warn("BundleGroupDto '{}' is not editable", bundleGroupId);
			return new ResponseEntity<>(null, HttpStatus.CONFLICT);
		} else {
            //if the user is not ADMIN
            if (!securityHelperService.hasRoles(Set.of(ADMIN))) {
                //I'm going to check the organisation
                BundleGroup bundleGroupEntity = bundleGroupOptional.get();

                //must exist and the user mat be in it
                if (bundleGroupEntity.getOrganisation() == null || !securityHelperService.userIsInTheOrganisation(bundleGroupEntity.getOrganisation().getId())) {
                    logger.warn("Only {} users can update bundle groups for any organisation, the other ones can update bundle groups only for their organisation", ADMIN);
                    return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
                }
            }
            // com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.of(bundleGroupId)), bundleGroup);
            bundleGroup.setBundleGroupId(bundleGroupId);
            BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroupMapper.toEntity(bundleGroup), bundleGroup);
        BundleGroupDto dto = bundleGroupMapper.toDto(saved);
            return new ResponseEntity<>(dto, HttpStatus.OK);
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
    public ResponseEntity<CategoryDto> deleteBundleGroup(@PathVariable String bundleGroupId) {
        logger.debug("REST request to delete bundleGroup {}", bundleGroupId);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
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

}
