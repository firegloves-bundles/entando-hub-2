package com.entando.hub.catalog.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString
@NoArgsConstructor
public class OrganisationDto {

  private String organisationId;
  @Schema(example = "Entando")
  private String name;

  @Schema(example = "Application Composition Platform for Kubernetes")
  private String description;

  private List<String> bundleGroups;

  public OrganisationDto(com.entando.hub.catalog.persistence.entity.Organisation entity) {
    this.name = entity.getName();
    this.description = entity.getDescription();
    if (entity.getBundleGroups() != null) {
      this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());
    }
    this.organisationId = entity.getId().toString();
  }

  public OrganisationDto(String organisationId, String name, String description) {
    this.name = name;
    this.description = description;
    this.organisationId = organisationId;
  }

}
