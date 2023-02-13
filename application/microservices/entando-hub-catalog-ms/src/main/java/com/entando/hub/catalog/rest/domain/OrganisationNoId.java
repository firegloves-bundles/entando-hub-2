package com.entando.hub.catalog.rest.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class OrganisationNoId {
  @Schema(example = "Entando")
  protected final String name;

  @Schema(example = "Application Composition Platform for Kubernetes")
  protected final String description;

  protected List<String> bundleGroups;

  public OrganisationNoId(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public OrganisationNoId(com.entando.hub.catalog.persistence.entity.Organisation entity) {
    this.name = entity.getName();
    this.description = entity.getDescription();
    if (entity.getBundleGroups() != null) {
      this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());
    }
  }


  public com.entando.hub.catalog.persistence.entity.Organisation createEntity(Optional<String> id) {
    com.entando.hub.catalog.persistence.entity.Organisation ret = new com.entando.hub.catalog.persistence.entity.Organisation();
    ret.setDescription(this.getDescription());
    ret.setName(this.getName());
    id.map(Long::valueOf).ifPresent(ret::setId);
    return ret;
  }

}
