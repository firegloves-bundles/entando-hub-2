package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.domain.BundleGroupDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface BundleGroupMapper extends BaseMapper<BundleGroup, BundleGroupDto> {

  // NOTE: the categories are not converted
  @Mapping(source = "bundleGroupId", target = "id", qualifiedByName = "toEntityID")
  @Mapping(target = "organisation", expression = "java(toOrganization(dto.getOrganisationName(), dto.getOrganisationId()))")
  BundleGroup toEntity(BundleGroupDto dto);


  @Mapping(source = "id", target = "bundleGroupId", qualifiedByName = "toDtoID")
  @Mapping(target = "organisationId", expression = "java(fromOrganizationId(entity.getOrganisation()))")
  @Mapping(target = "organisationName", expression = "java(fromOrganizationName(entity.getOrganisation()))")
  BundleGroupDto toDto(BundleGroup entity);

  @Named("toEntityID")
  static Long toEntityId(String value) {
    if (StringUtils.isNotBlank(value)) {
      return Long.parseLong(value);
    }
    return null;
  }

  @Named("toDtoID")
  static String fromEntityId(Long value) {
    return value != null ? value.toString() : null;
  }

  default Organisation toOrganization(String name, String id) {
    Organisation org = null;

    if (StringUtils.isNotBlank(id)) { // NOTE: we inherit the behavior, no check on name
      org = new Organisation();

      org.setId(Long.parseLong(id));
      org.setName(name);
    }
    return org;
  }

  default String fromOrganizationId(Organisation organisation) {
    return (organisation != null && organisation.getId() != null) ? organisation.getId().toString() : null;
  }

  default String fromOrganizationName(Organisation organisation) {
    return (organisation != null) ? organisation.getName() : null;
  }

  static Set<Category> toCategories(List<String> value) {
    System.out.println("-- toCategories --");
    return null;
  }

  static List<String> fromCategories(Set<Category> categories) {
    System.out.println("-- fromCategories --");
    if (!categories.isEmpty()) {
      return categories.stream().map(c -> c.getId().toString()).filter(i -> StringUtils.isNotBlank(i))
        .collect(Collectors.toList());
    }
    return null;
  }

}
