package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.domain.BundleGroupVersionDto;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {})
public interface BundleGroupVersionMapper extends BaseMapper<BundleGroupVersion, BundleGroupVersionDto> {

  @Override
  @Mapping(source = "bundleGroupVersionId", target = "id", qualifiedByName = "toEntityID")
  @Mapping(target = "bundles", ignore = true)
  BundleGroupVersion toEntity(BundleGroupVersionDto dto);

  @Mapping(source = "dto.bundleGroupVersionId", target = "id", qualifiedByName = "toEntityID")
  @Mapping(target = "dto.bundleGroup", expression = "java(toEntityBundleGroup(bundleGroup))")
  @Mapping(source = "dto.version", target = "version")
  @Mapping(target = "bundles", ignore = true)
  BundleGroupVersion toEntity(BundleGroupVersionDto dto, BundleGroup bundleGroup);

  @Override
  @Mapping(target = "children", expression = "java(toDtoBundles(entity.getBundles()))")
  @Mapping(source = "id", target = "bundleGroupId", qualifiedByName = "toDtoID")
  @Mapping(target = "bundles", ignore = true)
  BundleGroupVersionDto toDto(BundleGroupVersion entity);

  @Named("toEntityID")
  static Long toEntityId(String value) {
    if (StringUtils.isNotBlank(value)) {
      return Long.parseLong(value);
    }
    return null;
  }

  default BundleGroup toEntityBundleGroup(BundleGroup bundleGroup) {
    return bundleGroup;
  }

  @Named("toDtoID")
  static String fromEntityId(Long value) {
    return value != null ? value.toString() : null;
  }

  default List<Long> toDtoBundles(Set<Bundle> bundles) {
    throw new NotImplementedException();
  }

}
