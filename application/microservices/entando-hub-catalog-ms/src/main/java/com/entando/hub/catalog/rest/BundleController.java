package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import com.entando.hub.catalog.persistence.entity.Bundle.DescriptorVersion;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.service.BundleService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@RestController
@RequestMapping("/api/bundles/")
public class BundleController {
    
    private final Logger logger = LoggerFactory.getLogger(BundleController.class);

    final private BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
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
    public ResponseEntity<Bundle> createBundle(@RequestBody BundleNoId bundle) {
        logger.debug("REST request to create new Bundle: {}", bundle);
        
        Optional<String> opt =  Objects.nonNull(bundle.getBundleId()) 
   			 ?  Optional.of(bundle.getBundleId())
   					 : Optional.empty();
        
        com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(bundle.createEntity(opt));
        return new ResponseEntity<>(new Bundle(entity), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleId identifying the bundle")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Bundle> updateBundle(@PathVariable String bundleId, @RequestBody BundleNoId bundle) {
        logger.debug("REST request to update a Bundle with id {}: {}", bundleId, bundle);
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (!bundleOptional.isPresent()) {
        	logger.warn("Bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(bundle.createEntity(Optional.of(bundleId)));
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
    public static class Bundle extends BundleNoId {
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

    @Data
    public static class BundleNoId {
        @Schema(example = "bundle identifier")
        protected final String bundleId;

        @Schema(example = "bundle-sample")
        protected final String name;

        @Schema(example = "This is a example bundle")
        @Setter(AccessLevel.PUBLIC)
        protected String description;

        @Schema(example = "data:image/png;base64,base64code")
        @Setter(AccessLevel.PUBLIC)
        protected String descriptionImage;

        @Schema(example = "V5")
        protected final String descriptorVersion;

        @Schema(example = "docker://registry.hub.docker.com/organization/bundle-sample")
        protected final String gitRepoAddress;
        @Schema(example = "https://github.com/organization/bundle-sample")
        private final String gitSrcRepoAddress;

        protected final List<String> dependencies;
        protected final List<String> bundleGroups; //Used for bundle group versions, need to make it bundleGroupVersions

        public BundleNoId(String id, String name, String description, String gitRepoAddress, String gitSrcRepoAddress, List<String> dependencies, List<String> bundleGroupVersions, String descriptorVersion) {
        	this.bundleId = id;
            this.name = name;
            this.description = description;
            this.gitRepoAddress = gitRepoAddress;
            this.gitSrcRepoAddress = gitSrcRepoAddress;
            this.dependencies = dependencies;
            this.bundleGroups = bundleGroupVersions;
            this.descriptorVersion = descriptorVersion;
        }

        public BundleNoId(com.entando.hub.catalog.persistence.entity.Bundle entity) {
        	this.bundleId = entity.getId().toString();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.gitRepoAddress = entity.getGitRepoAddress();
            this.gitSrcRepoAddress = entity.getGitSrcRepoAddress();
            this.dependencies = Arrays.asList(entity.getDependencies().split(","));
            this.bundleGroups = entity.getBundleGroupVersions().stream().map(bundleGroupVersion -> bundleGroupVersion.getId().toString()).collect(Collectors.toList());
            this.descriptorVersion = entity.getDescriptorVersion().toString();
        }

        public com.entando.hub.catalog.persistence.entity.Bundle createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.Bundle ret = new com.entando.hub.catalog.persistence.entity.Bundle();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            ret.setGitRepoAddress(this.getGitRepoAddress());
            ret.setGitSrcRepoAddress(this.getGitSrcRepoAddress());
            ret.setDependencies(String.join(",", this.getDependencies()));

            //for now, if the repo address does not start with docker, we assume it's a V1 bundle.
            boolean isDocker = (this.getGitRepoAddress() != null) && (this.getGitRepoAddress().startsWith("docker:"));
            ret.setDescriptorVersion(isDocker ? DescriptorVersion.V5 : DescriptorVersion.V1);

            //TODO bundlegroups contains bundle group version id! fix it!
            Set<BundleGroupVersion> bundleGroupVersions = this.bundleGroups.stream().map((bundleGroupVersionId) -> {
            	BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
            	bundleGroupVersion.setId(Long.valueOf(bundleGroupVersionId));
                return bundleGroupVersion;
            }).collect(Collectors.toSet());
            ret.setBundleGroupVersions(bundleGroupVersions);
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }
    }
}
