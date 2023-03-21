package com.entando.hub.catalog.service.mapper.inclusion;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import com.entando.hub.catalog.service.mapper.BaseStandardMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BundleGroupVersionEntityMapper extends BaseStandardMapper<BundleGroupVersion, BundleGroupVersionEntityDto> {

    BundleGroupVersion toEntity(BundleGroupVersionEntityDto dto);

    BundleGroupVersionEntityDto toDto(BundleGroupVersion entity);

}
