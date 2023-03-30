package com.entando.hub.catalog.rest.dto;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BundleTemplateDto {
  @Schema(example = "Entando 7.1 Tutorials")
  private String bundleGroupName;

  @Schema(example = "bundle-sample")
  private String bundleName;

  @Schema(example = "https://github.com/entando/bundle-sample.git")
  private String gitSrcRepoAddress;
  private Long bundleGroupVersionId;
  private Long bundleGroupId;
  private Long bundleId;

  public BundleTemplateDto(BundleGroupVersion bundleGroupVersion, BundleGroup bundleGroup, Bundle bundle) {
    this.bundleGroupName = bundleGroup.getName();
    this.bundleName = bundle.getName();
    this.gitSrcRepoAddress = bundle.getGitSrcRepoAddress();
    this.bundleGroupVersionId = bundleGroupVersion.getId();
    this.bundleGroupId = bundleGroup.getId();
    this.bundleId = bundle.getId();
  }
}
