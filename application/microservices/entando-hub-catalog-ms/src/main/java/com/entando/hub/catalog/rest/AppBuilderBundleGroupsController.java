package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.ApplicationConstants.API_KEY_HEADER;
import static com.entando.hub.catalog.config.ApplicationConstants.CATALOG_ID_PARAM;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> getBundleGroupVersions(
            @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(name = CATALOG_ID_PARAM, required = false) Long catalogId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        logger.debug("REST request to get bundle group versions");
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
        Catalog userCatalog;
        if (StringUtils.isNotEmpty(apiKey)) {
            userCatalog = catalogService.getCatalogByApiKey(apiKey);
            return bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalog.getId(), sanitizedPageNum, pageSize);
        }
        return bundleGroupVersionService.getPublicCatalogPublishedBundleGroupVersions(sanitizedPageNum, pageSize);
    }

}
