package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.*;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(AppBuilderBundleGroupsController.class)
@WithMockUser(username = "admin", roles = { ADMIN })
public class AppBuilderBundleGroupsControllerTest {
	
	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	AppBuilderBundleGroupsController appBuilderBundleGroupsController;

	@MockBean
	BundleGroupVersionService bundleGroupVersionService;
	@MockBean
	CatalogService catalogService;
	private final Long BUNDLE_GROUP_VERSION_ID =  2001L;
	private final Long BUNDLE_GROUPID =  2002L;
	private final Long CATEGORY_ID =  2003L;
	private final Long BUNDLE_ID =  2004L;
	private final Long ORG_ID =  2005L;
	private final String NAME = "New Name";
	private final String DESCRIPTION = "New Description";
	private final String DESCRIPTION_IMAGE = "New Description Image";
	private final String DOCUMENTATION_URL = "New Documentation Url";
	private final String VERSION = "V1.V2";
	private final String GIT_REPO_ADDRESS = "Test Git Rep";
	private final String DEPENDENCIES = "Test Dependencies";
	private final String API_KEY = "api-key";

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void getBundleGroupVersionsTest() throws Exception{
		Category category = createCategory();
		String[] categoryIds = new String[]{category.getId().toString()};
		
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersionsList.add(bundleGroupVersion);
		Integer page = 0;
		Integer pageSize = 89;
		List<BundleGroupVersionFilteredResponseView> list = new ArrayList<>();
		BundleGroupVersionFilteredResponseView viewObj = new BundleGroupVersionFilteredResponseView();
		viewObj.setBundleGroupVersionId(bundleGroupVersion.getId());
		viewObj.setDescription(bundleGroupVersion.getDescription());
		viewObj.setStatus(bundleGroupVersion.getStatus());
		viewObj.setVersion(bundleGroupVersion.getVersion());
		list.add(viewObj);
		
		String inputJsonPage = mapToJson(page);
		String inputJsonPageSize = mapToJson(pageSize);
		
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> pagedContent = new PagedContent<>(list, response);
		
		//Case 1: api key is not passed as parameter
		Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupVersions(page, pageSize)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/appbuilder/api/bundlegroups/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));

		//Case 2: when passing a valid api-key
		Long userCatalogId = 1L;
		Catalog catalog = new Catalog();
		catalog.setId(userCatalogId);
		Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
		Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalogId, page, pageSize)).thenReturn(pagedContent);

		mockMvc.perform(MockMvcRequestBuilders.get("/appbuilder/api/bundlegroups/")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("Entando-hub-api-key", API_KEY)
						.param("page", inputJsonPage)
						.param("pageSize", inputJsonPageSize))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));
	}

	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	private BundleGroupVersion createBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroupVersion.setDescription(DESCRIPTION);
		bundleGroupVersion.setDescriptionImage(DESCRIPTION_IMAGE);
		bundleGroupVersion.setDocumentationUrl(DOCUMENTATION_URL);
		bundleGroupVersion.setVersion(VERSION);
		bundleGroupVersion.setStatus(Status.PUBLISHED);
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Bundle bundle = createBundle();
		bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
		bundleGroupVersion.setBundles(Set.of(bundle));
		return bundleGroupVersion;
	}

	private BundleGroup createBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUPID);
		bundleGroup.setName(NAME);
		Organisation organisation = createOrganisation();
		bundleGroup.setOrganisation(organisation);
		return bundleGroup;
	}

	private Bundle createBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(BUNDLE_ID);
		bundle.setName(NAME);
		bundle.setDescription(DESCRIPTION);
		bundle.setGitRepoAddress(GIT_REPO_ADDRESS);
		bundle.setDependencies(DEPENDENCIES);
		return bundle;
	}

	private Organisation createOrganisation() {
		Organisation organisation = new Organisation();
		organisation.setId(ORG_ID);
		organisation.setName(NAME);
		organisation.setDescription(DEPENDENCIES);
		return organisation;
	}

	private Category createCategory() {
		Category category = new Category();
		category.setId(CATEGORY_ID);
		category.setName(NAME);
		category.setDescription(DESCRIPTION);
		category.setBundleGroups(null);
		return category;
	}
}
