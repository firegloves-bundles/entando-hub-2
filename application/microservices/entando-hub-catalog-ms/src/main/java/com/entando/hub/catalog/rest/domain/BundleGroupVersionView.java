package com.entando.hub.catalog.rest.domain;

import com.entando.hub.catalog.rest.dto.BundleDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class BundleGroupVersionView {
  protected String bundleGroupId;

  @Schema(example = "a brief description")
  protected String description;

  @Schema(example = "data:image/png;base64,base64code")
  @ToString.Exclude
  protected String descriptionImage;

  @Schema(example = "https://github.com/organization/sample-bundle#read-me")
  protected String documentationUrl;

  @Schema(example = "1.0.0")
  protected String version;
  protected com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status status;
  protected Long organisationId;

  @Schema(example = "Entando")
  protected String organisationName;

  @Schema(example = "simple bundle name")
  protected String name;
  protected LocalDateTime lastUpdate;
  protected List<String> categories;
  protected List<Long> children;
  protected String bundleGroupVersionId;
  protected List<BundleDto> bundles;
  protected Boolean displayContactUrl;

  @Schema(example = "https://yoursite.com/contact-us")
  protected String contactUrl;

  public BundleGroupVersionView(String bundleGroupId, String description, String descriptionImage, String version) {
    this.bundleGroupId = bundleGroupId;
    this.description = description;
    this.descriptionImage = descriptionImage;
    this.version = version;
  }

  public BundleGroupVersionView(com.entando.hub.catalog.persistence.entity.BundleGroupVersion entity) {
    this.description = entity.getDescription();
    this.descriptionImage = entity.getDescriptionImage();
    this.status = entity.getStatus();
    this.documentationUrl = entity.getDocumentationUrl();
    this.version = entity.getVersion();
    if (entity.getBundleGroup() != null) {
      this.bundleGroupId = entity.getBundleGroup().getId().toString();
    }
    if (entity.getBundleGroup().getOrganisation() != null) {
      this.organisationId = entity.getBundleGroup().getOrganisation().getId();
      this.organisationName = entity.getBundleGroup().getOrganisation().getName();
    }
    this.name = entity.getBundleGroup().getName();
    this.lastUpdate = entity.getLastUpdated();
    if (entity.getBundleGroup().getCategories() != null) {
      this.categories = entity.getBundleGroup().getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
    }
    if (entity.getBundles() != null) {
      this.children = entity.getBundles().stream().map((children) -> children.getId()).collect(Collectors.toList());
    }
    this.displayContactUrl = entity.getDisplayContactUrl();
    this.contactUrl = entity.getContactUrl();
  }

  public com.entando.hub.catalog.persistence.entity.BundleGroupVersion createEntity(Optional<String> id, com.entando.hub.catalog.persistence.entity.BundleGroup bundleGroup) {
    com.entando.hub.catalog.persistence.entity.BundleGroupVersion bundleGroupVersion = new com.entando.hub.catalog.persistence.entity.BundleGroupVersion();
    bundleGroupVersion.setDescription(this.getDescription());
    bundleGroupVersion.setDescriptionImage(this.getDescriptionImage());
    bundleGroupVersion.setDocumentationUrl(this.getDocumentationUrl());
    bundleGroupVersion.setStatus(this.getStatus());
    bundleGroupVersion.setVersion(this.getVersion());
    bundleGroupVersion.setBundleGroup(bundleGroup);
    bundleGroupVersion.setDisplayContactUrl(this.getDisplayContactUrl());
    bundleGroupVersion.setContactUrl(this.getContactUrl());
    id.map(Long::valueOf).ifPresent(bundleGroupVersion::setId);
    return bundleGroupVersion;
  }
}
