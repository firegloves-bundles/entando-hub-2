package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface BaseMapper<E, D> {

  E toEntity(D dto);
  D toDto(E entity);

  List<E> toEntity(List<D> dtoList);
  List<D> toDto(List<E> entityList);

  @Named("toEntityId")
  static Long toEntityId(String value) {
    if (StringUtils.isNotBlank(value)) {
      return Long.parseLong(value);
    }
    return null;
  }

  @Named("toDtoId")
  static String toDtoCategoryId(Long id) {
    return (null != id) ? id.toString() : null;
  }

  @Named("toDtoBundleGroups")
  default List<String> toDtoBundleGroups(Set<BundleGroup> bundleGroups) {
    return (bundleGroups != null && !bundleGroups.isEmpty()) ?
      bundleGroups.stream().map(b -> b.getId().toString()).collect(Collectors.toList()) : null;
  }

}
