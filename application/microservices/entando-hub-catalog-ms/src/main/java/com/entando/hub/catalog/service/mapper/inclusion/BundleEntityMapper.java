package com.entando.hub.catalog.service.mapper.inclusion;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.rest.dto.BundleEntityDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BundleEntityMapper {
    Bundle toEntity(BundleEntityDto dto);
    BundleEntityDto toDto(Bundle entity);

}
