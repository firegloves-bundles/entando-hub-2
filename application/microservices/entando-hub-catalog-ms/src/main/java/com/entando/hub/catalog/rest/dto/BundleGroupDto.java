package com.entando.hub.catalog.rest.dto;

import com.entando.hub.catalog.rest.domain.BundleGroupVersionView;
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
  protected String organisationId;

  @Schema(example = "Entando")
  protected String organisationName;
  protected List<String> categories;
  protected BundleGroupVersionView versionDetails;

  public BundleGroupDto(String bundleGroupId, String name, String organizationId) {
//    super(name, organizationId);
    this.bundleGroupId = bundleGroupId;
  }

  /*
  public BundleGroupDto(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
    super(entity);
    this.bundleGroupId = entity.getId().toString();
  }
  */

}
