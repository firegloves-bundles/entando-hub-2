package com.entando.hub.catalog.rest.dto;

import com.entando.hub.catalog.persistence.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryDto {

  private String categoryId;

  @Schema(example = "Solution Template")
  private String name;

  @Schema(example = "a brief description")
  private String description;
  protected List<String> bundleGroups;

  @Deprecated
  public CategoryDto(Category entity) {
//    super(entity);
    this.categoryId = entity.getId().toString();
    this.name = entity.getName();
    this.description = entity.getDescription();
    if (entity.getBundleGroups() != null) {
      this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());
    }
  }

  @Deprecated
  public CategoryDto(String organisationId, String name, String description) {
//    super(name, description);
    this.categoryId = organisationId;
    this.name = name;
    this.description = description;
  }



}
