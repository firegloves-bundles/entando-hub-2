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
	
	private static final String USER_ID = "1001";
    private static final String USER_NAME = "Test User Admin";
    private static final Long CREATED_TIMESTAMP = 90019L;
    private static final boolean IS_OTP = true;
    private static final boolean IS_ENALBED= true;
    private static final String EMAIL_VERIFIED = "test@mail.com";
    private static final String FIRST_NAME = "ENTANDO";
    private static final String LAST_NAME = "HUB";
    private static final String USER_EMAIL = "usertest@mail.com";
    
    private static final Long PORTAL_USER_ID = 1001l;
    private static final String PORTAL_USER_NAME = "Portal User Admin";
    
    private static final Long ORG_ID = 2000L;
    private static final String ORG_NAME = "Test Org Name";
    private static final String ORG_DESCRIOPTION = "Test Org Decription";

	@Test
	public void getUsersByOrganisationTest() {
		List<UserRepresentation> userRepresentationList = new ArrayList<>();
		UserRepresentation user = createUserRepresentation();
	    List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = createPortalUser();
		portalUserList.add(portalUser);
	    Organisation organisation = createOrganisation();
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
		UserRepresentation user = createUserRepresentation();
	    List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = createPortalUser();
		portalUserList.add(portalUser);
	    Organisation organisation = createOrganisation();
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
		UserRepresentation user = createUserRepresentation();
	    List<PortalUser> portalUserList = new ArrayList<>();
		PortalUser portalUser = createPortalUser();
		portalUserList.add(portalUser);
		Set<Organisation> orgsSet = new HashSet<>();
	    Organisation organisation = createOrganisation();
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
		PortalUser portalUser = createPortalUser();
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
		PortalUser portalUser = createPortalUser();
		Set<Organisation> orgsSet = new HashSet<>();
	    Organisation organisation = createOrganisation();
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
	
	private UserRepresentation createUserRepresentation() {
		UserRepresentation user = new UserRepresentation();
		user.setId(USER_ID);
		user.setCreatedTimestamp(CREATED_TIMESTAMP);
	    user.setUsername(USER_NAME);
	    user.setEnabled(IS_ENALBED);
	    user.setTotp(IS_OTP);
	    user.setEmailVerified(EMAIL_VERIFIED);
	    user.setFirstName(FIRST_NAME);
	    user.setLastName(LAST_NAME);
	    user.setEmail(USER_EMAIL);
	    return user;
	}
	
	private PortalUser createPortalUser() {
		PortalUser portalUser = new PortalUser();
        portalUser.setId(PORTAL_USER_ID);
		portalUser.setUsername(PORTAL_USER_NAME);
	    return portalUser;
	}
	
	private Organisation createOrganisation() {
    	Organisation organisation = new Organisation();
		organisation.setId(ORG_ID);
		organisation.setName(ORG_NAME);
		organisation.setDescription(ORG_DESCRIOPTION);
		return organisation;
    }
}
