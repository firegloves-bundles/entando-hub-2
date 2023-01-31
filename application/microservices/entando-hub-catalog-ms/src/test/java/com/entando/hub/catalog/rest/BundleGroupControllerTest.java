package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroupNoId;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(BundleGroupController.class)
public class BundleGroupControllerTest {

	@Autowired
	WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
	BundleGroupController bundleGroupController;

    @MockBean
	BundleGroupService bundleGroupService;

    @MockBean
	BundleGroupVersionService bundleGroupVersionService;

    @MockBean
	SecurityHelperService securityHelperService;

    private static final String URI = "/api/bundlegroups/";

    private static final Long BUNDLE_GROUP_ID = 1000L;
    private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
    private static final Long ORG_ID = 2000L;
    private static final String ORG_NAME = "Test Org Name";
    private static final String ORG_DESCRIOPTION = "Test Org Decription";
    private static final Long CATEGORY_ID = 3000L;
    private static final String CATEGORY_NAME = "Test Category Name";
    private static final String CATEGORY_DESCRIPTION = "Test Category Description";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
	public void testGetBundleGroupsByOrganisationId() throws Exception {
		List<BundleGroup> bundleGroupList = new ArrayList<>();
		BundleGroup bundleGroup = getBundleGroupObj();
		Organisation organisation = getOrganisationObj();
		bundleGroup.setOrganisation(organisation);
		bundleGroupList.add(bundleGroup);

		String organisationId = organisation.getId().toString();

		Mockito.when(bundleGroupService.getBundleGroups(Optional.ofNullable(null))).thenReturn(bundleGroupList);
		Mockito.when(bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId))).thenReturn(bundleGroupList);

		//Case 1: no organisation specified
		mockMvc.perform(MockMvcRequestBuilders.get(URI)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].bundleGroupId").value(bundleGroup.getId().toString()))
				.andExpect(jsonPath("$.[*].name").value(bundleGroup.getName()));

		//Case 2: testing with specific organisation
		mockMvc.perform(MockMvcRequestBuilders.get(URI + "?organisationId=" + organisationId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].bundleGroupId").value(bundleGroup.getId().toString()))
				.andExpect(jsonPath("$.[*].name").value(bundleGroup.getName()));
	}
	
	@Test
	public void getBundleGroup() throws Exception {
		List<BundleGroup> bundleGroupList = new ArrayList<>();
		BundleGroup bundleGroup = getBundleGroupObj();
		bundleGroupList.add(bundleGroup);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));

		mockMvc.perform(MockMvcRequestBuilders.get(URI + bundleGroupId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.bundleGroupId").value(bundleGroup.getId().toString()))
				.andExpect(jsonPath("$.name").value(bundleGroup.getName()));
	}

	@Test
	public void getBundleGroupFails() throws Exception {
		List<BundleGroup> bundleGroupList = new ArrayList<>();
		BundleGroup bundleGroup = getBundleGroupObj();
		bundleGroupList.add(bundleGroup);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));

		mockMvc.perform(MockMvcRequestBuilders.get(URI + bundleGroupId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}

	@WithMockUser(roles = { ADMIN })
	@Test
	public void testCreateBundleGroup() throws Exception {
		BundleGroup bundleGroup = getBundleGroupObj();
		Organisation organisation = getOrganisationObj();
		bundleGroup.setOrganisation(organisation);	
		Category category = getCategoryObj();
		category.setBundleGroups(null);
		bundleGroup.setCategories(Set.of(category));
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);

		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.empty()), bundleGroupNoId)).thenReturn(bundleGroup);

		mockMvc.perform(MockMvcRequestBuilders.post(URI)
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(bundleGroupNoId))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.bundleGroupId").value(bundleGroup.getId().toString()))
			.andExpect(jsonPath("$.name").value(bundleGroup.getName()));
	}

	@WithMockUser(roles = { AUTHOR })
	@Test
	public void testCreateBundleGroupFails() throws Exception {
		BundleGroup bundleGroup = getBundleGroupObj();
		bundleGroup.setOrganisation(null);	
		bundleGroup.setCategories(null);
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);

		Mockito.when(securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroupNoId.getOrganisationId())).thenReturn(true);
		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.empty()), bundleGroupNoId)).thenReturn(bundleGroup);

		mockMvc.perform(MockMvcRequestBuilders.post(URI)
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(bundleGroupNoId))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@WithMockUser(roles = { ADMIN })
	@Test
	public void testUpdateBundleGroup() throws Exception {
		BundleGroup bundleGroup = getBundleGroupObj();
		Organisation organisation = getOrganisationObj();
		bundleGroup.setOrganisation(organisation);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);

		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(true);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(true);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(true);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.empty()), bundleGroupNoId)).thenReturn(bundleGroup);
		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.of(bundleGroupId)), bundleGroupNoId)).thenReturn(bundleGroup);

		mockMvc.perform(MockMvcRequestBuilders.post(URI + bundleGroupId)
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(bundleGroupNoId)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.bundleGroupId").value(bundleGroup.getId().toString()))
           .andExpect(jsonPath("$.name").value(bundleGroup.getName()));
	}

	@WithMockUser(roles = { ADMIN })
	@Test
	public void testUpdateBundleGroupFails() throws Exception {
		BundleGroup bundleGroup = getBundleGroupObj();
		Organisation organisation = getOrganisationObj();
		bundleGroup.setOrganisation(organisation);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);

		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));

		mockMvc.perform(MockMvcRequestBuilders.post(URI + bundleGroupId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(bundleGroupNoId))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(false);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));

		mockMvc.perform(MockMvcRequestBuilders.post(URI + bundleGroupId)
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(bundleGroupNoId)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isConflict());

		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(true);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(false);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(false);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));

		mockMvc.perform(MockMvcRequestBuilders.post(URI + bundleGroupId)
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(bundleGroupNoId)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isForbidden());

		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(true);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(false);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(false);
		bundleGroup.setOrganisation(null);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));

		mockMvc.perform(MockMvcRequestBuilders.post(URI + bundleGroupId)
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(bundleGroupNoId)) 
           .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isForbidden());
	}

	@WithMockUser(roles = { ADMIN })
	@Test
	public void testDeleteBundleGroup() throws Exception {
		BundleGroup bundleGroup = getBundleGroupObj();
		String bundleGroupId = Long.toString(bundleGroup.getId());

		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		bundleGroupService.deleteBundleGroup(bundleGroupId);

		mockMvc.perform(MockMvcRequestBuilders.delete(URI + bundleGroupId)
		.accept(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isNoContent());
	}

	@WithMockUser(roles = { ADMIN })
	@Test
	public void testDeleteBundleGroupFails() throws Exception {
		BundleGroup bundleGroup = getBundleGroupObj();
		String bundleGroupId = Long.toString(bundleGroup.getId());
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.empty());
		bundleGroupService.deleteBundleGroup(bundleGroupId);

		mockMvc.perform(MockMvcRequestBuilders.delete(URI + bundleGroupId)
			.accept(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isNotFound());
	}

	private Category getCategoryObj() {
		Category category = new Category();
		category.setId(CATEGORY_ID);
		category.setName(CATEGORY_NAME);
		category.setDescription(CATEGORY_DESCRIPTION);
		return category;
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
