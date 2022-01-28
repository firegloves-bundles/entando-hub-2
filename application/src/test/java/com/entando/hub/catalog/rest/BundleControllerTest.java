package com.entando.hub.catalog.rest;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;
import com.entando.hub.catalog.service.BundleService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(BundleController.class)
@WithMockUser(username="admin",roles={ADMIN})
public class BundleControllerTest {
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@Autowired
	private MockMvc mockMvc;
	
	@InjectMocks
	BundleController bundleController;
	
	@MockBean
	BundleService bundleService;
	
	private final Long BUNDLE_ID = 1001L;
	private final String NAME = "Test";
	private final String DESCRIPTION = "Test Description";
	private final String GIT_REPO_ADDRESS = "Test Git Rep";
	private final String DEPENDENCIES = "Test Dependencies";
	private final Long VERSION_ID = 5001L;
	private final BundleGroupVersion.Status STATUS = BundleGroupVersion.Status.PUBLISHED;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void testGetBundles() throws Exception {
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = populateBundle();
		bundlesList.add(bundle);
		String bundleGroupVersionId = bundle.getBundleGroupVersions().iterator().next().getId().toString();
		Mockito.when(bundleService.getBundles(Optional.of(bundleGroupVersionId))).thenReturn(bundlesList);
		Mockito.when(bundleService.getBundles(Optional.ofNullable(null))).thenReturn(bundlesList);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bundles/").accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
	            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
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
		BundleNoId bundleNoId = new BundleNoId(bundle);
		Mockito.when(bundleService.createBundle(bundleNoId.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		String inputJson = mapToJson(bundleNoId);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundles/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.CREATED.value()));
		
		//Case 2: bundleId is null
		BundleNoId bundleNoId2 = new BundleNoId(null, bundle.getName(), bundle.getDescription(), bundle.getGitRepoAddress(), new ArrayList<>(), new ArrayList<>());
		Mockito.when(bundleService.createBundle(bundleNoId2.createEntity(Optional.empty()))).thenReturn(bundle);
		inputJson = mapToJson(bundleNoId2);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundles/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}
	
	@Test
	public void testUpdateBundle() throws Exception {
		Bundle bundle = populateBundle();
		String bundleId = bundle.getId().toString();
		BundleNoId bundleNoId = new BundleNoId(bundle);
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.of(bundle));
		Mockito.when(bundleService.createBundle(bundleNoId.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		String inputJson = mapToJson(bundleNoId);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/bundles/{bundleId}", bundleId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson))
				.andExpect(status().isOk());
		
	}
	
	@Test
	public void testUpdateBundleFails() throws Exception {
		Bundle bundle = populateBundle();
		String bundleId = bundle.getId().toString();
		BundleNoId bundleNoId = new BundleNoId(bundle);
		Mockito.when(bundleService.getBundle(null)).thenReturn(Optional.of(bundle));
		Mockito.when(bundleService.createBundle(bundleNoId.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		String inputJson = mapToJson(bundleNoId);
		
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
