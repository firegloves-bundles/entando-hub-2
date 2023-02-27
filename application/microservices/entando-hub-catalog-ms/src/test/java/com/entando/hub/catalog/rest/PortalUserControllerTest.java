package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.entando.hub.catalog.service.security.SecurityHelperService;
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

import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.rest.model.PortalUserResponseView;
import com.entando.hub.catalog.rest.model.UserOrganisationRequest;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser(username="admin",roles={ADMIN})
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(PortalUserController.class)
public class PortalUserControllerTest {
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	private MockMvc mockMvc;
	@InjectMocks
	PortalUserController portalUserController;
	@MockBean
	PortalUserService portalUserService;
	@MockBean
	SecurityHelperService securityHelperService;
	private final Long ID = 1001L;
	private final String USERNAME = "Admin";
	private final String EMAIL = "admin.123@test.co.in";
	private final String ORGANISATIONID = "2001";

	@Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

	@Test
	public void testGetUsers()throws Exception {
		List<PortalUser> portalUserList = new ArrayList<>();
		List<UserRepresentation> userRepresentationList = new ArrayList<>();
		PortalUser portalUser = populatePortalUser();
		portalUserList.add(portalUser);
		Mockito.when(portalUserService.getUsersByOrganisation(ORGANISATIONID)).thenReturn(userRepresentationList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/").accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	@Test
	public void testGetUsersFails() throws Exception{
		List<PortalUser> portalUserList = new ArrayList<>();
		List<UserRepresentation> userRepresentationList = new ArrayList<>();
		PortalUser portalUser = populatePortalUser();
		portalUserList.add(portalUser);
		Mockito.when(portalUserService.getUsersByOrganisation(null)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(ORGANISATIONID))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

	@Test 
	public void getPortalUserByUsername() throws Exception {
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUserResponseView portalUserResponseView = new PortalUserResponseView();
		PortalUser portalUser = populatePortalUser();
		portalUserList.add(portalUser);
		String username = portalUser.getUsername();
		Mockito.when(securityHelperService.getContextAuthenticationUsername()).thenReturn(username);
		Mockito.when(portalUserService.getUserByUsername(username)).thenReturn(portalUserResponseView);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/details",username)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}

	@Test
	public void getPortalUserByUsernameFails() throws Exception{
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUserResponseView portalUserResponseView = null;
		PortalUser portalUser = populatePortalUser();
		portalUserList.add(portalUser);
		String username = portalUser.getUsername();
		Mockito.when(portalUserService.getUserByUsername(username)).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/details", username)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	public void TestAddUserToOrganisation() throws Exception{
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = populatePortalUser();
		UserOrganisationRequest userOrganisationRequest = new UserOrganisationRequest();
		userOrganisationRequest.setUsername(USERNAME);
		portalUserList.add(portalUser);
		String username =  userOrganisationRequest.getUsername();
		String inputJson = mapToJson(userOrganisationRequest);
		Mockito.when(portalUserService.addUserToOrganization(username,ORGANISATIONID)).thenReturn(Boolean.TRUE);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/users/{organisationId}",ORGANISATIONID)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
			      .andExpect(status().isOk())
			      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
	}

	@Test
	public void TestDeleteUser() throws Exception
	{
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
		UserOrganisationRequest userOrganisationRequest = new UserOrganisationRequest();
		userOrganisationRequest.setUsername(USERNAME);
		portalUserList.add(portalUser);
		String username =  userOrganisationRequest.getUsername();
		Mockito.when(portalUserService.removeUser(username)).thenReturn(Boolean.TRUE);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{username}",username).accept(MediaType.APPLICATION_JSON_VALUE))
	      .andExpect(status().isOk())
	      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	@Test
	public void testDeleteUserFromOrganisation() throws Exception {
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
		UserOrganisationRequest userOrganisationRequest = new UserOrganisationRequest();
		userOrganisationRequest.setUsername(USERNAME);
		portalUserList.add(portalUser);
		String username =  userOrganisationRequest.getUsername();
		Mockito.when(portalUserService.removeUserFromOrganization(username, ORGANISATIONID)).thenReturn(Boolean.TRUE);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{organisationId}/user/{username}",username,ORGANISATIONID).accept(MediaType.APPLICATION_JSON_VALUE))
	      .andExpect(status().isOk())
	      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	}

	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	private PortalUser populatePortalUser() {
		PortalUser portalUser = new PortalUser();
        portalUser.setId(ID);
		portalUser.setUsername(USERNAME);
		portalUser.setEmail(EMAIL);
		return portalUser;
	}
}