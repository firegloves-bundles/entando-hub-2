package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.service.dto.BundleEntityDto;
import com.entando.hub.catalog.service.mapper.inclusion.BundleEntityMapper;
import com.entando.hub.catalog.service.mapper.inclusion.BundleStandardMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses={BundleStandardMapper.class, BundleEntityMapper.class})
public abstract class BundleMapper {

    @Autowired
    private BundleStandardMapper bundleStandardMapper;
    @Autowired
    private BundleEntityMapper bundleEntityMapper;

    public BundleDto toDto(Bundle entity) {
        return bundleStandardMapper.toDto(entity);
    }

    public Bundle toEntity(BundleDto dto) {
        return bundleStandardMapper.toEntity(dto);
    }

    public BundleEntityDto toEntityDto(Bundle entity) {
        return bundleEntityMapper.toDto(entity);
    }

}
