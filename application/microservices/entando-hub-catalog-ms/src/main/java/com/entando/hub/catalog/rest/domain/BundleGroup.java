package com.entando.hub.catalog.rest.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class BundleGroup extends BundleGroupNoId {
  private final String bundleGroupId;

  public BundleGroup(String bundleGroupId, String name, String organizationId) {
    super(name, organizationId);
    this.bundleGroupId = bundleGroupId;
  }

  public BundleGroup(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
    super(entity);
    this.bundleGroupId = entity.getId().toString();
  }

}
