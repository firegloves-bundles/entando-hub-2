package com.entando.hub.catalog.rest;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/appbuilder/api/bundlegroups")
public class AppBuilderBundleGroupsController {
    private final BundleGroupVersionController bundleGroupVersionController;
	private final Logger logger = LoggerFactory.getLogger(AppBuilderBundleGroupsController.class);
	
    public AppBuilderBundleGroupsController(BundleGroupVersionController bundleGroupVersionController) {
        this.bundleGroupVersionController = bundleGroupVersionController;
    }

    @Operation(summary = "Get all the bundleGroups in the hub", description = "Public api, no authentication required.")
    @GetMapping(value = "/", produces = {"application/json"})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> getBundleGroupVersionsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] descriptorVersions) {
    	logger.debug("REST request to get bundle group versions and filter them by descriptorVersions {}", descriptorVersions);

        String[] statuses = {BundleGroupVersion.Status.PUBLISHED.toString()};

        //No-op currently but descriptorVersions are available if we need to refine functionality
        // Set<Bundle.DescriptorVersion> versions = AppBuilderBundleController.descriptorVersionsToSet(descriptorVersions);

        return bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, null, null, statuses, null);
    }

}
