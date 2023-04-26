package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.dto.OrganisationDto;
import com.entando.hub.catalog.service.OrganisationService;
import com.entando.hub.catalog.service.mapper.OrganizationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;

@RestController
@RequestMapping("/api/organisation")
public class OrganisationController {
    
    private final Logger logger = LoggerFactory.getLogger(OrganisationController.class);
    private final OrganizationMapper organizationMapper;
    private final OrganisationService organisationService;

    public OrganisationController(OrganizationMapper organizationMapper, OrganisationService organisationService) {
        this.organizationMapper = organizationMapper;
        this.organisationService = organisationService;
    }

    @Operation(summary = "Get all the organisations", description = "Public api, no authentication required.")
    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<OrganisationDto> getOrganisations() {
        logger.debug("REST request to get organisations");
        return organisationService.getOrganisations().stream().map(OrganisationDto::new).collect(Collectors.toList());
    }

    @Operation(summary = "Get the organisation details", description = "Public api, no authentication required. You have to provide the organisationId")
    @GetMapping(value = "/{organisationId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<OrganisationDto> getOrganisation(@PathVariable Long organisationId) {
        logger.debug("REST request to get organisation by id: {}", organisationId);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (organisationOptional.isPresent()) {
            return ResponseEntity.ok(new OrganisationDto(organisationOptional.get()));
        } else {
            logger.warn("Requested organisation '{}' does not exist", organisationId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new organisation", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<OrganisationDto> createOrganisation(@RequestBody OrganisationDto organisation) {
        logger.debug("REST request to create new organisation: {}", organisation);
        Organisation entity = organisationService.createOrganisation(organizationMapper.toEntity(organisation), organisation);
        return new ResponseEntity<>(new OrganisationDto(entity), HttpStatus.CREATED);
    }


    @Operation(summary = "Update an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId identifying the organisation")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/{organisationId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<OrganisationDto> updateOrganisation(@PathVariable Long organisationId, @RequestBody OrganisationDto organisation) {
        logger.debug("REST request to update organisation {}: {}", organisationId, organisation);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (!organisationOptional.isPresent()) {
            logger.warn("Requested organisation '{}' does not exist", organisationId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            Organisation entity = organizationMapper.toEntity(organisation);
            entity.setId(organisationId);
            entity = organisationService.createOrganisation(entity, organisation);
            return new ResponseEntity<>(new OrganisationDto(entity), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId identifying the organisation")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{organisationId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<OrganisationDto> deleteOrganisation(@PathVariable Long organisationId) {
        logger.debug("REST request to delete organisation {}", organisationId);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (!organisationOptional.isPresent()) {
            logger.warn("Requested organisation '{}' does not exist", organisationId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            organisationService.deleteOrganisation(organisationId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }


}
