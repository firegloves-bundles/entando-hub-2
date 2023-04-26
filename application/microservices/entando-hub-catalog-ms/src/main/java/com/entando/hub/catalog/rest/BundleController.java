package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.rest.dto.BundleEntityDto;
import com.entando.hub.catalog.rest.validation.BundleGroupValidator;
import com.entando.hub.catalog.service.BundleService;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.mapper.inclusion.BundleStandardMapper;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/bundles/")
public class BundleController {

    private final Logger logger = LoggerFactory.getLogger(BundleController.class);

    final private BundleService bundleService;
    private final BundleGroupValidator bundleGroupValidator;
    private final SecurityHelperService securityHelperService;

    private final BundleStandardMapper bundleStandardMapper;

    public BundleController(BundleService bundleService, BundleGroupValidator bundleGroupValidator, SecurityHelperService securityHelperService, BundleStandardMapper bundleStandardMapper) {
        this.bundleService = bundleService;
        this.bundleGroupValidator = bundleGroupValidator;
        this.securityHelperService = securityHelperService;
        this.bundleStandardMapper = bundleStandardMapper;
    }

    @Operation(summary = "Get all the bundles of a bundle group version", description = "Public api, no authentication required. You can provide a bundleGroupVersionId to get all the bundles in that")
    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<BundleDto>> getBundles(@RequestParam(required = false) String bundleGroupVersionId, @RequestParam(required = false) Long catalogId) {
        // If not Authenticated that request a private catalog
        boolean isUserAuthenticated = securityHelperService.isUserAuthenticated();
        if (null != catalogId && Boolean.FALSE.equals(isUserAuthenticated)) {
            return (new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
        if (Boolean.TRUE.equals(isUserAuthenticated)) {
            if (null != bundleGroupVersionId) {
                bundleGroupValidator.validateBundleGroupVersionPrivateCatalogRequest(catalogId, bundleGroupVersionId);
            } else {
                bundleGroupValidator.validateBundlePrivateCatalogRequest(catalogId);
            }
        } else {
            if (null != bundleGroupVersionId) {
                bundleGroupValidator.validateBundleGroupVersionPrivateCatalogRequest(catalogId, bundleGroupVersionId);
            }
        }
        List<BundleDto> bundles = bundleStandardMapper.toDto(bundleService.getBundles(bundleGroupVersionId,catalogId));
        return new ResponseEntity<>(bundles, HttpStatus.OK);
    }

    @Operation(summary = "Get the bundle details", description = "Public api, no authentication required. You have to provide the bundleId")
    @GetMapping(value = "/{bundleId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<BundleDto> getBundle(@PathVariable() String bundleId) {
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (bundleOptional.isPresent()) {
            return new ResponseEntity<>(bundleStandardMapper.toDto(bundleOptional.get()), HttpStatus.OK);
        } else {
            logger.warn("Requested bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BundleDto> createBundle(@RequestBody BundleDto bundleDto) {
        logger.debug("REST request to create new Bundle: {}", bundleDto);


        com.entando.hub.catalog.persistence.entity.Bundle eBundle = bundleStandardMapper.toEntity(bundleDto);
        com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(eBundle);
        return new ResponseEntity<>(bundleStandardMapper.toDto(entity), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleId identifying the bundle")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.NOT_FOUND_RESPONSE_CODE, description = SwaggerConstants.NOT_FOUND_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public ResponseEntity<BundleDto> updateBundle(@PathVariable String bundleId, @RequestBody BundleDto bundleDto) {
        Optional<Bundle> bundleOptional = bundleService.getBundle(bundleId);

        if (!bundleOptional.isPresent()) {
            logger.warn("Bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            bundleDto.setBundleId(bundleId);
            Bundle eBundle = bundleStandardMapper.toEntity(bundleDto);
            Bundle entity = bundleService.createBundle(eBundle);
            return new ResponseEntity<>(bundleStandardMapper.toDto(entity), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete a bundle", description = "Protected api, only eh-admin can access it. You have to provide the bundleId")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{bundleId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.FORBIDDEN_RESPONSE_CODE, description = SwaggerConstants.FORBIDDEN_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.UNAUTHORIZED_RESPONSE_CODE, description = SwaggerConstants.UNAUTHORIZED_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    @Transactional
    public ResponseEntity<BundleEntityDto> deleteBundle(@PathVariable String bundleId) {
        Optional<Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (!bundleOptional.isPresent()) {
            logger.warn("Bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } else {
            bundleService.deleteBundle(bundleOptional.get());
            return new ResponseEntity<>(null, HttpStatus.OK);
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
