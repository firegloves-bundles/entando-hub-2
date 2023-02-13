package com.entando.hub.catalog.rest.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Category extends CategoryNoId {

  private String categoryId;

  public Category(com.entando.hub.catalog.persistence.entity.Category entity) {
    super(entity);
    this.categoryId = entity.getId().toString();
  }

  public Category(String organisationId, String name, String description) {
    super(name, description);
    this.categoryId = organisationId;
  }


}
