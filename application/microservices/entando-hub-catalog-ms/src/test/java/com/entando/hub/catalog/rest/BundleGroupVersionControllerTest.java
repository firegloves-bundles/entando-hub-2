package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;
import com.entando.hub.catalog.rest.validation.BundleGroupValidator;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import com.entando.hub.catalog.service.mapper.BundleGroupVersionMapper;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles={ADMIN})
class BundleGroupVersionControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	BundleGroupVersionService bundleGroupVersionService;
	@MockBean
	BundleGroupService bundleGroupService;
	@MockBean
	CategoryService categoryService;
	@MockBean
	SecurityHelperService securityHelperService;
	@MockBean
	BundleGroupValidator bundleGroupValidator;
	@MockBean
	CatalogService catalogService;
	@Autowired
	private BundleGroupVersionMapper bundleGroupVersionMapper;
	private static final String CATALOG_ID_PARAM = "catalogId";
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
	@Test
	void testGetBundleGroupVersions() throws Exception {
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		bundleGroupVersionsList.add(bundleGroupVersion);
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		Long bundleGroupId = bundleGroupVersion.getBundleGroup().getId();
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
		PageImpl<BundleGroupVersionEntityDto> dtoResponse = convertoToDto(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> pagedContent = new PagedContent<>(list, dtoResponse);
		
		String inputJsonPage = mapToJson(page);
		String inputJsonPageSize = mapToJson(pageSize);
		
		//Case 1: bundle group exists
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(eq(page), eq(pageSize), eq(statuses), any(BundleGroup.class))).thenReturn(pagedContent);
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
	void testGetBundleGroupVersionsAndFilterThem() throws Exception{
		List<Category> categoryList = new ArrayList<>();
		Category category = createCategory();
		categoryList.add(category);
		String[] categoryIds = new String[]{category.getId().toString()};
		
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		Long bundleGroupId = bundleGroupVersion.getBundleGroup().getId();
		Long organisationId = bundleGroupVersion.getBundleGroup().getOrganisation().getId();
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
		PageImpl<BundleGroupVersionEntityDto> dtoResponse = convertoToDto(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> pagedContent = new PagedContent<>(list, dtoResponse);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));		//Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, Optional.empty())).thenReturn(pagedContent);
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, organisationId, categoryIds, statuses, null)).thenReturn(pagedContent);
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, organisationId, categoryIds, statuses, null)).thenReturn(pagedContent);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/filtered")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("organisationId", String.valueOf(organisationId))
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
				.param("organisationId", String.valueOf(organisationId))
				.param("statuses", statuses))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
		
		//Case 3: when statuses list is null
		statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, organisationId, categoryIds, statuses, null)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/filtered")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize)
				.param("organisationId", String.valueOf(organisationId))
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
				.param("organisationId", String.valueOf(organisationId))
				.param("categoryIds", categoryIds)
				.param("statuses", statuses))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));;
	}
	
	@Test
	void testGetBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	void testGetBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(null)).thenReturn(Optional.empty());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	void testCreateBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		BundleGroupVersionDto bundleGroupVersionView = bundleGroupVersionMapper.toViewDto(bundleGroupVersion); // new BundleGroupVersionView(bundleGroupVersion);
		Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = Optional.of(bundleGroup);
		Mockito.when(bundleGroupService.getBundleGroup(Long.parseLong(bundleGroupVersionView.getBundleGroupId()))).thenReturn(bundleGroupOptional);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(new ArrayList<BundleGroupVersion>());
    	Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn(bundleGroupVersion);
    	String inputJson = mapToJson(bundleGroupVersionView);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}

	@Test
	void testGetBundleGroupVersionPrivateCatalog() throws Exception {
		//Not authenticated user require to get a private catalog
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(false);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId))
				.thenReturn(Optional.of(bundleGroupVersion));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.param(CATALOG_ID_PARAM, "1")
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	}


	@Test
	void testGetBundleGroupVersionPrivateCatalogAdmin() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(false);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId))
				.thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(securityHelperService.isUserAuthenticated()).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
						.param(CATALOG_ID_PARAM, "1")
						.accept(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
				.andExpect(status().is(HttpStatus.OK.value()));
	}


	@Test
	@WithMockUser(username = "MANAGER", roles = {MANAGER})
	void testGetBundleGroupVersionPrivateCatalogManager() throws Exception {
		//Not authenticated user require to get a private catalog
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(false);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId))
				.thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(securityHelperService.isUserAuthenticated()).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
						.param(CATALOG_ID_PARAM, "1")
						.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.OK.value()));
	}
	@Test
	void testCreateBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		BundleGroupVersionDto bundleGroupVersionView = bundleGroupVersionMapper.toViewDto(bundleGroupVersion); // new BundleGroupVersionView(bundleGroupVersion);
		BundleGroupVersionDto bundleGroupVersionViewIn = bundleGroupVersionMapper.toViewDto(bundleGroupVersion); // new BundleGroupVersionView(bundleGroupVersion);
		bundleGroupVersionViewIn.setBundleGroupId(null);
		
		//Case 1: bundle group does not exist
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(new ArrayList<BundleGroupVersion>());

