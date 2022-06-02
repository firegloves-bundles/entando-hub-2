package com.entando.hub.catalog.response;

import java.time.LocalDateTime;
import java.util.List;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

import lombok.Data;

@Data
public class BundleGroupVersionFilteredResponseView {
	private Long bundleGroupId;
	private Long bundleGroupVersionId;
	private String name;
	private String description;
	private String descriptionImage;
	private String documentationUrl;
	private String version;
	private BundleGroupVersion.Status status;
	private Long organisationId;
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
	private String contactUrl;
}
