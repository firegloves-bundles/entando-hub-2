package com.entando.hub.catalog.rest.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class BundleGroupVersion extends BundleGroupVersionView {

  private final String bundleGroupVersionId;

  public BundleGroupVersion(com.entando.hub.catalog.persistence.entity.BundleGroupVersion entity) {
    super(entity);
    this.bundleGroupVersionId = entity.getId().toString();
  }
}
