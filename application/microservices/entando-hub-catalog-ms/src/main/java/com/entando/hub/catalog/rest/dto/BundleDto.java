package com.entando.hub.catalog.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleDto {

  @Schema(example = "bundle identifier")
  private String bundleId;

  @Schema(example = "bundle-sample")
  private String name;

  @Schema(example = "This is a example bundle")
  @Setter(AccessLevel.PUBLIC)
  protected String description;

  @Schema(example = "data:image/png;base64,base64code")
  @Setter(AccessLevel.PUBLIC)
  protected String descriptionImage;

  @Schema(example = "V5")
  private String descriptorVersion;

  @Schema(example = "docker://registry.hub.docker.com/organization/bundle-sample")
  private String gitRepoAddress;
  @Schema(example = "https://github.com/organization/bundle-sample")
  private String gitSrcRepoAddress;

  private List<String> dependencies;
  private List<String> bundleGroups; //Used for bundle group versions, need to make it bundleGroupVersions


}
