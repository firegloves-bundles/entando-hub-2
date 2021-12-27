package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.service.BundleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/bundles/")
public class BundleController {
    
    private final Logger logger = LoggerFactory.getLogger(BundleController.class);

    final private BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }


    @Operation(summary = "Get all the bundles", description = "Public api, no authentication required. You can provide a bundleGroupId to get all the bundles in that")
    @CrossOrigin
    @GetMapping("/")
    public List<Bundle> getBundles(@RequestParam(required = false) String bundleGroupId) {
        logger.debug("REST request to get Bundles by group: {}", bundleGroupId);
        return bundleService.getBundles(Optional.ofNullable(bundleGroupId)).stream().map(Bundle::new).collect(Collectors.toList());
    }

    @Operation(summary = "Get the bundle details", description = "Public api, no authentication required. You have to provide the bundleId")
    @CrossOrigin
    @GetMapping("/{bundleId}")
    public ResponseEntity<Bundle> getBundle(@PathVariable() String bundleId) {
        logger.debug("REST request to get Bundle by id: {}", bundleId);
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (bundleOptional.isPresent()) {
            return new ResponseEntity<>(bundleOptional.map(Bundle::new).get(), HttpStatus.OK);
        } else {
            logger.warn("Requested bundle '{}' does not exists", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @PostMapping("/")
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
    @CrossOrigin
    @PostMapping("/{bundleId}")
    public ResponseEntity<Bundle> updateBundle(@PathVariable String bundleId, @RequestBody BundleNoId bundle) {
        logger.debug("REST request to updare Bundle with id {}: {}", bundleId, bundle);
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (!bundleOptional.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            logger.warn("Bundle '{}' does not exists", bundleId);
            com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(bundle.createEntity(Optional.of(bundleId)));
            return new ResponseEntity<>(new Bundle(entity), HttpStatus.OK);
        }
    }
    @Operation(summary = "Delete a bundle", description = "Protected api, only eh-admin can access it. A bundleGroup can be deleted only if it is in DELETE_REQ status  You have to provide the bundlegroupId identifying the category")
    @RolesAllowed({ADMIN})
    @CrossOrigin
    @DeleteMapping("/{bundleId}")
    @Transactional
    public ResponseEntity<CategoryController.Category> deleteBundleGroup(@PathVariable String bundleId) {
        logger.debug("REST request to delete bundle {}", bundleId);
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
            bundleService.deleteBundle(bundleOptional.get());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
      }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class Bundle extends BundleNoId {
        private final String bundleId;
        public Bundle(String bundleId, String name, String description, String gitRepoAddress, List<String> dependencies, List<String> bundleGroups) {
            super(bundleId, name, description, gitRepoAddress, dependencies, bundleGroups);
            this.bundleId = bundleId;
        }

        public Bundle(com.entando.hub.catalog.persistence.entity.Bundle entity) {
            super(entity);
            this.bundleId = entity.getId().toString();
        }
    }

    @Data
    public static class BundleNoId {
        protected final String bundleId;
        protected final String name;
        protected final String description;

        @Setter(AccessLevel.PUBLIC)
        protected String descriptionImage;


        protected final String gitRepoAddress;
        protected final List<String> dependencies;
        protected final List<String> bundleGroups;

        public BundleNoId(String id, String name, String description, String gitRepoAddress, List<String> dependencies, List<String> bundleGroups) {
        	this.bundleId = id;
            this.name = name;
            this.description = description;
            this.gitRepoAddress = gitRepoAddress;
            this.dependencies = dependencies;
            this.bundleGroups = bundleGroups;
        }


        public BundleNoId(com.entando.hub.catalog.persistence.entity.Bundle entity) {
        	this.bundleId = entity.getId().toString();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.gitRepoAddress = entity.getGitRepoAddress();
            this.dependencies = Arrays.asList(entity.getDependencies().split(","));
            this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());

        }

        public com.entando.hub.catalog.persistence.entity.Bundle createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.Bundle ret = new com.entando.hub.catalog.persistence.entity.Bundle();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            ret.setGitRepoAddress(this.getGitRepoAddress());
            ret.setDependencies(String.join(",", this.getDependencies()));

            Set<BundleGroup> bundleGroups = this.bundleGroups.stream().map((bundleGroupId) -> {
                BundleGroup bundleGroup = new BundleGroup();
                bundleGroup.setId(Long.valueOf(bundleGroupId));
                return bundleGroup;
            }).collect(Collectors.toSet());
            ret.setBundleGroups(bundleGroups);
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;

        }

    }

}
