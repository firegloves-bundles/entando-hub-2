package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper extends BaseStandardMapper<Category, CategoryDto> {

  @Mapping(source = "categoryId", target = "id", qualifiedByName = "toEntityId")
  @Mapping(target = "bundleGroups", expression = "java(toEntityBundleGroups(dto.getBundleGroups()))")
  Category toEntity(CategoryDto dto);

  @Mapping(source = "id", target = "categoryId", qualifiedByName = "toDtoId")
  @Mapping(source = "bundleGroups", target = "bundleGroups", qualifiedByName = "toDtoBundleGroups")
  CategoryDto toDto(Category entity);


  default Set<BundleGroup> toEntityBundleGroups(List<String> bundleGroups) {
    return (bundleGroups != null && !bundleGroups.isEmpty()) ?
      bundleGroups.stream().map(id -> {
        BundleGroup bundleGroup = new BundleGroup();

        bundleGroup.setId(Long.parseLong(id));
        return bundleGroup;
      }).collect(Collectors.toSet())
      : null;
  }


}
