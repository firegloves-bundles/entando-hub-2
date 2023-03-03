package com.entando.hub.catalog.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BundleGroupDto  {

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String bundleGroupId;
  @Schema(example = "bundle group name")
  private String name;
  private Long organisationId;

  private Boolean publicCatalog;

  @Schema(example = "Entando")
  private String organisationName;
  private List<String> categories;
  private BundleGroupVersionDto versionDetails;

//  public BundleGroupDto(String bundleGroupId, String name, String organizationId) {
//    super(name, organizationId);
//    this.bundleGroupId = bundleGroupId;
//  }

  /*
  public BundleGroupDto(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
    super(entity);
    this.bundleGroupId = entity.getId().toString();
  }
  */

}
