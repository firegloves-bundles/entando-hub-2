package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
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

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.OrganisationController.OrganisationNoId;
import com.entando.hub.catalog.service.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(OrganisationController.class)
public class OrganisationControllerTest {

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	OrganisationController organisationController;

	@MockBean
	OrganisationService organisationService;

	private static final String URI = "/api/organisation/";
	private static final Long ORG_ID = 2000L;
	private static final String ORG_NAME = "Test Org Name";
	private static final String ORG_DESCRIOPTION = "Test Org Decription";

	private static final Long BUNDLE_GROUP_ID = 1000L;
	private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void testGetOrganisations() throws Exception {
		Organisation organisation = getOrganisationObj();
		Mockito.when(organisationService.getOrganisations()).thenReturn(List.of(organisation));
		mockMvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.[*].name").value(organisation.getName())).andReturn();
	}

	@Test
	public void testGetOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		String organisationId = Long.toString(organisation.getId());
		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
		mockMvc.perform(MockMvcRequestBuilders.get(URI + organisationId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.name").value(organisation.getName()));
	}

	@Test
	public void testGetOrganisationFails() throws Exception {
		Organisation organisation = getOrganisationObj();
		String organisationId = Long.toString(organisation.getId());
		Mockito.when(organisationService.getOrganisation(null)).thenReturn(Optional.of(organisation));
		mockMvc.perform(MockMvcRequestBuilders.get(URI + organisationId)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

	}

	@Test
	@WithMockUser(roles = { ADMIN })
	public void testCreateOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		OrganisationNoId organisationNoId = new OrganisationNoId(organisation.getName(), organisation.getDescription());
		Mockito.when(organisationService.createOrganisation(organisationNoId.createEntity(Optional.empty()),organisationNoId)).thenReturn(organisation);
		mockMvc.perform(MockMvcRequestBuilders.post(URI).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(organisationNoId)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.name").value(organisation.getName()));

	}

	@Test
	@WithMockUser(roles = { ADMIN })
	public void testUpdateOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		String organisationId = Long.toString(organisation.getId());
		OrganisationNoId organisationNoId = new OrganisationNoId(organisation.getName(), organisation.getDescription());
		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
		Mockito.when(organisationService.createOrganisation(organisationNoId.createEntity(Optional.of(organisationId)),organisationNoId)).thenReturn(organisation);
		mockMvc.perform(MockMvcRequestBuilders.post(URI + organisationId).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(organisationNoId)).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.name").value(organisation.getName()));
	}

	@Test
	@WithMockUser(roles = { ADMIN })
	public void testUpdateOrganisationFails() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		String organisationId = Long.toString(organisation.getId());
		OrganisationNoId organisationNoId = new OrganisationNoId(organisation.getName(), organisation.getDescription());

		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.empty());
		mockMvc.perform(MockMvcRequestBuilders.post(URI + organisationId).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(organisationNoId)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));

	}

	@Test
	@WithMockUser(roles = { ADMIN })
	public void testDeleteOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		String organisationId = Long.toString(organisation.getId());
		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
		organisationService.deleteOrganisation(organisationId);
		mockMvc.perform(MockMvcRequestBuilders.delete(URI + organisationId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(roles = { ADMIN })
	public void testDeleteOrganisationFails() throws Exception {
		Organisation organisation = getOrganisationObj();
		String organisationId = Long.toString(organisation.getId());
		Mockito.when(organisationService.getOrganisation(null)).thenReturn(Optional.of(organisation));
		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.empty());
		organisationService.deleteOrganisation(organisationId);
		mockMvc.perform(MockMvcRequestBuilders.delete(URI + organisationId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}

	private Organisation getOrganisationObj() {
		Organisation organisation = new Organisation();
		organisation.setId(ORG_ID);
		organisation.setName(ORG_NAME);
		organisation.setDescription(ORG_DESCRIOPTION);
		return organisation;
	}

	private BundleGroup getBundleGroupObj() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUP_ID);
		bundleGroup.setName(BUNDLE_GROUP_NAME);
		return bundleGroup;
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
