package com.entando.hub.catalog.rest;


import io.swagger.v3.oas.annotations.Operation;

import java.util.Objects;

import org.springframework.web.bind.annotation.*;

import com.entando.hub.catalog.persistence.entity.BundleGroup;

@RestController
@RequestMapping("/appbuilder/api/bundlegroups")
public class AppBuilderBundleGroupsController {
    private final BundleGroupController bundleGroupController;

    public AppBuilderBundleGroupsController(BundleGroupController bundleGroupController) {
        this.bundleGroupController = bundleGroupController;
    }


    @Operation(summary = "Get all the bundleGroups in the hub", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @CrossOrigin
    @GetMapping("/")
    public PagedContent<BundleGroupController.BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses) {
		if (Objects.isNull(statuses)) {
			statuses = new String[1];
			statuses[0] = BundleGroup.Status.PUBLISHED.toString();
		}
        return bundleGroupController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, statuses);
    }

}
