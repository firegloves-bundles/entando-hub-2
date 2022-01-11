package com.entando.hub.catalog.rest;


import java.util.Objects;

import org.springframework.web.bind.annotation.CrossOrigin;
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

    public AppBuilderBundleGroupsController(BundleGroupVersionController bundleGroupVersionController) {
        this.bundleGroupVersionController = bundleGroupVersionController;
    }


    @Operation(summary = "Get all the bundleGroups in the hub", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping("/")
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> getBundleGroupVersionsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses) {
		if (Objects.isNull(statuses)) {
			statuses = new String[1];
			statuses[0] = BundleGroupVersion.Status.PUBLISHED.toString();
		}
        return bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, statuses);
    }

}
