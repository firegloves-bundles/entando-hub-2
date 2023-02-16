package com.entando.hub.catalog.rest.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BundleGroupDto extends BundleGroupNoId {

  private String bundleGroupId;

  public BundleGroupDto(String bundleGroupId, String name, String organizationId) {
    super(name, organizationId);
    this.bundleGroupId = bundleGroupId;
  }

  public BundleGroupDto(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
    super(entity);
    this.bundleGroupId = entity.getId().toString();
  }

}
