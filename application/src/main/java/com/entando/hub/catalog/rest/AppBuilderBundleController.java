package com.entando.hub.catalog.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
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

	public AppBuilderBundleController(BundleService bundleService,
			BundleGroupVersionService bundleGroupVersionService) {
		this.bundleService = bundleService;
		this.bundleGroupVersionService = bundleGroupVersionService;
	}

	@Operation(summary = "Get all the bundles in the hub", description = "Public api, no authentication required. You can provide a bundleGroupId to get all the bundles in that")
	@CrossOrigin
	@GetMapping("/")
	public PagedContent<BundleController.Bundle, Bundle> getBundles(@RequestParam Integer page,@RequestParam Integer pageSize, @RequestParam(required = false) String bundleGroupId) {
		Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

		Page<Bundle> bundlesPage = bundleService.getBundles(sanitizedPageNum, pageSize,Optional.ofNullable(bundleGroupId));
		PagedContent<BundleController.Bundle, Bundle> pagedContent = new PagedContent<>(
				bundlesPage.getContent().stream().map(BundleController.Bundle::new).peek(bundle -> {
					// add the bundle group image as bundle image
					List<String> bundleGroupVersons = bundle.getBundleGroups();
					if (bundleGroupVersons != null && bundleGroupVersons.size() > 0) {
						Optional<BundleGroupVersion> optionalBundleGroup = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersons.get(0));
						optionalBundleGroup.ifPresent(group -> bundle.setDescriptionImage(group.getDescriptionImage()));
					}
				}).collect(Collectors.toList()), bundlesPage);
		return pagedContent;
	}

}