//		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroup), bundleGroupVersionView)).thenReturn(bundleGroupVersion);
		BundleGroupVersion entity = bundleGroupVersionMapper.toEntity(bundleGroupVersionViewIn, bundleGroup);
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(entity, bundleGroupVersionView)).thenReturn(bundleGroupVersion);

		String inputJson = mapToJson(bundleGroupVersionView);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		
		//Case 2: when there already exists a version
		Mockito.when(bundleGroupService.getBundleGroup(Long.parseLong(bundleGroupVersionView.getBundleGroupId()))).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(List.of(bundleGroupVersion));
//		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroup), bundleGroupVersionView)).thenReturn(bundleGroupVersion);
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(entity, bundleGroupVersionView)).thenReturn(bundleGroupVersion);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	void testUpdateBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		String bundleGroupId = bundleGroup.getId().toString();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		BundleGroupVersionDto bundleGroupVersionView = generateBundleGroupVersionView(bundleGroupVersion, bundleGroupId);
		// new BundleGroupVersionView(bundleGroupId, bundleGroupVersion.getDescription(), bundleGroupVersion.getDescriptionImage(), bundleGroupVersion.getVersion());
		String inputJson = mapToJson(bundleGroupVersionView);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));		
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundlegroupversions/{bundleGroupVersionId}",
					bundleGroupVersionId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().isOk());
	}
	
	@Test
	void testUpdateBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();
		String bundleGroupId = bundleGroup.getId().toString();
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		BundleGroupVersionDto bundleGroupVersionView = generateBundleGroupVersionView(bundleGroupVersion, bundleGroupId); // bundleGroupId, bundleGroupVersion.getDescription(), bundleGroupVersion.getDescriptionImage(), bundleGroupVersion.getVersion());

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

	private static BundleGroupVersionDto generateBundleGroupVersionView(BundleGroupVersion bundleGroupVersion, String bundleGroupId) {
		return BundleGroupVersionDto.builder()
				.bundleGroupId(bundleGroupId)
				.description(bundleGroupVersion.getDescription())
				.descriptionImage(bundleGroupVersion.getDescriptionImage())
				.version(bundleGroupVersion.getVersion())
				.build();
	}

	@Test
	void testDeleteBundleGroupVersion() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
		bundleGroupVersion.setStatus(Status.DELETE_REQ);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundlegroupversions/{bundleGroupVersionId}", bundleGroupVersionId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	void testDeleteBundleGroupVersionFails() throws Exception {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion(true);
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
	
	private BundleGroupVersion createBundleGroupVersion(boolean publicCatalog ) {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroupVersion.setDescription(DESCRIPTION);
		bundleGroupVersion.setDescriptionImage(DESCRIPTION_IMAGE);
		bundleGroupVersion.setDocumentationUrl(DOCUMENTATION_URL);
		bundleGroupVersion.setVersion(VERSION);
		bundleGroupVersion.setStatus(Status.PUBLISHED);

		BundleGroup bundleGroup = createBundleGroup(publicCatalog);
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Bundle bundle = createBundle();
		bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
		bundleGroupVersion.setBundles(Set.of(bundle));
		return bundleGroupVersion;
	}
	
	private BundleGroup createBundleGroup(boolean publicCatalog) {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUPID);
		bundleGroup.setName(NAME);
		bundleGroup.setPublicCatalog(publicCatalog);
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

	protected PageImpl<BundleGroupVersionEntityDto> convertoToDto(Page<BundleGroupVersion> page) {
		return new PageImpl<>(page.getContent()
				.stream()
				.map(e -> bundleGroupVersionMapper.toEntityDto(e))
				.collect(Collectors.toList())
				, page.getPageable(), page.getNumberOfElements());
	}
}
