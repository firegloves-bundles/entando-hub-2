package com.entando.hub.catalog.rest;


import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ent/api/templates")
public class EntTemplateController {
    private BundleGroupVersionRepository bundleGroupVersionRepository;
    private final Logger logger = LoggerFactory.getLogger(EntTemplateController.class);

    public EntTemplateController(BundleGroupVersionRepository bundleGroupVersionRepository) {
        this.bundleGroupVersionRepository = bundleGroupVersionRepository;
    }

    @Operation(summary = "Get all the templates for the bundle that are in the hub", description = "Public api, no authentication required.")
    @GetMapping("/bundles")
    public List<BundleTemplate> getBundleTemplates() {
        List<BundleGroupVersion> bundleGroupVersionList = bundleGroupVersionRepository.getByTemplateInIt();

        List<BundleTemplate> bundleTemplateList = bundleGroupVersionList.stream()
                .flatMap(bundleGroupVersion ->
                        bundleGroupVersion.getBundles().stream().map(bundle -> new BundleTemplate(bundleGroupVersion, bundleGroupVersion.getBundleGroup(), bundle))
                )
                .filter(bundleTemplate ->
                        bundleTemplate.gitSrcRepoAddress != null && bundleTemplate.gitSrcRepoAddress.length() > 0
                )
                .collect(Collectors.toList());

        return bundleTemplateList;
    }

    @Operation(summary = "Get all the bundle groups having templates in them, they can be filtered by name part", description = "Public api, no authentication required.")
    @GetMapping("/bundlegroups")
    public List<BundleGroupTemplate> getBundleGroupsWithTemplates(@RequestParam(required = false) String name) {
        List<BundleGroupVersion> bundleGroupVersionList;
        if (name != null) {
            bundleGroupVersionList = bundleGroupVersionRepository.getByTemplateInItFilteredByName("%" + name + "%");
        } else {
            bundleGroupVersionList = bundleGroupVersionRepository.getByTemplateInIt();
        }

        List<BundleGroupTemplate> bundleGroupTemplateList = bundleGroupVersionList.stream()
                .map(bundleGroupVersion -> new BundleGroupTemplate(bundleGroupVersion, bundleGroupVersion.getBundleGroup())).collect(Collectors.toList());

        return bundleGroupTemplateList;
    }

    @Operation(summary = "Get the templates for the bundle given the bundlegroup id", description = "Public api, no authentication required.")
    @GetMapping("/bundlegroups/{id}")
    public List<BundleTemplate> getBundleTemplateByBundleGroupId(@PathVariable Long id) {
        List<BundleGroupVersion> bundleGroupVersionList;
        bundleGroupVersionList = bundleGroupVersionRepository.getByTemplateInItAndId(id);

        List<BundleTemplate> bundleTemplateList = bundleGroupVersionList.stream()
                .flatMap(bundleGroupVersion ->
                        bundleGroupVersion.getBundles().stream().map(bundle -> new BundleTemplate(bundleGroupVersion, bundleGroupVersion.getBundleGroup(), bundle))
                )
                .filter(bundleTemplate ->
                        bundleTemplate.gitSrcRepoAddress != null && bundleTemplate.gitSrcRepoAddress.length() > 0
                )
                .collect(Collectors.toList());

        return bundleTemplateList;
    }


    @Data
    public static class BundleTemplate {
        private String bundleGroupName;
        private String bundleName;
        private String gitSrcRepoAddress;
        private Long bundleGroupVersionId;
        private Long bundleGroupId;
        private Long bundleId;

        public BundleTemplate(BundleGroupVersion bundleGroupVersion, BundleGroup bundleGroup, Bundle bundle) {
            this.bundleGroupName = bundleGroup.getName();
            this.bundleName = bundle.getName();
            this.gitSrcRepoAddress = bundle.getGitSrcRepoAddress();
            this.bundleGroupVersionId = bundleGroupVersion.getId();
            this.bundleGroupId = bundleGroup.getId();
            this.bundleId = bundle.getId();
        }
    }

    @Data
    public static class BundleGroupTemplate { //this is a bundle group version containing templates
        private String bundleGroupName;
        private Long bundleGroupVersionId;

        public BundleGroupTemplate(BundleGroupVersion bundleGroupVersion, BundleGroup bundleGroup) {
            this.bundleGroupName = bundleGroup.getName();
            this.bundleGroupVersionId = bundleGroupVersion.getId();
        }
    }


}
