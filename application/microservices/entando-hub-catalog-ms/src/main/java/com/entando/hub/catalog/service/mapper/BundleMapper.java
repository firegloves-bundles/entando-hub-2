package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface BundleMapper  extends BaseMapper<Bundle, BundleDto> {


  @Mapping(source = "bundleGroups", target = "bundleGroupVersions", qualifiedByName = "toEntityGroups")
  @Mapping(source = "bundleId", target = "id", qualifiedByName = "toEntityID")
  Bundle toEntity(BundleDto dto);

  @Mapping(source = "id", target = "bundleId", qualifiedByName = "toDtoID")
  @Mapping(source = "bundleGroupVersions", target = "bundleGroups", qualifiedByName = "toDtoGroups")
  BundleDto toDto(Bundle dto);

  default String toDependencies(List<String> dependencies) {
    if (!CollectionUtils.isEmpty(dependencies)) {
      return String.join(",", dependencies);
    }
    return null;
  }

  default List<String> fromDependencies(String value) {
    return StringUtils.isNotBlank(value) ? Arrays.asList(value.split(",")) : null;
  }

  default DescriptorVersion toDescriptorVersion(String value) {
    if (StringUtils.isNotBlank(value)) {
      switch (value) {
        case "V5":
          return DescriptorVersion.V5;
      }
    }
    return DescriptorVersion.V1;
  }

  default String fromDescriptorVersion(DescriptorVersion version) {
    return (version != null) ? version.toString() : DescriptorVersion.V1.toString();
  }

  @Named("toEntityGroups")
  static Set<BundleGroupVersion> toEntityGroups(List<String> groups) {
    if (groups != null && !CollectionUtils.isEmpty(groups)) {
      return groups.stream().map((bundleGroupVersionId) -> {
          BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
          bundleGroupVersion.setId(Long.valueOf(bundleGroupVersionId));
          return bundleGroupVersion;
        })
        .filter(g -> g != null)
        .collect(Collectors.toSet());
    }
    return null;
  }

  @Named("toDtoGroups")
  static List<String> toDtoGroups(Set<BundleGroupVersion> bundleGroupVersions) {
    return (bundleGroupVersions != null && !bundleGroupVersions.isEmpty()) ?
      bundleGroupVersions
        .stream()
        .map(bundleGroupVersion -> bundleGroupVersion.getId().toString())
        .collect(Collectors.toList()) : null;
  }

  @Named("toEntityID")
  static Long idConvert(String value) {
    if (StringUtils.isNotBlank(value)) {
      return Long.parseLong(value);
    }
    return null;
  }

  @Named("toDtoID")
  static String fromEntityId(Long value) {
    return value != null ? value.toString() : null;
  }


}
