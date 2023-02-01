package com.entando.hub.catalog.rest.model;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PortalUserResponseView {

	private long id;

    @Schema(example = "admin")
    private String username;

    @Schema(example = "Admin")
    private String firstName;

    @Schema(example = "Administrator")
    private String lastName;

    @Schema(example = "admin@localhost")
    private String email;
    private Set<OrganisationResponseView> organisations;
	
}
