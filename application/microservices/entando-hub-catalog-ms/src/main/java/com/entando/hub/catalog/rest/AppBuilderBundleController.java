package com.entando.hub.catalog.rest;

import java.util.*;
import java.util.stream.Collectors;

import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.service.mapper.inclusion.BundleStandardMapper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.BundleService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/appbuilder/api/bundles/")
public class AppBuilderBundleController {

	private final BundleService bundleService;
	private final BundleGroupVersionService bundleGroupVersionService;
	private final BundleStandardMapper bundleStandardMapper;

	private static final Logger logger = LoggerFactory.getLogger(AppBuilderBundleController.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();

	public AppBuilderBundleController(BundleService bundleService, BundleGroupVersionService bundleGroupVersionService, BundleStandardMapper bundleStandardMapper) {
		this.bundleService = bundleService;
		this.bundleGroupVersionService = bundleGroupVersionService;
		this.bundleStandardMapper = bundleStandardMapper;
	}

	static Set<DescriptorVersion> descriptorVersionsToSet(String[] descriptorVersions) {
		Set<DescriptorVersion> versions = new HashSet<>();

		//Default to V1 for best compatibility
		if (ArrayUtils.isEmpty(descriptorVersions)) {
			versions.add(DescriptorVersion.V1);
		}
		//Otherwise map to the DescriptorVersion
		else {
			for (String v : descriptorVersions) {
				try {
					versions.add(DescriptorVersion.valueOf(v.toUpperCase()));
				} catch (Exception e) {
					logger.warn("Ignoring unrecognized descriptorVersion {} provided.", v);
				}
			}
		}
		return versions;
	}

	@Operation(summary = "Get all the bundles in the hub", description = "Public api, no authentication required. You can provide a bundleGroupId to get all the bundles. The descriptorVersions parameter is required in order to return docker-based bundles with Entando 7.1 and up.")
	@GetMapping(value = "/", produces = {"application/json"})
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
	@ApiResponse(responseCode = "200", description = "OK")
	public PagedContent<BundleDto, Bundle> getBundles(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String bundleGroupId, @RequestParam(required=false) String[] descriptorVersions) {
		logger.debug("{}: REST request to get bundles for the current published version by bundleGroup Id: {} ",CLASS_NAME, bundleGroupId );
		Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
		Set<DescriptorVersion> versions = descriptorVersionsToSet(descriptorVersions);
		Page<Bundle> bundlesPage = bundleService.getBundles(sanitizedPageNum, pageSize, Optional.ofNullable(bundleGroupId), versions);

		PagedContent<BundleDto, Bundle> pagedContent = new PagedContent<>(
				bundlesPage.getContent().stream().map(bundleStandardMapper::toDto).peek(bundle -> {
					// add the bundle group image as bundle image
					List<String> bundleGroupVersions = bundle.getBundleGroups();
					if (bundleGroupVersions != null && bundleGroupVersions.size() > 0) {
						Optional<BundleGroupVersion> optionalBundleGroup = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersions.get(0));
						optionalBundleGroup.ifPresent(group -> {
							bundle.setDescriptionImage(group.getDescriptionImage());
							bundle.setDescription(group.getDescription());
						});
					}
				}).collect(Collectors.toList()), bundlesPage);
		return pagedContent;
	}

}
