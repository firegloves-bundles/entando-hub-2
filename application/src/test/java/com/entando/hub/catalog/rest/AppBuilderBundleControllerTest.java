package com.entando.hub.catalog.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.BundleService;



@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class AppBuilderBundleControllerTest {
	
	@InjectMocks
	AppBuilderBundleController appBuilderBundleController;
	@Mock
	BundleService bundleService;
	@Mock
	BundleGroupVersionService bundleGroupVersionService;
	
	@Test
	public void getBundlesTest() {
		Integer page = 0;
		Integer pageSize = 89;
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1001L);
		bundleGroup.setName("New Bundle Group");
		String bundleGroupId = bundleGroup.getId().toString();
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1002L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = new Bundle();
		bundle.setId(1001L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		bundle.setBundleGroupVersions(new HashSet<>());
		bundlesList.add(bundle);
	
		BundleController.Bundle bundleC = new BundleController.Bundle(bundle);
		List<BundleController.Bundle> bundlesCList = new ArrayList<>();
		bundlesCList.add(bundleC);
		
		Page<Bundle> response = new PageImpl<>(bundlesList);
		
		//Case 1: bundleGroupId not provided, page = 0, bundle has null versions
		Mockito.when(bundleService.getBundles(page, pageSize, Optional.ofNullable(null))).thenReturn(response);
		PagedContent<BundleController.Bundle, Bundle> result = appBuilderBundleController.getBundles(page, pageSize, null);
		assertNotNull(result);
		assertEquals(bundle.getName(), result.getPayload().get(0).getName());
		
		//Case 2: bundle has a version
		bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleService.getBundles(page, pageSize, Optional.ofNullable(null))).thenReturn(response);
		PagedContent<BundleController.Bundle, Bundle> result2 = appBuilderBundleController.getBundles(page, pageSize, null);
		assertNotNull(result2);
		assertEquals(bundle.getName(), result2.getPayload().get(0).getName());
		
		//Case 3: optionalBundleGroup is empty
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.empty());
		PagedContent<BundleController.Bundle, Bundle> result3 = appBuilderBundleController.getBundles(page, pageSize, null);
		assertNotNull(result3);
		assertEquals(bundle.getName(), result3.getPayload().get(0).getName());
		
		//Case 4: bundleGroupId provided, page >= 1
		page = 1;
		Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
		Mockito.when(bundleService.getBundles(sanitizedPageNum, pageSize, Optional.of(bundleGroupId))).thenReturn(response);
		PagedContent<BundleController.Bundle, Bundle> result4 = appBuilderBundleController.getBundles(page, pageSize, bundleGroupId);
		assertNotNull(result4);
		assertEquals(bundle.getName(), result4.getPayload().get(0).getName());
			
	}

}
