package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionEntityMapper;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionStandardMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This acts as a container for the two mappers that handle the same entity (gosh!)
 */
@Mapper(componentModel = "spring", uses={BundleGroupVersionEntityMapper.class, BundleGroupVersionStandardMapper.class})
public abstract class BundleGroupVersionMapper {

    @Autowired
    private BundleGroupVersionStandardMapper bundleGroupVersionStandardMapper;
    @Autowired
    private BundleGroupVersionEntityMapper bundleGroupVersionEntityMapper;

    public BundleGroupVersionDto toDto(BundleGroupVersion entity) {
        return bundleGroupVersionStandardMapper.toDto(entity);
    }

    public BundleGroupVersion toEntity(BundleGroupVersionDto dto) {
        return bundleGroupVersionStandardMapper.toEntity(dto);
    }

    public BundleGroupVersion toEntity(BundleGroupVersionDto dto, BundleGroup bundleGroup) {
        return bundleGroupVersionStandardMapper.toEntity(dto, bundleGroup);
    }

    public BundleGroupVersionDto toViewDto(BundleGroupVersion entity) {
        return bundleGroupVersionStandardMapper.toViewDto(entity);
    }

    public BundleGroupVersionEntityDto toEntityDto(BundleGroupVersion bundleGroupVersion) {
        return bundleGroupVersionEntityMapper.toDto(bundleGroupVersion);
    }
}
