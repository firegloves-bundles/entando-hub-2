package com.entando.hub.catalog.service.mapper.inclusion;


import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.service.mapper.inclusion.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BundleStandardMapper extends BaseMapper<Bundle, BundleDto> {


  @Mapping(source = "bundleGroups", target = "bundleGroupVersions", qualifiedByName = "toEntityGroups")
  @Mapping(source = "bundleId", target = "id", qualifiedByName = "toEntityId")
  @Mapping(target = "descriptorVersion", expression = "java(toDescriptorVersion(dto.getGitRepoAddress()))")
  Bundle toEntity(BundleDto dto);

  @Mapping(source = "id", target = "bundleId", qualifiedByName = "toDtoId")
  @Mapping(source = "bundleGroupVersions", target = "bundleGroups", qualifiedByName = "toDtoGroups")
  BundleDto toDto(Bundle entity);

  default String toDependencies(List<String> dependencies) {
    if (dependencies != null && !CollectionUtils.isEmpty(dependencies)) {
      return String.join(",", dependencies);
    }
    return ""; // EHUB-296 - retain behaviour
  }

  default List<String> fromDependencies(String value) {
    return StringUtils.isNotBlank(value) ? Arrays.asList(value.split(",")) : Arrays.asList("") ; // EHUB-296 - retain behaviour
  }

  default DescriptorVersion toDescriptorVersion(String value) {
    if (StringUtils.isNotBlank(value) && value.startsWith("docker:")) {
      return DescriptorVersion.V5;
    }
    return DescriptorVersion.V1;
  }

  default String fromDescriptorVersion(DescriptorVersion version) {
    return (version != null) ? version.toString() : DescriptorVersion.V1.toString();
  }

  @Named("toEntityGroups")
  static Set<BundleGroupVersion> toEntityGroups(List<String> groups) {
    if (groups != null && !CollectionUtils.isEmpty(groups)) {
      return groups.stream()
              .filter(id -> StringUtils.isNotBlank(id))
              .map((bundleGroupVersionId) -> {
                BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();

                bundleGroupVersion.setId(Long.valueOf(bundleGroupVersionId));
                return bundleGroupVersion;
              })
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());
    }
    return new HashSet<>(); // EHUB-296 - retain behaviour
  }

  @Named("toDtoGroups")
  static List<String> toDtoGroups(Set<BundleGroupVersion> bundleGroupVersions) {
    return (bundleGroupVersions != null && !bundleGroupVersions.isEmpty()) ?
            bundleGroupVersions
                    .stream()
                    .map(bundleGroupVersion -> bundleGroupVersion.getId().toString())
                    .collect(Collectors.toList()) : Arrays.asList(); // EHUB-296 - retain behaviour
  }

}
