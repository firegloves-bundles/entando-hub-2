package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionOutDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BundleGroupVersionStandardMapper extends BaseMapperMethods {


  @Mapping(source = "bundleGroupVersionId", target = "id", qualifiedByName = "toEntityId")
  @Mapping(target = "bundles", ignore = true)
  BundleGroupVersion toEntity(BundleGroupVersionDto dto);

  @Mapping(source = "dto.bundleGroupVersionId", target = "id", qualifiedByName = "toEntityId")
  @Mapping(target = "dto.bundleGroup", expression = "java(toEntityBundleGroup(bundleGroup))")
  @Mapping(source = "dto.version", target = "version")
  @Mapping(target = "bundles", ignore = true)
  BundleGroupVersion toEntity(BundleGroupVersionDto dto, BundleGroup bundleGroup);


  @Mapping(target = "children", expression = "java(toDtoBundles(entity.getBundles()))")
  @Mapping(target = "bundles", ignore = true)
  @Mapping(target = "bundleGroupId", expression = "java(toDtoBundleGroupId(entity.getBundleGroup()))")
  @Mapping(target = "organisationId", expression = "java(toDtoOrganizationId(entity.getBundleGroup()))")
  @Mapping(target = "organisationName", expression = "java(toDtoOrganizationName(entity.getBundleGroup()))")
  @Mapping(target = "name", expression = "java(toDtoName(entity.getBundleGroup()))")
  @Mapping(target = "lastUpdate", expression = "java(toDtoLastUpdated(entity))")
  @Mapping(target = "categories", expression = "java(toDtoCategories(entity.getBundleGroup()))")
  @Mapping(source = "id", target = "bundleGroupVersionId", qualifiedByName = "toBundleGroupVersionId")
  BundleGroupVersionDto toDto(BundleGroupVersion entity);

  // Helper to make sure the bundleGroupVersionId is not set
  @Named("ununsedAnnotation")
  default BundleGroupVersionDto toViewDto(BundleGroupVersion entity) {
    BundleGroupVersionDto dto = toDto(entity);

    dto.setBundleGroupVersionId(null);
    return dto;
  }

  default BundleGroup toEntityBundleGroup(BundleGroup bundleGroup) {
    return bundleGroup;
  }

  default List<Long> toDtoBundles(Set<Bundle> bundles) {
    return bundles != null && !bundles.isEmpty() ? bundles.stream()
      .map(Bundle::getId).collect(Collectors.toList())
      : null;
  }

  default String toDtoBundleGroupId(BundleGroup group) {
    return (group != null) ? group.getId().toString() : null;
  }

  default Long toDtoOrganizationId(BundleGroup group) {
    return (group != null && group.getOrganisation() != null) ?
      group.getOrganisation().getId() : null;
  }

  default String toDtoOrganizationName(BundleGroup group) {
    return (group != null && group.getOrganisation() != null) ?
      group.getOrganisation().getName() : null;
  }

  default String toDtoName(BundleGroup group) {
    return group != null ? group.getName() : null;
  }

  default LocalDateTime toDtoLastUpdated(BundleGroupVersion entity) {
    return entity.getLastUpdated();
  }

  default List<String> toDtoCategories(BundleGroup group) {
    return (group != null && group.getCategories() != null && !group.getCategories().isEmpty()) ?
      group.getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList()) :
      null;
  }

  @Named("toBundleGroupVersionId")
  default String toBundleGroupVersionId(Long id) {
    return id != null? id.toString() : null;
  }

  default BundleGroupVersionOutDto stika(BundleGroupVersion entity) {
    return null;
  }

}
