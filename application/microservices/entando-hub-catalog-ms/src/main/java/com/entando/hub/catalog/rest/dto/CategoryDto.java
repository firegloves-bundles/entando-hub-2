package com.entando.hub.catalog.rest.dto;

import com.entando.hub.catalog.persistence.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
public class CategoryDto {

  private String categoryId;

  @Schema(example = "Solution Template")
  private String name;

  @Schema(example = "a brief description")
  private String description;
  protected List<String> bundleGroups;

}
