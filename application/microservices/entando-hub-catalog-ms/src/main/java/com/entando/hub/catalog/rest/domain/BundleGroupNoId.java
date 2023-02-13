package com.entando.hub.catalog.rest.domain;

import com.entando.hub.catalog.persistence.entity.Organisation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class BundleGroupNoId {

  @Schema(example = "bundle group name")
  private String name;
  protected String organisationId;

  @Schema(example = "Entando")
  protected String organisationName;
  protected List<String> categories;
  protected BundleGroupVersionView versionDetails;

  public BundleGroupNoId(String name, String organisationId) {
    this.name = name;
    this.organisationId = organisationId;
  }

  public BundleGroupNoId(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
    this.name = entity.getName();

    if (entity.getOrganisation() != null) {
      this.organisationId = entity.getOrganisation().getId().toString();
      this.organisationName = entity.getOrganisation().getName();
    }
    if (entity.getCategories() != null) {
      this.categories = entity.getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
    }
  }

  public com.entando.hub.catalog.persistence.entity.BundleGroup createEntity(Optional<String> id) {
    com.entando.hub.catalog.persistence.entity.BundleGroup ret = new com.entando.hub.catalog.persistence.entity.BundleGroup();
    ret.setName(this.getName());
    if (this.organisationId != null) {
      Organisation organisation = new Organisation();
      organisation.setId(Long.parseLong(this.organisationId));
      organisation.setName(this.organisationName);
      ret.setOrganisation(organisation);
    }
    id.map(Long::valueOf).ifPresent(ret::setId);
    return ret;
  }
}
