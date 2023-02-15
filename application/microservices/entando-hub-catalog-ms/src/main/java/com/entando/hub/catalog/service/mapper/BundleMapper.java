package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleDtoIn;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface BundleMapper  extends BaseMapper<Bundle, BundleDtoIn> {


  @Mapping(source = "bundleGroups", target = "bundleGroupVersions", qualifiedByName = "toEntityGroups")
  @Mapping(source = "bundleId", target = "id", qualifiedByName = "toEntityID")
  Bundle toEntity(BundleDtoIn dto);

  BundleDtoIn toDto(Bundle dto);


  default String toDependencies(List<String> dependencies) {
    if (!CollectionUtils.isEmpty(dependencies)) {
      return String.join(",", dependencies);
    }
    return null;
  }

  default List<String> fromDependencies(String value) {
    throw new NotImplementedException("must be implemented!");
//    return null;
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

  @Named("toEntityGroups")
  static Set<BundleGroupVersion> groupConvert(List<String> groups) {
    if (!CollectionUtils.isEmpty(groups)) {
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

  @Named("toEntityID")
  static Long idConvert(String value) {
    if (StringUtils.isNotBlank(value)) {
      return Long.parseLong(value);
    }
    return null;
  }


}
