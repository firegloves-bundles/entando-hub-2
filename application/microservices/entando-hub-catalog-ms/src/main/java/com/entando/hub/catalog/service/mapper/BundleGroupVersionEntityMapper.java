package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BundleGroupVersionEntityMapper extends BaseMapper<BundleGroupVersion, BundleGroupVersionEntityDto> {

    BundleGroupVersion toEntity(BundleGroupVersionEntityDto dto);

    BundleGroupVersionEntityDto toDto(BundleGroupVersion entity);

}
