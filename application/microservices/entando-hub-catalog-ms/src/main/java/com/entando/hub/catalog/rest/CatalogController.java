package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.dto.CatalogDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Operation(summary = "Get all the catalogs", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(value = "/", produces = {"application/json"})
    public ResponseEntity<List<CatalogDTO>> getCatalogs() {
        logger.debug("REST request to get Catalogs");
        List<CatalogDTO> catalogDTOList = catalogService.getCatalogs();
        return new ResponseEntity<>(catalogDTOList, HttpStatus.OK);
    }

    @Operation(summary = "Get the Catalog by id", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(value = "/{catalogId}", produces = {"application/json"})
    public ResponseEntity<CatalogDTO> getCatalog(@PathVariable Long catalogId) {
        logger.debug("REST request to get Catalog by id");
        return catalogService.getCatalogById(catalogId);
    }

    @Operation(summary = "Create a new catalog", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/{organisationId}", produces = {"application/json"})
    public ResponseEntity<CatalogDTO> createCatalog(@PathVariable Long organisationId) {
        logger.debug("REST request to create Catalog for organisation: {}", organisationId);
        return catalogService.createCatalog(organisationId);
    }

    @Operation(summary = "Delete a Catalog by Id", description = "Protected api, only eh-admin can access it. You have to provide the catalogId identifying the catalog")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{catalogId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CatalogDTO> deleteCatalog(@PathVariable Long catalogId) {
        logger.debug("REST request to delete catalog {}", catalogId);
        return this.catalogService.deleteCatalog(catalogId);
    }

}
