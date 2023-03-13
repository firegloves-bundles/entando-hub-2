package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.rest.validation.BundleGroupValidator;
import com.entando.hub.catalog.service.BundleService;
import com.entando.hub.catalog.service.mapper.BundleMapper;
import com.entando.hub.catalog.service.mapper.BundleMapperImpl;
import com.entando.hub.catalog.service.security.SecurityHelperService;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(BundleController.class)
@WithMockUser(username="admin", roles={ADMIN})
@ComponentScan(basePackageClasses = {BundleMapper.class, BundleMapperImpl.class})
public class BundleControllerTest {

	@Autowired
	private BundleMapper bundleMapper;

	@Autowired
	WebApplicationContext webApplicationContext;
	
	@Autowired
	private MockMvc mockMvc;
	
	@InjectMocks
	BundleController bundleController;
	
	@MockBean
	BundleService bundleService;

	@MockBean
	BundleGroupValidator bundleGroupValidator;
	@MockBean
	SecurityHelperService securityHelperService;
	private final Long BUNDLE_ID = 1001L;
	private final String NAME = "Test";
	private final String DESCRIPTION = "Test Description";
	private final String GIT_REPO_ADDRESS = "Test Git Rep";
	private final String GIT_SRC_REPO_ADDRESS = "Test Src Git Rep";
	private final String DEPENDENCIES = "Test Dependencies";
	private final Long VERSION_ID = 5001L;
	private final BundleGroupVersion.Status STATUS = BundleGroupVersion.Status.PUBLISHED;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {ADMIN})
	public void testGetAllBundlesAdmin() throws Exception {
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = populateBundle();
		bundlesList.add(bundle);
		Mockito.when(bundleService.getBundles(null,null)).thenReturn(bundlesList);
		Mockito.when(bundleGroupValidator.validateBundlePrivateCatalogRequest(any())).thenReturn(true);
		Mockito.when(securityHelperService.isUserAuthenticated()).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundles/").accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
	            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andDo(print())
	            .andExpect(jsonPath("$.[*].bundleId").value(bundle.getId().toString()))
	            .andExpect(jsonPath("$.[*].name").value(bundle.getName()))
	            .andExpect(jsonPath("$.[*].description").value(bundle.getDescription()));
		
	}
	
	@Test
	public void testGetBundle() throws Exception {
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = populateBundle();
		bundlesList.add(bundle);
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.of(bundle));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundles/{bundleId}", bundleId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testGetBundleFails() throws Exception {
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = populateBundle();
		bundlesList.add(bundle);
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.empty());
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundles/{bundleId}", bundleId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	public void testCreateBundle() throws Exception {
		Bundle bundle = populateBundle();
		String bundleId = bundle.getId().toString();
		//Case 1: bundleId is not null
		BundleDto bundleDto = bundleMapper.toDto(bundle);
		// Mockito.when(bundleService.createBundle(bundleDto.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		Mockito.when(bundleService.createBundle(bundleMapper.toEntity(bundleDto))).thenReturn(bundle);
		String inputJson = mapToJson(bundleDto);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundles/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.CREATED.value()));
		
		//Case 2: bundleId is null
		BundleDto bundleDto2 = new BundleDto(null, bundle.getName(), bundle.getDescription(), bundle.getGitRepoAddress(), bundle.getGitSrcRepoAddress(), new ArrayList<>(), new ArrayList<>(), null);
//		Mockito.when(bundleService.createBundle(bundleDto2.createEntity(Optional.empty()))).thenReturn(bundle);
		Mockito.when(bundleService.createBundle(bundleMapper.toEntity(bundleDto2))).thenReturn(bundle);
		inputJson = mapToJson(bundleDto2);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundles/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}
	
	@Test
	public void testUpdateBundle() throws Exception {
		Bundle bundle = populateBundle();
		String bundleId = bundle.getId().toString();
		BundleDto bundleDto = bundleMapper.toDto(bundle); // new BundleDto(bundle);
		bundleDto.setBundleId(bundleId);
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.of(bundle));
//		Mockito.when(bundleService.createBundle(bundleDto.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		Mockito.when(bundleService.createBundle(bundleMapper.toEntity(bundleDto))).thenReturn(bundle);
		String inputJson = mapToJson(bundleDto);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundles/{bundleId}", bundleId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testUpdateBundleFails() throws Exception {
		Bundle bundle = populateBundle();
		String bundleId = bundle.getId().toString();
		BundleDto bundleDto = bundleMapper.toDto(bundle);// new BundleDto(bundle);
		bundleDto.setBundleId(bundleId);
		Mockito.when(bundleService.getBundle(null)).thenReturn(Optional.of(bundle));
//		Mockito.when(bundleService.createBundle(bundleDto.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		Mockito.when(bundleService.createBundle(bundleMapper.toEntity(bundleDto))).thenReturn(bundle);
		String inputJson = mapToJson(bundleDto);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundles/{bundleId}", bundleId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
		
	}
	
	@Test
	public void testDeleteBundle() throws Exception {
		Bundle bundle =populateBundle();
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.of(bundle));
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundles/{bundleId}", bundleId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testDeleteBundleFails() throws Exception {
		Bundle bundle =populateBundle();
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.empty());
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundles/{bundleId}", bundleId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}
	
	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}
	
	private Bundle populateBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(BUNDLE_ID);
		bundle.setName(NAME);
		bundle.setDescription(DESCRIPTION);
		bundle.setGitRepoAddress(GIT_REPO_ADDRESS);
		bundle.setGitSrcRepoAddress(GIT_SRC_REPO_ADDRESS);
		bundle.setDependencies(DEPENDENCIES);
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		Set<Bundle> bundles = new HashSet<>();
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		bundles.add(bundle);
		return bundle;
	}
	
	private BundleGroupVersion createBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(VERSION_ID);
		bundleGroupVersion.setStatus(STATUS);
		return bundleGroupVersion;
	}

}
