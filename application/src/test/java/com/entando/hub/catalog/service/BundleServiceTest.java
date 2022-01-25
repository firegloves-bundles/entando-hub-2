package com.entando.hub.catalog.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class BundleServiceTest {
	@InjectMocks
	BundleService bundleService;
	@Mock
	BundleRepository bundleRepository;
	@Mock
	BundleGroupRepository bundleGroupRepository;
	@Mock
	BundleGroupVersionRepository bundleGroupVersionRepository;
	
	private final Logger logger = LoggerFactory.getLogger(BundleServiceTest.class);

	@Test
	public void getBundlesPageTest() {
		 Integer pageNum = 78;
		 Integer pageSize = 89;
		 Pageable paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "name"));
		 List<Bundle> bundleList = new ArrayList<>();
		 Bundle bundle = new Bundle();
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 BundleGroup bundleGroup = new BundleGroup();
		 bundleGroup.setId(101L);
		 List<BundleGroupVersion> bundleGroupVersionList = new ArrayList<>();
		 BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		 bundleGroupVersion.setId(2001L);
		 bundleGroupVersion.setBundleGroup(bundleGroup);		 
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));		
		 bundleList.add(bundle);
		 bundleGroupVersionList.add(bundleGroupVersion);
		 Long bundleGroupId = bundleGroup.getId();
		 
	     Page<Bundle> response = new PageImpl<>(bundleList);
	     
	     Mockito.when(bundleGroupRepository.findById(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
	     Mockito.when(bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroup, BundleGroupVersion.Status.PUBLISHED)).thenReturn(bundleGroupVersion);
	     Mockito.when(bundleRepository.findByBundleGroupVersionsIs(bundleGroupVersion, paging)).thenReturn(response);
	     Mockito.when(bundleGroupVersionRepository.findDistinctByStatus(BundleGroupVersion.Status.PUBLISHED)).thenReturn(bundleGroupVersionList);
	     Mockito.when(bundleRepository.findByBundleGroupVersionsIn(bundleGroupVersionList, paging)).thenReturn(response);
	     
	     //Case 1: bundle group id is present, pageSize > 0
	     Page<Bundle> bundleResult = bundleService.getBundles(pageNum, pageSize, Optional.of(bundleGroupId.toString()));
		 assertNotNull(bundleResult);
		 assertEquals(response.getSize(), bundleResult.getSize());
		 
		 //Case 2: bundle group id is not present, pageSize = 0
		 pageSize = 0;
		 paging = Pageable.unpaged();
		 Mockito.when(bundleRepository.findByBundleGroupVersionsIn(bundleGroupVersionList, paging)).thenReturn(response);
		 Page<Bundle> bundleResult2 = bundleService.getBundles(pageNum, pageSize, Optional.empty());
		 assertNotNull(bundleResult2);
		 assertEquals(response.getSize(), bundleResult2.getSize());
		 
		//Case 3: bundle group entity is empty
		 Mockito.when(bundleGroupRepository.findById(bundleGroupId)).thenReturn(Optional.empty());
		 Page<Bundle> bundleResult3 = bundleService.getBundles(pageNum, pageSize, Optional.of(bundleGroupId.toString()));
		 assertNotNull(bundleResult3);
		 assertEquals(0, bundleResult3.getSize());
	}
	
	@Test
	public void getBundlesTest() {
		 List<Bundle> bundleList = new ArrayList<>();
		 Bundle bundle = new Bundle();
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		 bundleGroupVersion.setId(2001L);
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));		
		 bundleList.add(bundle);
		 
		 String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		 Mockito.when(bundleRepository.findByBundleGroupVersionsIs(any(BundleGroupVersion.class))).thenReturn(bundleList);
		 List<Bundle> bundleResult = bundleService.getBundles(Optional.of(bundleGroupVersionId));
		 assertNotNull(bundleResult);
		 assertEquals(bundleList.get(0).getId(), bundleResult.get(0).getId());
	}
	
	@Test
	public void getBundlesTestFails() {
		 List<Bundle> bundleList = new ArrayList<>();
		 Bundle bundle = new Bundle();
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 bundleList.add(bundle);
		 BundleGroup bundleGroupEntity = new BundleGroup();
		 bundleGroupEntity.setId(101L);
		 Optional <String> bundleGroupId = Optional.empty(); ;
		 Mockito.when(bundleRepository.findAll()).thenReturn(bundleList);
		 List<Bundle> bundleResult = bundleService.getBundles(Optional.empty());
		 assertNotNull(bundleResult);
		 assertEquals(bundleList.get(0).getId(),bundleResult.get(0).getId());
	}
	
	@Test
	public void getBundleTest() {
		Bundle bundle = new Bundle(); 
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 Optional<Bundle> bundleList = Optional.of(bundle);
		 String bundleId = Long.toString(bundle.getId());
		 Mockito.when(bundleRepository.findById(bundle.getId())).thenReturn(bundleList);
		 Optional<Bundle> bundleResult = bundleService.getBundle(bundleId);
		 assertNotNull(bundleResult);
		 assertEquals(bundleList.get().getId(),bundleResult.get().getId());
	}
	
	  @Test
	  public void createBundleTest() {
		 Bundle bundle = new Bundle();
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 Mockito.when(bundleRepository.save(bundle)).thenReturn(bundle);
		 Bundle bundleResult = bundleService.createBundle(bundle);
		 assertNotNull(bundleResult);
		 assertEquals(bundle.getId(),bundleResult.getId());
	  }
	  
	  @Test
	  public void createBundlesTest() {
		 Bundle bundle = new Bundle();
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 List<Bundle> bundlesList = new ArrayList<>();
		 bundlesList.add(bundle);	
		 Mockito.when(bundleRepository.saveAll(bundlesList)).thenReturn(bundlesList);
		 
		 List<Bundle> bundleResult = bundleService.createBundles(bundlesList);
		 assertNotNull(bundleResult);
		 assertEquals(bundle.getId(),bundleResult.get(0).getId());
	  }
	  
	  @Test
	  public void createBundleEntitiesAndSaveTest() {
		 Bundle bundle = new Bundle();
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		 bundleGroupVersion.setId(2001L);
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));		
		 List<Bundle> bundlesList = new ArrayList<>();
		 bundlesList.add(bundle);	
		 BundleNoId bundleNoId = new BundleNoId(bundle);
		 List<BundleNoId> bundleNoIdsList = new ArrayList<>();
		 bundleNoIdsList.add(bundleNoId);	
		 
		 Mockito.when(bundleRepository.saveAll(bundlesList)).thenReturn(bundlesList);
		 
		 //Case 1: bundleRequest given
		 List<Bundle> bundleResult = bundleService.createBundleEntitiesAndSave(bundleNoIdsList);
		 assertNotNull(bundleResult);
		 assertEquals(bundle.getId(),bundleResult.get(0).getId());
		 
		//Case 1: bundleRequest empty
		 List<Bundle> bundleResult2 = bundleService.createBundleEntitiesAndSave(new ArrayList<>());
		 assertNotNull(bundleResult2);
		 assertEquals(0,bundleResult2.size());
	  }
	  
	  @Test
	  public void deleteBundleTest() {
		 Bundle bundle = new Bundle();
		 bundle.setId(1001L);
		 bundle.setName("bytes");
		 bundle.setDescription("tree");
		 bundle.setGitRepoAddress("new");
		 bundle.setDependencies("xyz");
		 List<Bundle> bundlesList = new ArrayList<>();
		 bundlesList.add(bundle);	
		 
		 BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		 bundleGroupVersion.setId(2001L);
		 bundleGroupVersion.setBundles(new HashSet<>(bundlesList));
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));	
		 
		 bundleService.deleteBundle(bundle);
//		 assertEquals(0, bundleGroupVersion.getBundles().size());
	  }  
}
	