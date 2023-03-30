package com.entando.hub.catalog.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

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
