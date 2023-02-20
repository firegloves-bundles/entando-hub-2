package com.entando.hub.catalog.rest.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CategoryDto extends CategoryNoId {

  private String categoryId;

  public CategoryDto(com.entando.hub.catalog.persistence.entity.Category entity) {
    super(entity);
    this.categoryId = entity.getId().toString();
  }

  public CategoryDto(String organisationId, String name, String description) {
    super(name, description);
    this.categoryId = organisationId;
  }


}
