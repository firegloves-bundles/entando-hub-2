package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.rest.dto.BundleDtoIn;
import com.entando.hub.catalog.service.BundleService;
import com.entando.hub.catalog.service.mapper.BundleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/bundles/")
public class BundleController {
    
    private final Logger logger = LoggerFactory.getLogger(BundleController.class);

    final private BundleService bundleService;
    final private BundleMapper bundleMapper;

    public BundleController(BundleService bundleService, BundleMapper bundleMapper) {
        this.bundleService = bundleService;
        this.bundleMapper = bundleMapper;
    }

    @Operation(summary = "Get all the bundles of a bundle group version", description = "Public api, no authentication required. You can provide a bundleGroupVersionId to get all the bundles in that")
    @GetMapping(value = "/", produces = {"application/json"})
    public List<Bundle> getBundles(@RequestParam(required = false) String bundleGroupVersionId) {
        logger.debug("REST request to get Bundles by bundle group version id: {}", bundleGroupVersionId);
        return bundleService.getBundles(Optional.ofNullable(bundleGroupVersionId)).stream().map(Bundle::new).collect(Collectors.toList());
    }

    @Operation(summary = "Get the bundle details", description = "Public api, no authentication required. You have to provide the bundleId")
    @GetMapping(value = "/{bundleId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Bundle> getBundle(@PathVariable() String bundleId) {
        logger.debug("REST request to get Bundle by id: {}", bundleId);
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (bundleOptional.isPresent()) {
            return new ResponseEntity<>(bundleOptional.map(Bundle::new).get(), HttpStatus.OK);
        } else {
            logger.warn("Requested bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/", produces = {"application/json"})
    public ResponseEntity<Bundle> createBundle(@RequestBody BundleDtoIn bundleDto) {
        logger.debug("REST request to create new Bundle: {}", bundleDto);
        
//        Optional<String> opt =  Objects.nonNull(bundle.getBundleId())
//   			 ?  Optional.of(bundle.getBundleId())
//   					 : Optional.empty();
        com.entando.hub.catalog.persistence.entity.Bundle eBundle = bundleMapper.toEntity(bundleDto);
        com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(eBundle);
        return new ResponseEntity<>(new Bundle(entity), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleId identifying the bundle")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Bundle> updateBundle(@PathVariable String bundleId, @RequestBody BundleDtoIn bundleDto) {
        logger.debug("REST request to update a Bundle with id {}: {}", bundleId, bundleDto);
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);

        if (!bundleOptional.isPresent()) {
        	logger.warn("Bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            com.entando.hub.catalog.persistence.entity.Bundle eBundle = bundleMapper.toEntity(bundleDto);
            com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(eBundle);
            return new ResponseEntity<>(new Bundle(entity), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete a bundle", description = "Protected api, only eh-admin can access it. You have to provide the bundlegId")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{bundleId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @Transactional
    public ResponseEntity<Bundle> deleteBundle(@PathVariable String bundleId) {
        logger.debug("REST request to delete bundle {}", bundleId);
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (!bundleOptional.isPresent()) {
        	logger.warn("Bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } else {
        	bundleService.deleteBundle(bundleOptional.get());
        	return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class Bundle extends BundleDtoIn {
        @Schema(example = "bundle identifier")
        private final String bundleId;
        public Bundle(String bundleId, String name, String description, String gitRepoAddress, String gitSrcRepoAddress, List<String> dependencies, List<String> bundleGroups, String descriptorVersion) {
            super(bundleId, name, description, gitRepoAddress, gitSrcRepoAddress, dependencies, bundleGroups, descriptorVersion);
            this.bundleId = bundleId;
        }

        public Bundle(com.entando.hub.catalog.persistence.entity.Bundle entity) {
            super(entity);
            this.bundleId = entity.getId().toString();
        }
    }

}
