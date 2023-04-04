package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.rest.dto.RestUserRepresentationDto;
import com.entando.hub.catalog.rest.model.SearchKeycloackUserRequest;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles={ADMIN})
public class KeycloakUserControllerTest {

    @Autowired
    private MockMvc mockMvc;
	@MockBean
	KeycloakService keyCloakService;
	private static final String URI = "/api/keycloak/users/";
	private final String USERNAME = "Admin";
	private final String EMAIL = "admin.123@test.co.in";
	private final String FIRSTNAME = "Admin";
	private final String LASTNAME = "admin";
	private final String ID = "9001";
    private final Long CREATEDTIMESTAMP = 90019L;
    private final String EMAILVERIFIED = "admin.123@test.co.in";
    private final String ORGID = "2001";

	@Test
	public void testSearchUsers() throws Exception{
		UserRepresentation user = populateUserRepresentation();
		SearchKeycloackUserRequest request = populateSearchKeycloackUserRequest();
        List<UserRepresentation> userRepresentationList = new ArrayList<>();
        userRepresentationList.add(user);
        Map<String, String> map = new HashMap<>();
        String inputJson = mapToJson(request);
        Mockito.when(keyCloakService.searchUsers(map)).thenReturn(userRepresentationList);
       mockMvc.perform(MockMvcRequestBuilders.get(URI)
        		.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(jsonPath("$.[*].id").value(user.getId()))
		        .andExpect(jsonPath("$.[*].username").value(user.getUsername()));

	}

	@Test
	public void testGetUser() throws Exception {
		UserRepresentation user = populateUserRepresentation();
	    String username=user.getUsername();
		RestUserRepresentationDto restUserRepresentation = populateRestUserRepresentation();
		Mockito.when(keyCloakService.getUser(restUserRepresentation.getUsername())).thenReturn(user);
		mockMvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON_VALUE).content(username))
	      .andExpect(status().is(HttpStatus.OK.value()))
	      
	      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}
	
	@Test
	public void testGetUserFails() throws Exception{
		UserRepresentation user = populateUserRepresentation();
	    String username=user.getUsername();
		Mockito.when(keyCloakService.getUser(null)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.get(URI + username)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(username))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		
	}

	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	private UserRepresentation populateUserRepresentation()
	{
		UserRepresentation user = new UserRepresentation();
		user.setId(ID);
		user.setCreatedTimestamp(CREATEDTIMESTAMP);
	    user.setUsername(USERNAME);
	    user.setEnabled(true);
	    user.setTotp(true);
	    user.setEmailVerified(EMAILVERIFIED);
	    user.setFirstName(FIRSTNAME);
	    user.setLastName(LASTNAME);
	    user.setEmail(EMAIL);
		
		return user;
	}

	private RestUserRepresentationDto populateRestUserRepresentation() {
	   
		    Set<String> organisationIds = new HashSet<String>();
	        organisationIds.add(ORGID);
	        organisationIds.add(ORGID);
      		RestUserRepresentationDto restUserRepresentation = new RestUserRepresentationDto(populateUserRepresentation());
		    restUserRepresentation.setId(ID);
	        restUserRepresentation.setFirstName(FIRSTNAME);
	        restUserRepresentation.setLastName(LASTNAME);
	        restUserRepresentation.setUsername(USERNAME);
	        restUserRepresentation.setEmail(EMAIL);
	        restUserRepresentation.setOrganisationIds(organisationIds);
	        restUserRepresentation.setEnabled(true);
		    return restUserRepresentation;
	}
	
	private SearchKeycloackUserRequest populateSearchKeycloackUserRequest() {
		
		SearchKeycloackUserRequest request = new SearchKeycloackUserRequest();
		request.setFirstName(FIRSTNAME);
		request.setLastName(LASTNAME);
		request.setUsername(USERNAME);
		request.setEmail(EMAIL);
		return request;
	}
}