package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.config.ApplicationConstants;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.OrganisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;
    private final OrganisationService organisationService;
    public CatalogController(CatalogService catalogService, OrganisationService organisationService) {

        this.catalogService = catalogService;
        this.organisationService = organisationService;
    }

    @Operation(summary = "Get all the catalogues", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(value = "/", produces = {"application/json"})
    public List<Catalog> getCatalogues() {
        logger.debug("REST request to get Catalogues");
        return catalogService.getCatalogues();
    }

    @Operation(summary = "Get the Catalog by id", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(value = "/{catalogId}", produces = {"application/json"})
    public ResponseEntity<Optional<Catalog>> getCatalog(@PathVariable String catalogId) {
        logger.debug("REST request to get Catalog by id");
        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);

        if (catalog.isPresent()){
            return new ResponseEntity<>(catalog, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new catalog", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/", produces = {"application/json"})
    public ResponseEntity<Catalog> createCatalog(@RequestBody CatalogNoId catalog) {
        logger.debug("REST request to create Catalog: {}", catalog.toString());

        Optional<Organisation> organisation = organisationService.getOrganisation(catalog.getOrganisationId());
        logger.debug("organisation details: {} ", organisation.toString());

        if (organisation.isPresent()) {
            Optional<Catalog> orgCatalog = catalogService.getCatalogByOrganisationId(catalog.getOrganisationId());
            if(orgCatalog.isPresent()) {
                logger.debug("catalog already exists, details: {} ", orgCatalog.toString());
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
            Catalog entity = catalogService.createCatalog(catalog.createEntity(Optional.empty()));
            return new ResponseEntity<>(entity, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @Operation(summary = "Update a catalog", description = "Protected api, only eh-admin can access it. You have to provide the catalogId identifying the catalog")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/{catalogId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Catalog> updateCatalog(@PathVariable String catalogId, @RequestBody CatalogNoId catalog) {
        logger.debug("REST request to update Catalog {}: {}", catalogId, catalog.toString());
        Optional<Catalog> orgCatalog = catalogService.getCatalogById(catalogId);
        if (orgCatalog.isPresent()) {
            Catalog entity = catalogService.createCatalog(catalog.createEntity(Optional.of(catalogId)));
            return new ResponseEntity<>(entity, HttpStatus.OK);
        } else {
            logger.warn("Catalog '{}' does not exist", catalogId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a Catalog by Id", description = "Protected api, only eh-admin can access it. You have to provide the catalogId identifying the catalog")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{catalogId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Optional<Catalog>> deleteCatalog(@PathVariable String catalogId) {
        logger.debug("REST request to delete catalog {}", catalogId);

        Optional<Catalog> catalog = catalogService.getCatalogById(catalogId);
        if (catalog.isPresent()) {
            catalogService.deleteCatalog(catalogId);
            return new ResponseEntity<>(catalog, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Data
    public static class CatalogNoId {
        protected final Long organisationId;
        @Schema(example = "Entando Catalog")
        protected String name;

        public CatalogNoId(Long organisationId,String name) {
            this.organisationId = organisationId;
            this.name = name;
        }

        public Catalog createEntity(Optional<String> id) {
            Catalog entity = new Catalog();
            entity.setOrganisationId(this.organisationId);
            entity.setName(this.name);
            id.map(Long::valueOf).ifPresent(entity::setId);
            return entity;
        }
    }




}
