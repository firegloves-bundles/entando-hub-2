package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.rest.dto.apikey.GetApiKeyResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface PrivateCatalogApiKeyMapper {

    @Mapping(target = "username", expression = "java(getUsername(privateCatalogApiKey.getPortalUser()))")
    GetApiKeyResponseDTO toDto(PrivateCatalogApiKey privateCatalogApiKey);

    List<GetApiKeyResponseDTO> toDto(List<PrivateCatalogApiKey> privateCatalogApiKey);

    default String getUsername(PortalUser portalUser) {
        return portalUser.getUsername();
    }
}
