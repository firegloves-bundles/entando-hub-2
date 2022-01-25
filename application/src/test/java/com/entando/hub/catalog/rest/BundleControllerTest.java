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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;
import com.entando.hub.catalog.service.BundleService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class BundleControllerTest {
	
	@InjectMocks
	BundleController bundleController;
	@Mock
	BundleService bundleService;
	
	@Test
	public void testGetBundles() {
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = new Bundle();
		bundle.setId(1001L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(5001L);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		bundlesList.add(bundle);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleService.getBundles(Optional.ofNullable(null))).thenReturn(bundlesList);
		Mockito.when(bundleService.getBundles(Optional.of(bundleGroupVersionId))).thenReturn(bundlesList);
		
		// Case 1: no bundle group version specified
		List<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResultList = bundleController.getBundles(null);
		assertNotNull(bundleResultList);
		assertEquals(bundleResultList.get(0).getName(), bundlesList.get(0).getName());
		
		//Case 2: bundle group version specified
		List<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResultList2 = bundleController.getBundles(bundleGroupVersionId);
		assertNotNull(bundleResultList2);
		assertEquals(bundleResultList2.get(0).getName(), bundlesList.get(0).getName());
	}
	
	@Test
	public void testGetBundle() {
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = new Bundle();
		bundle.setId(1002L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(5001L);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		bundlesList.add(bundle);
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.of(bundle));
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResultList = bundleController.getBundle(bundleId);
		assertNotNull(bundleResultList);
		assertEquals(HttpStatus.OK, bundleResultList.getStatusCode());
	}

	@Test
	public void testGetBundleFails() {
		List<Bundle> bundlesList = new ArrayList<>();
		Bundle bundle = new Bundle();
		bundle.setId(1003L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		bundlesList.add(bundle);
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(null)).thenReturn(Optional.empty());
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResultList = bundleController.getBundle(bundleId);
		assertNotNull(bundleResultList);
		assertEquals(HttpStatus.NOT_FOUND, bundleResultList.getStatusCode());
	}
	
	@Test
	public void testCreateBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(1004L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(5001L);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		String bundleId = bundle.getId().toString();
		BundleNoId bundleNoId = new BundleNoId(bundle);
		
		//Case 1: bundleId is not null
		Mockito.when(bundleService.createBundle(bundleNoId.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResult = bundleController.createBundle(bundleNoId);
		assertNotNull(bundleResult);
		assertEquals(HttpStatus.CREATED, bundleResult.getStatusCode());
		
		//Case 2: bundleId is null
		BundleNoId bundleNoId2 = new BundleNoId(null, bundle.getName(), bundle.getDescription(), bundle.getGitRepoAddress(), new ArrayList<>(), new ArrayList<>());
		Mockito.when(bundleService.createBundle(bundleNoId2.createEntity(Optional.empty()))).thenReturn(bundle);
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResult2 = bundleController.createBundle(bundleNoId2);
		assertNotNull(bundleResult2);
		assertEquals(HttpStatus.CREATED, bundleResult2.getStatusCode());
	}
	
	@Test
	public void testUpdateBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(1005L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(5001L);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		String bundleId = bundle.getId().toString();
		BundleNoId bundleNoId = new BundleNoId(bundle);
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.of(bundle));
		Mockito.when(bundleService.createBundle(bundleNoId.createEntity(Optional.of(bundleId)))).thenReturn(bundle);
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResult = bundleController.updateBundle(bundleId, bundleNoId);
		assertNotNull(bundleResult);
		assertEquals(HttpStatus.OK, bundleResult.getStatusCode());
	}
	
	@Test
	public void testUpdateBundleFails() {
		Bundle bundle = new Bundle();
		bundle.setId(1005L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(5001L);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		String bundleId = bundle.getId().toString();
		BundleNoId bundleNoId = new BundleNoId(bundle);
		Mockito.when(bundleService.getBundle(null)).thenReturn(Optional.of(bundle));
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResult = bundleController.updateBundle(bundleId, bundleNoId);
		assertNotNull(bundleResult);
		assertEquals(HttpStatus.NOT_FOUND, bundleResult.getStatusCode());
	}
	
	@Test
	public void testDeleteBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(1005L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(5001L);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.of(bundle));
		bundleService.deleteBundle(bundle);
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResult = bundleController.deleteBundle(bundleId);
		assertNotNull(bundleResult);
		assertEquals(HttpStatus.OK, bundleResult.getStatusCode());
	}
	
	@Test
	public void testDeleteBundleFails() {
		Bundle bundle = new Bundle();
		bundle.setId(1005L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(5001L);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		Set<BundleGroupVersion> versions = new HashSet<>();
		versions.add(bundleGroupVersion);
		bundle.setBundleGroupVersions(versions);
		String bundleId = bundle.getId().toString();
		Mockito.when(bundleService.getBundle(bundleId)).thenReturn(Optional.empty());
		bundleService.deleteBundle(bundle);
		ResponseEntity<com.entando.hub.catalog.rest.BundleController.Bundle> bundleResult = bundleController.deleteBundle(bundleId);
		assertNotNull(bundleResult);
		assertEquals(HttpStatus.NO_CONTENT, bundleResult.getStatusCode());
	}
}
