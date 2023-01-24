package com.entando.hub.catalog.response;

import java.time.LocalDateTime;
import java.util.List;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BundleGroupVersionFilteredResponseView {
	private Long bundleGroupId;
	private Long bundleGroupVersionId;

	@Schema(example = "bundle-sample")
	private String name;

	@Schema(example = "This is a example bundle")
	private String description;

	@Schema(example = "data:image/png;base64,base64code")
	private String descriptionImage;

	@Schema(example = "https://github.com/organization/sample-bundle#read-me")
	private String documentationUrl;

	@Schema(example = "1.0.0")
	private String version;
	private BundleGroupVersion.Status status;
	private Long organisationId;

	@Schema(example = "Entando")
	private String organisationName;
	private List<String> categories;
	private List<String> children;
	private List<String> allVersions;
	private LocalDateTime createdAt;
	private LocalDateTime lastUpdate;
	private String bundleGroupUrl;
	private Boolean isEditable = false;
	private boolean canAddNewVersion = false;
	private Boolean displayContactUrl;

	@Schema(example = "https://yoursite.com/contact-us")
	private String contactUrl;
}
