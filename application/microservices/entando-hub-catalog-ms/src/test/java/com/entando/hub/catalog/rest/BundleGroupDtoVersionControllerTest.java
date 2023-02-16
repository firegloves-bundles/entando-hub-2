package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.domain.BundleGroupVersionView;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(BundleGroupVersionController.class)
@WithMockUser(username="admin",roles={ADMIN})
public class BundleGroupDtoVersionControllerTest {
	
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	private MockMvc mockMvc;
	@InjectMocks
	BundleGroupVersionController bundleGroupVersionController;
	@MockBean
	BundleGroupVersionService bundleGroupVersionService;
	@MockBean
	BundleGroupService bundleGroupService;
	@MockBean
	CategoryService categoryService;
	@MockBean
	SecurityHelperService securityHelperService;
	
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

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void testGetBundleGroupVersions() throws Exception {
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();		
		bundleGroupVersionsList.add(bundleGroupVersion);
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		String bundleGroupId = bundleGroupVersion.getBundleGroup().getId().toString();
		Integer page = 0;
		Integer pageSize = 89;
		String[] statuses = new String[]{BundleGroupVersion.Status.PUBLISHED.toString()};
		
		List<BundleGroupVersionFilteredResponseView> list = new ArrayList<>();
		BundleGroupVersionFilteredResponseView viewObj = new BundleGroupVersionFilteredResponseView();
		viewObj.setBundleGroupVersionId(bundleGroupVersion.getId());
		viewObj.setDescription(bundleGroupVersion.getDescription());
		viewObj.setStatus(bundleGroupVersion.getStatus());
		viewObj.setVersion(bundleGroupVersion.getVersion());
		list.add(viewObj);
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> pagedContent = new PagedContent<>(list, response);
		
		String inputJsonPage = mapToJson(page);
		String inputJsonPageSize = mapToJson(pageSize);
		
		//Case 1: bundle group exists
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(eq(page), eq(pageSize), eq(statuses), any(BundleGroup.class))).thenReturn(pagedContent);
		System.out.println(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/versions/"+bundleGroupId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("statuses", statuses))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
		
		//Case 2: bundle group does not exist
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.empty());
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, statuses, bundleGroup)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/versions/"+bundleGroupId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("statuses", statuses))
				.andExpect(status().isOk());
		
		//Case 3: statuses list is null
		statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, statuses, bundleGroup)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/versions/"+bundleGroupId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
		
		//Case 4: page number >= 1
		page = 1;
		inputJsonPage = mapToJson(page);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, statuses,bundleGroup)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/versions/"+bundleGroupId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("statuses", statuses))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
	}
	
	@Test
	public void testGetBundleGroupVersionsAndFilterThem() throws Exception{
		List<Category> categoryList = new ArrayList<>();
		Category category = createCategory();
		categoryList.add(category);
		String[] categoryIds = new String[]{category.getId().toString()};
		
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		String bundleGroupId = bundleGroupVersion.getBundleGroup().getId().toString();
		String organisationId = bundleGroupVersion.getBundleGroup().getOrganisation().getId().toString();
		Integer page = 0;
		Integer pageSize = 89;
		String[] statuses = new String[]{BundleGroupVersion.Status.PUBLISHED.toString()};
		
		List<BundleGroupVersionFilteredResponseView> list = new ArrayList<>();
		BundleGroupVersionFilteredResponseView viewObj = new BundleGroupVersionFilteredResponseView();
		viewObj.setBundleGroupVersionId(bundleGroupVersion.getId());
		viewObj.setDescription(bundleGroupVersion.getDescription());
		viewObj.setStatus(bundleGroupVersion.getStatus());
		viewObj.setVersion(bundleGroupVersion.getVersion());
		list.add(viewObj);
		
		String inputJsonPage = mapToJson(page);
		String inputJsonPageSize = mapToJson(pageSize);

		//Case 1: all optional parameters given
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> pagedContent = new PagedContent<>(list, response);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));		//Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, Optional.empty())).thenReturn(pagedContent);
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, null)).thenReturn(pagedContent);
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, Optional.ofNullable(organisationId), categoryIds, statuses, null)).thenReturn(pagedContent);
		
		System.out.println(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, null));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/filtered")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("organisationId", organisationId)
				.param("categoryIds", categoryIds)
				.param("statuses", statuses))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));

		//Case 2: when categories list is null
		Mockito.when(categoryService.getCategories()).thenReturn(categoryList);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/filtered")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("organisationId", organisationId)
				.param("statuses", statuses))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
		
		//Case 3: when statuses list is null
		statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, null)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/filtered")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("organisationId", organisationId)
				.param("categoryIds", categoryIds))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
		
		//Case 4: page number >= 1
		page = 1;
		inputJsonPage = mapToJson(page);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/filtered")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("organisationId", organisationId)
				.param("categoryIds", categoryIds)
				.param("statuses", statuses))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
	}
	
	@Test
	public void testGetBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testGetBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(null)).thenReturn(Optional.empty());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	public void testCreateBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupVersion);
		Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = Optional.of(bundleGroup);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupVersionView.getBundleGroupId().toString())).thenReturn(bundleGroupOptional);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(new ArrayList<BundleGroupVersion>());
    	Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn(bundleGroupVersion);
    	String inputJson = mapToJson(bundleGroupVersionView);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}
	
	@Test
	public void testCreateBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupVersion);
		
		//Case 1: bundle group does not exist
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(new ArrayList<BundleGroupVersion>());
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroup), bundleGroupVersionView)).thenReturn(bundleGroupVersion);
		String inputJson = mapToJson(bundleGroupVersionView);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		
		//Case 2: when there already exists a version
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupVersionView.getBundleGroupId().toString())).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(List.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroup), bundleGroupVersionView)).thenReturn(bundleGroupVersion);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	public void testUpdateBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		String bundleGroupId = bundleGroup.getId().toString();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupId, bundleGroupVersion.getDescription(), bundleGroupVersion.getDescriptionImage(), bundleGroupVersion.getVersion());
		String inputJson = mapToJson(bundleGroupVersionView);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));		
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testUpdateBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		String bundleGroupId = bundleGroup.getId().toString();
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupId, bundleGroupVersion.getDescription(), bundleGroupVersion.getDescriptionImage(), bundleGroupVersion.getVersion());
		String inputJson = mapToJson(bundleGroupVersionView);
		
		//Case 1: bundle group version does not exist
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(null)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		
		//Case 2: user is not admin
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(false);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().isOk());
				
		//Case 3: user is not an admin and user is not in the organisation
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(false);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
		
		//Case 4: user is not an admin and organisation is null
		bundleGroupVersion.setBundleGroup(null);
		bundleGroup.setOrganisation(null);
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(null)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	}
	
	@Test
	public void testDeleteBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersion.setStatus(Status.DELETE_REQ);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testDeleteBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersion.setStatus(Status.DELETE_REQ);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		
		//Case 1: when bundle group version does not exist
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.empty());
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
			
		//Case 2: when bundle group version is not in delete request status
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.NOT_PUBLISHED);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		
		//Case 3: when bundle group version does not exist and is not in delete request status
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.empty());
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
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
