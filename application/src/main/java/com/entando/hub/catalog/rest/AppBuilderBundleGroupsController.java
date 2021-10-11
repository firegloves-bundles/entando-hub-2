package com.entando.hub.catalog.rest;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appbuilder/api/bundlegroups")
public class AppBuilderBundleGroupsController {
    private final BundleGroupController bundleGroupController;

    public AppBuilderBundleGroupsController(BundleGroupController bundleGroupController) {
        this.bundleGroupController = bundleGroupController;
    }

    @CrossOrigin
    @GetMapping("/")
    public PagedContent<BundleGroupController.BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses) {
        return bundleGroupController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, statuses);
    }

}
