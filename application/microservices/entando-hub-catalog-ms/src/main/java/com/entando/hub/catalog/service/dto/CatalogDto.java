package com.entando.hub.catalog.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CatalogDto {
    protected Long id;
    protected Long organisationId;
    @Schema(example = "Entando Catalog")
    protected String name;

    public CatalogDto(Long id, Long organisationId, String name) {
        this.id = id;
        this.organisationId = organisationId;
        this.name = name;
    }

}