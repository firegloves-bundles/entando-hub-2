package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.entando.hub.catalog.rest.KeycloakUserController.RestUserRepresentation;
import com.entando.hub.catalog.rest.model.SearchKeycloackUserRequest;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser(username="admin",roles={ADMIN})
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(KeycloakUserController.class)
public class KeycloakUserControllerTest {

	@Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
	KeycloakUserController keyCloakUserController;
	@MockBean
	KeycloakService keyCloakService;
	
	private final String USERNAME = "Admin";
	private final String EMAIL = "admin.123@test.co.in";
	private final String FIRSTNAME = "Admin";
	private final String LASTNAME = "admin";

	@Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

	@Test
	public void testSearchUsers() throws Exception{
		UserRepresentation user = populateUserRepresentation();
	    RestUserRepresentation restUserRepresentation = populateRestUserRepresentation();
		SearchKeycloackUserRequest request = populateSearchKeycloackUserRequest();
        List<UserRepresentation> userRepresentationList = new ArrayList<>();
        userRepresentationList.add(user);
        Map<String, String> map = new HashMap<>();
        String inputJson = mapToJson(request);
        Mockito.when(keyCloakService.searchUsers(map)).thenReturn(userRepresentationList);
       mockMvc.perform(MockMvcRequestBuilders.get("/api/keycloak/users")
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
		RestUserRepresentation restUserRepresentation = populateRestUserRepresentation();
		Mockito.when(keyCloakService.getUser(restUserRepresentation.getUsername())).thenReturn(user);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/keycloak/users/").accept(MediaType.APPLICATION_JSON_VALUE).content(username))
	      .andExpect(status().is(HttpStatus.OK.value()))
	      
	      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}
	
	@Test
	public void testGetUserFails() throws Exception{
		UserRepresentation user = populateUserRepresentation();
	    String username=user.getUsername();
		RestUserRepresentation restUserRepresentation = populateRestUserRepresentation();
		Mockito.when(keyCloakService.getUser(null)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/keycloak//users/{username}",username)
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
		user.setId("9001");
		user.setCreatedTimestamp(90019);
	    user.setUsername("admin");
	    user.setEnabled(true);
	    user.setTotp(true);
	    user.setEmailVerified("xyz.mail.com");
	    user.setFirstName("entando");
	    user.setLastName("hub");
	    user.setEmail("xyz.mail.com");
		
		return user;
	}

	private RestUserRepresentation populateRestUserRepresentation() {
	   
		    Set<String> organisationIds = new HashSet<String>();
	        organisationIds.add("1001");
	        organisationIds.add("9001");
      		RestUserRepresentation restUserRepresentation = new RestUserRepresentation(populateUserRepresentation());
		    restUserRepresentation.setId("1001");
	        restUserRepresentation.setFirstName("entando");
	        restUserRepresentation.setLastName("new");
	        restUserRepresentation.setUsername("entandohub");
	        restUserRepresentation.setEmail("entando.hub@entando.com");
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