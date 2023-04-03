package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appbuilder/api/bundlegroups")
public class AppBuilderBundleGroupsController {
    private final BundleGroupVersionService bundleGroupVersionService;

    private static final Logger logger = LoggerFactory.getLogger(AppBuilderBundleGroupsController.class);

    private final CatalogService catalogService;

    public AppBuilderBundleGroupsController(BundleGroupVersionService bundleGroupVersionService,
                                            CatalogService catalogService) {
        this.bundleGroupVersionService = bundleGroupVersionService;
        this.catalogService = catalogService;
    }

    @Operation(summary = "Get all the bundleGroups in the hub", description = "Public api, no authentication required.")
    @GetMapping(value = "/", produces = {"application/json"})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> getBundleGroupVersions(@RequestHeader(name = "Entando-hub-api-key", required = false) String apiKey, @RequestParam Integer page, @RequestParam Integer pageSize) {

        logger.debug("REST request to get bundle group versions");
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
        Catalog userCatalog;
        if (null != apiKey) {
            userCatalog = catalogService.getCatalogByApiKey(apiKey);
            return bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalog.getId(), sanitizedPageNum, pageSize);
        }
        return bundleGroupVersionService.getPublicCatalogPublishedBundleGroupVersions(sanitizedPageNum, pageSize);
    }

}
