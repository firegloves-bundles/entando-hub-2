package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.dto.OrganisationDto;
import com.entando.hub.catalog.service.mapper.inclusion.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface OrganizationMapper extends BaseMapper<Organisation, OrganisationDto> {

  @Mapping(source = "organisationId", target = "id", qualifiedByName = "toEntityId")
  @Mapping(target = "bundleGroups", ignore = true)
  Organisation toEntity(OrganisationDto dto);

  @Mapping(source = "id", target = "organisationId", qualifiedByName = "toDtoId")
  @Mapping(source = "bundleGroups", target = "bundleGroups", qualifiedByName = "toDtoBundleGroups")
  OrganisationDto toDto(Organisation entity);

}
