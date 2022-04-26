package com.entando.hub.catalog.rest;


import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<BundleGroupVersion> byTemplateInIt = bundleGroupVersionRepository.getByTemplateInIt();

        List<BundleTemplate> bundleTemplateList = byTemplateInIt.stream()
                .flatMap(bundleGroupVersion ->
                        bundleGroupVersion.getBundles().stream().map(bundle -> new BundleTemplate(bundleGroupVersion, bundleGroupVersion.getBundleGroup(), bundle))
                ).collect(Collectors.toList());

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

}
