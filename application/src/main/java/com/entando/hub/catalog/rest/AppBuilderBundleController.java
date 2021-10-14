package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appbuilder/api/bundles/")

public class AppBuilderBundleController {

    private final BundleService bundleService;
    private final BundleGroupService bundleGroupService;

    public AppBuilderBundleController(BundleService bundleService, BundleGroupService bundleGroupService) {
        this.bundleService = bundleService;
        this.bundleGroupService = bundleGroupService;
    }

    //@RolesAllowed("codemotion-bff-admin")
    //@PreAuthorize("hasAuthority('ROLE_mf-widget-admin')")
    @CrossOrigin
    @GetMapping("/")
    public PagedContent<BundleController.Bundle, Bundle> getBundles(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String bundleGroupId) {
        Integer sanitizedPageNum = page >=1 ? page -1 : 0;

        Page<Bundle> bundlesPage = bundleService.getBundles(sanitizedPageNum, pageSize, Optional.ofNullable(bundleGroupId));
        PagedContent<BundleController.Bundle, Bundle> pagedContent = new PagedContent<>(bundlesPage.getContent().stream().map(BundleController.Bundle::new)
                .peek(bundle -> {
                    //add the bundle group image as bundle image
                    List<String> bundleGroups = bundle.getBundleGroups();
                    if (bundleGroups != null && bundleGroups.size() > 0) {
                        Optional<BundleGroup> optionalBundleGroup = bundleGroupService.getBundleGroup(bundleGroups.get(0));
                        optionalBundleGroup.ifPresent(group -> bundle.setDescriptionImage(group.getDescriptionImage()));
                    }
                }).collect(Collectors.toList()),bundlesPage);
        return pagedContent;
    }





}
