package com.entando.hub.catalog.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.rest.KeycloakUserController.RestUserRepresentation;
import com.entando.hub.catalog.rest.model.PortalUserResponseView;
import com.entando.hub.catalog.rest.model.UserOrganisationRequest;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.model.UserRepresentation;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class PortalUserControllerTest {
	@InjectMocks
	PortalUserController portalUserController;
	@Mock
	PortalUserService portalUserService;
	@Before
	public void setup() {}
	
	@Test
	public void testGetUsers() {
		List<PortalUser> portalUserList = new ArrayList<>();
		List<UserRepresentation> userRepresentationList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
        portalUser.setId(5001L);
		portalUser.setUsername("admin");
		portalUser.setEmail("admin.123@test.co.in");
		portalUserList.add(portalUser);
		String organisationId = "2001";
		Mockito.when(portalUserService.getUsersByOrganisation(organisationId)).thenReturn(userRepresentationList);
		ResponseEntity<List<RestUserRepresentation>> portalUserListResult = portalUserController.getUsers(organisationId);
		assertNotNull(portalUserListResult);
		assertEquals(HttpStatus.OK,portalUserListResult.getStatusCode());
	}
	
	@Test
	public void TestAddUserToOrganisation() {
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
		UserOrganisationRequest userOrganisationRequest = new UserOrganisationRequest();
        portalUser.setId(5001L);
		portalUser.setUsername("admin");
		portalUser.setEmail("admin.123@test.co.in");
		portalUserList.add(portalUser);
		String organisationId = "2001";
		String username =  userOrganisationRequest.getUsername();
		Mockito.when(portalUserService.addUserToOrganization(organisationId, username)).thenReturn(Boolean.TRUE);
		ResponseEntity<Map<String, Boolean>> portalUserAddResult = portalUserController.addUserToOrganisation(organisationId, userOrganisationRequest);
		assertNotNull(portalUserAddResult);
		assertEquals(HttpStatus.OK,portalUserAddResult.getStatusCode());
	}
	
	@Test
	public void testDeleteUserFromOrganisation() {
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
		UserOrganisationRequest userOrganisationRequest = new UserOrganisationRequest();
        portalUser.setId(5001L);
		portalUser.setUsername("admin");
		portalUser.setEmail("admin.123@test.co.in");
		portalUserList.add(portalUser);
		String organisationId = "2001";
		String username =  userOrganisationRequest.getUsername();
		Mockito.when(portalUserService.removeUserFromOrganization(username, organisationId)).thenReturn(Boolean.TRUE);
		ResponseEntity<Map<String, Boolean>> deleteUserFromOrganisationResult = portalUserController.deleteUserFromOrganisation(organisationId, username);
		assertNotNull(deleteUserFromOrganisationResult);
		assertEquals(HttpStatus.OK,deleteUserFromOrganisationResult.getStatusCode());
	}
	@Test
	public void TestDeleteUser()
	{
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
		UserOrganisationRequest userOrganisationRequest = new UserOrganisationRequest();
        portalUser.setId(5001L);
		portalUser.setUsername("admin");
		portalUser.setEmail("admin.123@test.co.in");
		portalUserList.add(portalUser);
		String username =  userOrganisationRequest.getUsername();
		Mockito.when(portalUserService.removeUser(username)).thenReturn(Boolean.TRUE);
		ResponseEntity<Map<String, Boolean>> deleteUserResult = portalUserController.deleteUser(username);
		assertNotNull(deleteUserResult);
		assertEquals(HttpStatus.OK,deleteUserResult.getStatusCode());
	}
	
	@Test
	public void testGetUsersFails() {
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
        portalUser.setId(5001L);
		portalUser.setUsername("admin");
		portalUser.setEmail("admin.123@test.co.in");
		portalUserList.add(portalUser);
		String organisationId = "2001";
		Mockito.when(portalUserService.getUsersByOrganisation(organisationId)).thenReturn(null);
		ResponseEntity<List<RestUserRepresentation>> portalUserListResult = portalUserController.getUsers(organisationId);
		assertNotNull(portalUserListResult);
		assertEquals(HttpStatus.NOT_FOUND,portalUserListResult.getStatusCode());
	}
	
	@Test 
	public void getPortalUserByUsername() {
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUserResponseView portalUserResponseView = new PortalUserResponseView();
		PortalUser portalUser = new PortalUser();
        portalUser.setId(5001L);
		portalUser.setUsername("admin");
		portalUser.setEmail("admin.123@test.co.in");
		portalUserList.add(portalUser);
		String username = portalUser.getUsername();
		Mockito.when(portalUserService.getUserByUsername(username)).thenReturn(portalUserResponseView);
		ResponseEntity<PortalUserResponseView> portalUserByNameResult = portalUserController.getPortalUserByUsername(username);
		assertNotNull(portalUserByNameResult);
		assertEquals(HttpStatus.OK,portalUserByNameResult.getStatusCode());
	}
	
	@Test
	public void getPortalUserByUsernameFails() {
		List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
        portalUser.setId(5001L);
		portalUser.setUsername("admin");
		portalUser.setEmail("admin.123@test.co.in");
		portalUserList.add(portalUser);
		String username = portalUser.getUsername();
		Mockito.when(portalUserService.getUserByUsername(username)).thenReturn(null);
		ResponseEntity<PortalUserResponseView> portalUserByNameResult = portalUserController.getPortalUserByUsername(username);
		assertNotNull(portalUserByNameResult);
		assertEquals(HttpStatus.NO_CONTENT,portalUserByNameResult.getStatusCode());
	}
}
