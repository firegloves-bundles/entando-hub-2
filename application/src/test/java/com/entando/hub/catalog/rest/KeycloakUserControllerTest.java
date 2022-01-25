package com.entando.hub.catalog.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.entando.hub.catalog.rest.model.SearchKeycloackUserRequest;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.model.UserRepresentation;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class KeycloakUserControllerTest {
	@InjectMocks
	KeycloakUserController keyCloakUserController;
	@Mock
	KeycloakService keyCloakService;
	@Before
	public void setup() {}
	
	@Test
	public void testSearchUsers() {
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
	    Set<String> organisationIds = new HashSet<String>();
	    organisationIds.add("1001");
	    organisationIds.add("9001");
	    RestUserRepresentation restUserRepresentation = new RestUserRepresentation(user);
        restUserRepresentation.setId("1001");
        restUserRepresentation.setFirstName("entando");
        restUserRepresentation.setLastName("new");
        restUserRepresentation.setUsername("entandohub");
        restUserRepresentation.setEmail("entando.hub@entando.com");
        restUserRepresentation.setOrganisationIds(organisationIds);
        restUserRepresentation.setEnabled(true);
		SearchKeycloackUserRequest request = new SearchKeycloackUserRequest();
		request.setFirstName("admin");
		request.setLastName("user");
		request.setUsername("username");
		request.setEmail("test.com");
        List<UserRepresentation> userRepresentationList = new ArrayList<>();
        userRepresentationList.add(user);
        Map<String, String> map = request.getParams();
        Mockito.when(keyCloakService.searchUsers(map)).thenReturn(userRepresentationList);
        List<RestUserRepresentation> searchUsersResult = keyCloakUserController.searchUsers(request);
        assertNotNull(searchUsersResult);
         assertEquals(userRepresentationList.get(0).getId(),searchUsersResult.get(0).getId());
	}
	
	@Test
	public void testSearchUsersFails() {
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
	    Set<String> organisationIds = new HashSet<String>();
	    organisationIds.add("1001");
	    organisationIds.add("9001");
	    RestUserRepresentation restUserRepresentation = new RestUserRepresentation(user);
        restUserRepresentation.setId("1001");
        restUserRepresentation.setFirstName("entando");
        restUserRepresentation.setLastName("new");
        restUserRepresentation.setUsername("entandohub");
        restUserRepresentation.setEmail("entando.hub@entando.com");
        restUserRepresentation.setOrganisationIds(organisationIds);
        restUserRepresentation.setEnabled(true);
		SearchKeycloackUserRequest request = new SearchKeycloackUserRequest();
		request = null;
        List<UserRepresentation> userRepresentationList = new ArrayList<>();
        userRepresentationList.add(user);
		Map<String, String> map = new HashMap<>();
        Mockito.when(keyCloakService.searchUsers(null)).thenReturn(userRepresentationList);
        List<RestUserRepresentation> searchUsersResult = keyCloakUserController.searchUsers(request);
        assertNotNull(searchUsersResult);
        assertEquals(map.isEmpty(),searchUsersResult.isEmpty());
	}
	
	@Test
	public void testGetUser() {
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
	    String username=user.getUsername();
	    Set<String> organisationIds = new HashSet<String>();
	    organisationIds.add("1001");
	    organisationIds.add("9001");
		RestUserRepresentation restUserRepresentation = new RestUserRepresentation(user);
		restUserRepresentation.setId("1001");
        restUserRepresentation.setFirstName("entando");
        restUserRepresentation.setLastName("new");
        restUserRepresentation.setUsername(user.getUsername());
        restUserRepresentation.setEmail("entando.hub@entando.com");
        restUserRepresentation.setOrganisationIds(organisationIds);
        restUserRepresentation.setEnabled(true);
		Mockito.when(keyCloakService.getUser(restUserRepresentation.getUsername())).thenReturn(user);
		ResponseEntity<RestUserRepresentation> getUserResult = keyCloakUserController.getUser(username);
		assertNotNull(getUserResult);
		assertEquals(HttpStatus.OK,getUserResult.getStatusCode());
	}
	
	@Test
	public void testGetUserFails() {
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
	    String username=user.getUsername();
		RestUserRepresentation restUserRepresentation = new RestUserRepresentation(user);
		Mockito.when(keyCloakService.getUser(null)).thenReturn(user);
		ResponseEntity<RestUserRepresentation> getUserResult = keyCloakUserController.getUser(username);
		assertNotNull(getUserResult);
		assertEquals(HttpStatus.NOT_FOUND,getUserResult.getStatusCode());
	}
}
