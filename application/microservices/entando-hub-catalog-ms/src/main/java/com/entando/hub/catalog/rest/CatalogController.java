package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.dto.CatalogDto;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;
    private final SecurityHelperService securityHelperService;

    public CatalogController(CatalogService catalogService, SecurityHelperService securityHelperService) {
        this.catalogService = catalogService;
        this.securityHelperService = securityHelperService;
    }

    @Operation(summary = "Get all catalogs accessible by the current user", description = "Protected api, only eh-admin, eh-author and eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(value = "/", produces = {"application/json"})
    public ResponseEntity<List<CatalogDto>> getCatalogs() {
        logger.debug("REST request to get Catalogs");

        boolean userIsAdmin = this.securityHelperService.isAdmin();
        String username = this.securityHelperService.getContextAuthenticationUsername();

        List<CatalogDto> catalogsDTO = catalogService.getCatalogs(username, userIsAdmin)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(catalogsDTO, HttpStatus.OK);
    }

    @Operation(summary = "Get the Catalog by id", description = "Protected api, only eh-admin, eh-author and eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(value = "/{catalogId}", produces = {"application/json"})
    public ResponseEntity<CatalogDto> getCatalog(@PathVariable Long catalogId) {
        logger.debug("REST request to get Catalog by id");
        boolean userIsAdmin = this.securityHelperService.isAdmin();
        String username = this.securityHelperService.getContextAuthenticationUsername();

        try {
            Catalog response = catalogService.getCatalogById(username, catalogId, userIsAdmin);
            return ResponseEntity.ok(mapToDTO(response));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new catalog", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/{organisationId}", produces = {"application/json"})
    public ResponseEntity<CatalogDto> createCatalog(@PathVariable Long organisationId) {
        logger.debug("REST request to create Catalog for organisation: {}", organisationId);
        try {
            Catalog response = catalogService.createCatalog(organisationId);
            return ResponseEntity.ok(mapToDTO(response));
        } catch (ConflictException conflictException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException notFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a Catalog by Id", description = "Protected api, only eh-admin can access it. You have to provide the catalogId identifying the catalog")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{catalogId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CatalogDto> deleteCatalog(@PathVariable Long catalogId) {
        logger.debug("REST request to delete catalog {}", catalogId);
        try {
            Catalog response = this.catalogService.deleteCatalog(catalogId);
            return ResponseEntity.ok(mapToDTO(response));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    public CatalogDto mapToDTO(Catalog catalog) {
        return new CatalogDto(catalog.getId(), catalog.getOrganisation().getId(), catalog.getName());
    }

}
