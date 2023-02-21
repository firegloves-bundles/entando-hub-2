package com.entando.hub.catalog.rest.domain;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CategoryNoId {

  @Schema(example = "Solution Template")
  private String name;

  @Schema(example = "a brief description")
  private String description;
  protected List<String> bundleGroups;

  public CategoryNoId(String name, String description) {
    this.name = name;
    this.description = description;

  }

  public CategoryNoId(com.entando.hub.catalog.persistence.entity.Category entity) {
    this.name = entity.getName();
    this.description = entity.getDescription();
    if (entity.getBundleGroups() != null) {
      this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());
    }
  }

//  public com.entando.hub.catalog.persistence.entity.Category createEntity(Optional<String> id) {
//    com.entando.hub.catalog.persistence.entity.Category ret = new com.entando.hub.catalog.persistence.entity.Category();
//    ret.setDescription(this.getDescription());
//    ret.setName(this.getName());
//    if (this.getBundleGroups() != null) {
//      ret.setBundleGroups(this.getBundleGroups().stream().map(bundleGroupId -> {
//        com.entando.hub.catalog.persistence.entity.BundleGroup bundleGroup = new BundleGroup();
//        bundleGroup.setId(Long.valueOf(bundleGroupId));
//        return bundleGroup;
//      }).collect(Collectors.toSet()));
//    }
//    id.map(Long::valueOf).ifPresent(ret::setId);
//    return ret;
//  }

}
