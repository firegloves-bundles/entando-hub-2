package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.dto.BundleGroupDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface BundleGroupMapper extends BaseStandardMapper<BundleGroup, BundleGroupDto> {

  // NOTE: the categories are not converted
  @Mapping(source = "bundleGroupId", target = "id", qualifiedByName = "toEntityId")
  @Mapping(target = "organisation", expression = "java(toOrganization(dto.getOrganisationName(), dto.getOrganisationId()))")
  BundleGroup toEntity(BundleGroupDto dto);


  @Mapping(source = "id", target = "bundleGroupId", qualifiedByName = "toDtoId")
  @Mapping(target = "organisationId", expression = "java(fromOrganizationId(entity.getOrganisation()))")
  @Mapping(target = "organisationName", expression = "java(fromOrganizationName(entity.getOrganisation()))")
  BundleGroupDto toDto(BundleGroup entity);

  default Organisation toOrganization(String name, Long id) {
    Organisation org = new Organisation();

    org.setId(id);
    org.setName(name);
    return org;
  }

  default Long fromOrganizationId(Organisation organisation) {
    return (organisation != null && organisation.getId() != null) ? organisation.getId() : null;
  }

  default String fromOrganizationName(Organisation organisation) {
    return (organisation != null) ? organisation.getName() : null;
  }

  static Set<Category> toCategories(List<String> value) {
    return null;
  }

  static List<String> fromCategories(Set<Category> categories) {
    if (categories != null && !categories.isEmpty()) {
      return categories.stream().map(c -> c.getId().toString()).filter(i -> StringUtils.isNotBlank(i))
        .collect(Collectors.toList());
    }
    return null;
  }

}
