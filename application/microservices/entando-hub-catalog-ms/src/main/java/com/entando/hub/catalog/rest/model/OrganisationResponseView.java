package com.entando.hub.catalog.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrganisationResponseView {
	
    private Long organisationId;

    @Schema(example = "Entando")
    private String organisationName;

    @Schema(example = "Application Composition Platform for Kubernetes")

    private String organisationDescription;


}
