package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.service.dto.apikey.ApiKeyResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class PrivateCatalogApiKeyMapper {

    @Named( "toDto" )
    @Mapping(target = "apiKey", ignore = true)
    public abstract ApiKeyResponseDTO toApiKeyResponseDTO(PrivateCatalogApiKey privateCatalogApiKey);

    @Named( "toAddApiKeyDto" )
    @Mapping(target = "label", ignore = true)
    public abstract ApiKeyResponseDTO toAddApiKeyDto(PrivateCatalogApiKey privateCatalogApiKey);

    @Named( "toRefreshApiKeyDto" )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "label", ignore = true)
    public abstract ApiKeyResponseDTO toRefreshApiKeyDto(PrivateCatalogApiKey privateCatalogApiKey);

    @Named( "toApiKeyResponseDTOList" )
    @Mapping(target = "apiKey", ignore = true)
    public abstract List<ApiKeyResponseDTO> toApiKeyResponseDTO(List<PrivateCatalogApiKey> privateCatalogApiKey);


    @Named( "removeApiKey" )
    public List<ApiKeyResponseDTO> removeApiKey(List<ApiKeyResponseDTO> privateCatalogApiKey){
        return privateCatalogApiKey.stream().peek(f-> f.setApiKey(null)).collect(Collectors.toList());
    }

}
