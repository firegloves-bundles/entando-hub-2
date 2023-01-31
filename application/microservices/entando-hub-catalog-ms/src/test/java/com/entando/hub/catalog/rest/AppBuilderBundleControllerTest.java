package com.entando.hub.catalog.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.BundleService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(AppBuilderBundleController.class)
public class AppBuilderBundleControllerTest {

	@Autowired
	WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

	@InjectMocks
	AppBuilderBundleController appBuilderBundleController;

	@MockBean
	BundleService bundleService;

	@MockBean
	BundleGroupVersionService bundleGroupVersionService;

    private static final String URI = "/appbuilder/api/bundles/";

    private static final String PAGE_PARAM = "page";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String DESCRIPTOR_VERSIONS = "descriptorVersions";

    private static final Long BUNDLE_GROUP_ID = 1000L;
    private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
    
    private static final Long BUNDLE_GROUP_VERSION_ID = 1002L;
    private static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "Test Bundle Group Version Decription";
    private static final String BUNDLE_GROUP_VERSION_VERSION = "v1.0.0";
    
    private static final Long BUNDLE_ID = 1001L;
    private static final String BUNDLE_NAME = "Test Bundle Name";
    private static final String BUNDLE_DESCRIPTION = "Test Bundle Decription";
    private static final String BUNDLE_GIT_REPO_ADDRESS = "https://github.com/entando/TEST-portal.git";
    private static final String BUNDLE_DEPENDENCIES = "Test Dependencies";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
	public void getBundlesTest() throws Exception {
		Integer page = 0;
		Integer pageSize = 89;
		BundleGroup bundleGroup = getBundleGroupObj();
		String bundleGroupId = bundleGroup.getId().toString();

		BundleGroupVersion bundleGroupVersion = getBundleGroupVersionObj();
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = getBundleObj();
		bundlesList.add(bundle);
	
		BundleController.Bundle bundleC = new BundleController.Bundle(bundle);
		List<BundleController.Bundle> bundlesCList = new ArrayList<>();
		bundlesCList.add(bundleC);
		
		Page<Bundle> response = new PageImpl<>(bundlesList);

		Set<Bundle.DescriptorVersion> versions = new HashSet<>();
		versions.add(Bundle.DescriptorVersion.V1);

		//Case 1: bundleGroupId not provided, page = 0, bundle has null versions
		Mockito.when(bundleService.getBundles(page, pageSize, Optional.ofNullable(null), versions)).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.get(URI)
				.param(PAGE_PARAM, page.toString())
		        .param(PAGE_SIZE_PARAM, pageSize.toString()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.payload").exists())
				.andExpect(jsonPath("$.metadata").exists())
				.andExpect(status().isOk());
		
		//Case 2: bundle has a version
		bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleService.getBundles(page, pageSize, Optional.ofNullable(null), versions)).thenReturn(response);
		
		mockMvc.perform(MockMvcRequestBuilders.get(URI)
				.param(PAGE_PARAM, page.toString())
		        .param(PAGE_SIZE_PARAM, pageSize.toString()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.payload").exists())
				.andExpect(jsonPath("$.metadata").exists())
				.andExpect(status().isOk());

//		//Case 3: optionalBundleGroup is empty
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.empty());
		mockMvc.perform(MockMvcRequestBuilders.get(URI)
				.param(PAGE_PARAM, page.toString())
		        .param(PAGE_SIZE_PARAM, pageSize.toString()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.payload").exists())
				.andExpect(jsonPath("$.metadata").exists())
				.andExpect(status().isOk());

//		//Case 4: bundleGroupId provided, page >= 1
		page = 1;
		Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
		Mockito.when(bundleService.getBundles(sanitizedPageNum, pageSize, Optional.of(bundleGroupId), versions)).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.get(URI)
				.param(PAGE_PARAM, page.toString())
		        .param(PAGE_SIZE_PARAM, pageSize.toString()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.payload").hasJsonPath())
				.andExpect(jsonPath("$.metadata").hasJsonPath())
				.andExpect(status().isOk());

		//Case 5: provide one more good descriptorVersion as well as a bad one (which should be excluded.
		versions.add(Bundle.DescriptorVersion.V5);
		page = 1;

		bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleService.getBundles(page, pageSize, Optional.ofNullable(null), versions)).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.get(URI)
				.param(PAGE_PARAM, page.toString())
				.param(PAGE_SIZE_PARAM, pageSize.toString())
				.param(DESCRIPTOR_VERSIONS, "v1")
				.param(DESCRIPTOR_VERSIONS, "v5")
				.param(DESCRIPTOR_VERSIONS, "vInvalid"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.payload").exists())
				.andExpect(jsonPath("$.metadata").exists())
				.andExpect(status().isOk());
	}

    private Bundle getBundleObj() {
    	Bundle bundle = new Bundle();
		bundle.setId(BUNDLE_ID);
		bundle.setName(BUNDLE_NAME);
		bundle.setDescription(BUNDLE_DESCRIPTION);
		bundle.setGitRepoAddress(BUNDLE_GIT_REPO_ADDRESS);
		bundle.setDependencies(BUNDLE_DEPENDENCIES);
		bundle.setBundleGroupVersions(new HashSet<>());
		return bundle;
	}

    private BundleGroup getBundleGroupObj() {
    	BundleGroup bundleGroup = new BundleGroup();
    	bundleGroup.setId(BUNDLE_GROUP_ID);
    	bundleGroup.setName(BUNDLE_GROUP_NAME);
    	return bundleGroup;
    }

    private BundleGroupVersion getBundleGroupVersionObj() {
    	BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroupVersion.setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion(BUNDLE_GROUP_VERSION_VERSION);
		return bundleGroupVersion;
	}
}
