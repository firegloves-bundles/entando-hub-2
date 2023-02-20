package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.domain.CategoryDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper extends BaseMapper<Category, CategoryDto> {

  @Mapping(source = "categoryId", target = "id", qualifiedByName = "toEntityID")
  @Mapping(target = "bundleGroups", expression = "java(toEntityBundleGroups(dto.getBundleGroups()))")
  Category toEntity(CategoryDto dto);

  @Mapping(source = "id", target = "categoryId", qualifiedByName = "toDtoCategoryId")
  @Mapping(source = "bundleGroups", target = "bundleGroups", qualifiedByName = "toDtoBundleGroups")
  CategoryDto toDto(Category entity);

  @Named("toEntityID")
  static Long toEntityId(String value) {
    if (StringUtils.isNotBlank(value)) {
      return Long.parseLong(value);
    }
    return null;
  }

  @Named("toDtoCategoryId")
  static String toDtoCategoryId(Long id) {
    return (null != id) ? id.toString() : null;
  }

  default Set<BundleGroup> toEntityBundleGroups(List<String> bundleGroups) {
    return (bundleGroups != null && !bundleGroups.isEmpty()) ?
      bundleGroups.stream().map(id -> {
        BundleGroup bundleGroup = new BundleGroup();

        bundleGroup.setId(Long.parseLong(id));
        return bundleGroup;
      }).collect(Collectors.toSet())
      : null;
  }

  @Named("toDtoBundleGroups")
  default List<String> toDtoBundleGroups(Set<BundleGroup> bundleGroups) {
    return (bundleGroups != null && !bundleGroups.isEmpty()) ?
      bundleGroups.stream().map(b -> b.getId().toString()).collect(Collectors.toList()) : null;
  }

}
