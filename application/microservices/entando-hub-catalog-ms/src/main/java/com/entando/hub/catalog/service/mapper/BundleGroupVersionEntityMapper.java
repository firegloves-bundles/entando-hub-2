package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionOutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BundleGroupVersionEntityMapper extends BaseMapper<BundleGroupVersion, BundleGroupVersionOutDto> {

    BundleGroupVersion toEntity(BundleGroupVersionOutDto dto);

    BundleGroupVersionOutDto toDto(BundleGroupVersion entity);

}
