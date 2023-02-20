package com.entando.hub.catalog.rest.dto;

import com.entando.hub.catalog.rest.domain.BundleGroupVersionView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BundleGroupVersionDto extends BundleGroupVersionView {

  private String bundleGroupVersionId;

//  public BundleGroupVersionDto(com.entando.hub.catalog.persistence.entity.BundleGroupVersion entity) {
//    super(entity);
//    this.bundleGroupVersionId = entity.getId().toString();
//  }

}
