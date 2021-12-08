package com.entando.hub.catalog.rest.model;

import java.util.Set;

import lombok.Data;

@Data
public class PortalUserResponseView {

	private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<OrganisationResponseView> organisations;
	
}
