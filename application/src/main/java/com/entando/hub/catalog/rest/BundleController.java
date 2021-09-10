package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.service.BundleService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bundles")
@Slf4j
public class BundleController {

    final private BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }

    //@RolesAllowed("codemotion-bff-admin")
    //@PreAuthorize("hasAuthority('ROLE_mf-widget-admin')")
    @CrossOrigin
    @GetMapping("/")
    public List<Bundle> getBundles(@RequestParam(required = false) String bundleGroupId) {
        return bundleService.getBundles(Optional.ofNullable(bundleGroupId)).stream().map(Bundle::new).collect(Collectors.toList());
    }

    @CrossOrigin
    @GetMapping("/{bundleId}")
    public ResponseEntity<Bundle> getBundle(@PathVariable() String bundleId) {
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (bundleOptional.isPresent()) {
            return new ResponseEntity<>(bundleOptional.map(Bundle::new).get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PostMapping("/")
    public ResponseEntity<Bundle> createBundle(@RequestBody BundleNoId bundle) {
        com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(bundle.createEntity(Optional.empty()));
        return new ResponseEntity<>(new Bundle(entity), HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping("/{bundleId}")
    public ResponseEntity<Bundle> updateBundle(@PathVariable String bundleId, @RequestBody BundleNoId bundle) {
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (!bundleOptional.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(bundle.createEntity(Optional.of(bundleId)));
            return new ResponseEntity<>(new Bundle(entity), HttpStatus.OK);
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class Bundle extends BundleNoId {
        private final String bundleId;
        public Bundle(String bundleId, String name, String description, String gitRepoAddress, List<String> dependencies, List<String> bundleGroups) {
            super(name, description, gitRepoAddress, dependencies, bundleGroups);
            this.bundleId = bundleId;
        }

        public Bundle(com.entando.hub.catalog.persistence.entity.Bundle entity) {
            super(entity);
            this.bundleId = entity.getId().toString();
        }
    }

    @Data
    public static class BundleNoId {
        protected final String name;
        protected final String description;
        protected final String gitRepoAddress;
        protected final List<String> dependencies;
        protected final List<String> bundleGroups;

        public BundleNoId(String name, String description, String gitRepoAddress, List<String> dependencies, List<String> bundleGroups) {
            this.name = name;
            this.description = description;
            this.gitRepoAddress = gitRepoAddress;
            this.dependencies = dependencies;
            this.bundleGroups = bundleGroups;
        }


        public BundleNoId(com.entando.hub.catalog.persistence.entity.Bundle entity) {
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
