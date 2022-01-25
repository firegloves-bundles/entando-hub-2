package com.entando.hub.catalog.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.rest.model.OrganisationResponseView;
import com.entando.hub.catalog.rest.model.PortalUserResponseView;
import com.entando.hub.catalog.service.model.UserRepresentation;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class PortalUserServiceTest {
	
	@InjectMocks
	PortalUserService portalUserService;
	@Mock
	KeycloakService keycloakService;
	@Mock
	OrganisationRepository organisationRepository;
	@Mock
	PortalUserRepository portalUserRepository;
	
	@Test
	public void getUsersByOrganisationTest() {
		List<UserRepresentation> userRepresentationList = new ArrayList<>();
		UserRepresentation user = new UserRepresentation();
		user.setId("1001");
		user.setCreatedTimestamp(90019);
	    user.setUsername("admin");
	    user.setEnabled(true);
	    user.setTotp(true);
	    user.setEmailVerified("xyz.mail.com");
	    user.setFirstName("entando");
	    user.setLastName("hub");
	    user.setEmail("xyz.mail.com");
	    List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
        portalUser.setId(1001L);
		portalUser.setUsername("admin");
		portalUserList.add(portalUser);
	    Organisation organisation = new Organisation();
	    organisation.setId(2001L);
		organisation.setPortalUsers(Set.of(portalUser));
		user.setOrganisationIds(Set.of());
	    Long organisationId = organisation.getId();
		user.setOrganisationIds(Set.of(organisationId));
		userRepresentationList.add(user);
		
		Mockito.when(this.organisationRepository.findById(organisationId)).thenReturn(Optional.of(organisation));
		Mockito.when(this.portalUserRepository.findAll()).thenReturn(portalUserList);
		Mockito.when(this.keycloakService.getUser(portalUser.getUsername())).thenReturn(user);
		
		//Case 1: orgId is not null
		List<UserRepresentation> result = portalUserService.getUsersByOrganisation(organisationId.toString());
		assertNotNull(result);
		assertEquals(userRepresentationList.get(0).getId(), result.get(0).getId());
		
		//Case 2: when user is null
		Mockito.when(this.keycloakService.getUser(portalUser.getUsername())).thenReturn(null);
		List<UserRepresentation> result2 = portalUserService.getUsersByOrganisation(organisationId.toString());
		assertNotNull(result2);
		assertEquals(0, result2.size());
		
		//Case 3: org has no users
		organisation.setPortalUsers(null);
		Mockito.when(this.keycloakService.getUser(portalUser.getUsername())).thenReturn(user);
		Mockito.when(this.organisationRepository.findById(organisationId)).thenReturn(Optional.of(organisation));
		List<UserRepresentation> result3 = portalUserService.getUsersByOrganisation(organisationId.toString());
		assertNotNull(result3);
		assertEquals(0, result3.size());
		
		//Case 4: org is empty
		Mockito.when(this.organisationRepository.findById(organisationId)).thenReturn(Optional.empty());
		List<UserRepresentation> result4 = portalUserService.getUsersByOrganisation(organisationId.toString());
		assertNull(result4);
		
		//Case 5: orgId is null
		List<UserRepresentation> result5 = portalUserService.getUsersByOrganisation(null);
		assertNotNull(result5);
		assertEquals(userRepresentationList.get(0).getId(), result5.get(0).getId());
	}
	
	@Test
	public void addUserToOrganizationTest() {
		UserRepresentation user = new UserRepresentation();
		user.setId("1001");
		user.setCreatedTimestamp(90019);
	    user.setUsername("admin");
	    user.setEnabled(true);
	    user.setTotp(true);
	    user.setEmailVerified("xyz.mail.com");
	    user.setFirstName("entando");
	    user.setLastName("hub");
	    user.setEmail("xyz.mail.com");
	    List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
        portalUser.setId(1001L);
		portalUser.setUsername("admin");
		portalUserList.add(portalUser);
	    Organisation organisation = new Organisation();
	    organisation.setId(2001L);
		organisation.setPortalUsers(Set.of(portalUser));
		user.setOrganisationIds(Set.of());
	    Long organisationId = organisation.getId();
		user.setOrganisationIds(Set.of(organisationId));
		String username = user.getUsername();
		
		Mockito.when(this.organisationRepository.findById(organisationId)).thenReturn(Optional.of(organisation));
		Mockito.when(keycloakService.getUser(username)).thenReturn(user);
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(portalUser);
		Mockito.when(this.portalUserRepository.save(portalUser)).thenReturn(portalUser);
		
		//Case 1: all true
		Boolean result = portalUserService.addUserToOrganization(username, organisationId.toString());
		assertNotNull(result);
		assertEquals(true, result);
		
		//Case 2: user already present in org
		portalUser.setOrganisations(Set.of(organisation));
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(portalUser);
		Mockito.when(this.portalUserRepository.save(portalUser)).thenReturn(portalUser);
		Boolean result2 = portalUserService.addUserToOrganization(username, organisationId.toString());
		assertNotNull(result2);
		assertEquals(false, result2);
		
		//Case 3: portal user is null
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(null);
		Boolean result3 = portalUserService.addUserToOrganization(username, organisationId.toString());
		assertNotNull(result3);
		assertEquals(true, result3);

		//Case 2: user is null
		Mockito.when(keycloakService.getUser(username)).thenReturn(null);
		Boolean result4 = portalUserService.addUserToOrganization(username, organisationId.toString());
		assertNotNull(result4);
		assertEquals(false, result4);
		
		//Case 5: org is not present
		Mockito.when(this.organisationRepository.findById(organisationId)).thenReturn(Optional.empty());
		Boolean result5 = portalUserService.addUserToOrganization(username, organisationId.toString());
		assertNotNull(result5);
		assertEquals(false, result5);
	}
	
	@Test
	public void removeUserFromOrganizationTest() {
		UserRepresentation user = new UserRepresentation();
		user.setId("1001");
		user.setCreatedTimestamp(90019);
	    user.setUsername("admin");
	    user.setEnabled(true);
	    user.setTotp(true);
	    user.setEmailVerified("xyz.mail.com");
	    user.setFirstName("entando");
	    user.setLastName("hub");
	    user.setEmail("xyz.mail.com");
	    List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = new PortalUser();
        portalUser.setId(1001L);
		portalUser.setUsername("admin");
		portalUserList.add(portalUser);
		Set<Organisation> orgsSet = new HashSet<>();
	    Organisation organisation = new Organisation();
	    organisation.setId(2001L);
		organisation.setPortalUsers(Set.of(portalUser));
		orgsSet.add(organisation);
		user.setOrganisationIds(Set.of());
		portalUser.setOrganisations(orgsSet);
	    Long organisationId = organisation.getId();
		user.setOrganisationIds(Set.of(organisationId));
		String username = user.getUsername();
		
		Mockito.when(this.organisationRepository.findById(organisationId)).thenReturn(Optional.of(organisation));
		Mockito.when(keycloakService.getUser(username)).thenReturn(user);
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(portalUser);
		
		//Case 1: all true
		Boolean result = portalUserService.removeUserFromOrganization(username, organisationId.toString());
		assertNotNull(result);
		assertEquals(true, result);
		
		//Case 2: portal user is null
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(null);
		Boolean result2 = portalUserService.removeUserFromOrganization(username, organisationId.toString());
		assertNotNull(result2);
		assertEquals(false, result2);
		
		//Case 3: user is null
		Mockito.when(keycloakService.getUser(username)).thenReturn(null);
		Boolean result3 = portalUserService.removeUserFromOrganization(username, organisationId.toString());
		assertNotNull(result3);
		assertEquals(false, result3);
		
		//Case 4: org is not present
		Mockito.when(this.organisationRepository.findById(organisationId)).thenReturn(Optional.empty());
		Boolean result4 = portalUserService.removeUserFromOrganization(username, organisationId.toString());
		assertNotNull(result4);
		assertEquals(false, result4);
	}
	
	@Test
	public void removeUserTest() {
		PortalUser portalUser = new PortalUser();
        portalUser.setId(1001L);
		portalUser.setUsername("admin");
		String username = portalUser.getUsername();
		
		//Case 1: portal user exists
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(portalUser);
		Boolean result = portalUserService.removeUser(username);
		assertNotNull(result);
		assertEquals(true, result);
		
		//Case 2: portal user does not exist
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(null);
		Boolean result2 = portalUserService.removeUser(username);
		assertNotNull(result2);
		assertEquals(false, result2);
	}
	
	@Test
	public void getUserByUsernameTest() {
		PortalUser portalUser = new PortalUser();
        portalUser.setId(1001L);
        portalUser.setEmail("test@test.com");
		portalUser.setUsername("admin");
		Set<Organisation> orgsSet = new HashSet<>();
	    Organisation organisation = new Organisation();
	    organisation.setId(2001L);
	    organisation.setName("Technical");
		organisation.setDescription("New Organisation");
		organisation.setPortalUsers(Set.of(portalUser));
		orgsSet.add(organisation);
		portalUser.setOrganisations(orgsSet);
		String username = portalUser.getUsername();
		
		OrganisationResponseView organisationsView = new OrganisationResponseView();
		organisationsView.setOrganisationId(organisation.getId());
		organisationsView.setOrganisationName(organisation.getName());
		organisationsView.setOrganisationDescription(organisation.getDescription());
		Set<OrganisationResponseView> organisationsViewSet = new HashSet<OrganisationResponseView>();
		organisationsViewSet.add(organisationsView);
		
		PortalUserResponseView portalUserResponseView = new PortalUserResponseView();
		portalUserResponseView.setId(portalUser.getId());
		portalUserResponseView.setEmail(portalUser.getEmail());
		portalUserResponseView.setUsername(portalUser.getUsername());
		portalUserResponseView.setOrganisations(organisationsViewSet);
		
		//Case 1: portal user exists
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(portalUser);
		PortalUserResponseView result = portalUserService.getUserByUsername(username);
		assertNotNull(result);
		assertEquals(portalUserResponseView, result);
		
		//Case 2: portal user is null
		Mockito.when(this.portalUserRepository.findByUsername(username)).thenReturn(null);
		PortalUserResponseView result2 = portalUserService.getUserByUsername(username);
		assertNull(result2);
	}
}
