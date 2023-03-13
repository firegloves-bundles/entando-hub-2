package com.entando.hub.catalog.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@Jacksonized
public class BundleGroupVersionDto {

  private String bundleGroupVersionId;

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
//  protected String bundleGroupVersionId;
  protected List<BundleDto> bundles;
  protected Boolean displayContactUrl;

  @Schema(example = "https://yoursite.com/contact-us")
  protected String contactUrl;

}
