package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;
import com.entando.hub.catalog.rest.validation.BundleGroupValidator;
import com.entando.hub.catalog.service.security.SecurityHelperService;
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
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.*;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
@ComponentScan(basePackageClasses = {BundleMapper.class, BundleMapperImpl.class})
public class BundleServiceTest {


	@InjectMocks
	BundleService bundleService;
	@Mock
	BundleMapper bundleMapper;
	@Mock
	BundleRepository bundleRepository;
	@Mock
	BundleGroupRepository bundleGroupRepository;
	@Mock
	BundleGroupVersionRepository bundleGroupVersionRepository;

	@Mock
	SecurityHelperService securityHelperService;
	@Mock
	BundleGroupValidator bundleGroupValidator;

	private static final Long BUNDLE_ID = 1001L; 
	private static final String BUNDLE_NAME = "Test Bundle Name";
	private static final String BUNDLE_DESCRIPTION = "Test Bundle Decription";
	private static final String BUNDLE_GIT_REPO_ADDRESS = "https://github.com/entando/TEST-portal.git";
	private static final String BUNDLE_DEPENDENCIES = "Test Dependencies";

	private static final Long BUNDLE_GROUP_ID = 1000L;
    private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
    
    private static final Long BUNDLE_GROUP_VERSION_ID = 1002L;
    private static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "Test Bundle Group Version Decription";
    private static final String BUNDLE_GROUP_VERSION_VERSION = "v1.0.0";

	@Test
	public void getBundlesPageTest() {
		 Integer pageNum = 1;
		 Integer pageSize = 12;
		 Pageable paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "name"));
		 List<Bundle> bundleList = new ArrayList<>();
		 Bundle bundle = createBundle();
		 BundleGroup bundleGroup = createBundleGroup();
		 List<BundleGroupVersion> bundleGroupVersionList = new ArrayList<>();
		 BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));		
		 bundleList.add(bundle);
		 bundleGroupVersionList.add(bundleGroupVersion);
		 Long bundleGroupId = bundleGroup.getId();
		 
	     Page<Bundle> response = new PageImpl<>(bundleList);

		Set<DescriptorVersion> versions = new HashSet<>();
		versions.add(DescriptorVersion.V1);

		Mockito.when(bundleGroupRepository.findById(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
	     Mockito.when(bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroup, BundleGroupVersion.Status.PUBLISHED)).thenReturn(bundleGroupVersion);
	     Mockito.when(bundleRepository.findByBundleGroupVersionsIsAndDescriptorVersionIn(bundleGroupVersion, versions, paging)).thenReturn(response);
	     Mockito.when(bundleGroupVersionRepository.getPublishedBundleGroups(versions)).thenReturn(bundleGroupVersionList);
	     Mockito.when(bundleRepository.findByBundleGroupVersionsInAndDescriptorVersionIn(bundleGroupVersionList, versions, paging)).thenReturn(response);

	     //Case 1: bundle group id is present, pageSize > 0
	     Page<Bundle> bundleResult = bundleService.getBundles(pageNum, pageSize, Optional.of(bundleGroupId.toString()), versions);
		 assertNotNull(bundleResult);
		 assertEquals(response.getSize(), bundleResult.getSize());
		 
		 //Case 2: bundle group id is not present, pageSize = 0
		 pageSize = 0;
		 paging = Pageable.unpaged();
		Mockito.when(bundleRepository.findByBundleGroupVersionsInAndDescriptorVersionIn(bundleGroupVersionList, versions, paging)).thenReturn(response);
		 Page<Bundle> bundleResult2 = bundleService.getBundles(pageNum, pageSize, Optional.empty(), versions);
		 assertNotNull(bundleResult2);
		 assertEquals(response.getSize(), bundleResult2.getSize());
		 
		//Case 3: bundle group entity is empty
		 Mockito.when(bundleGroupRepository.findById(bundleGroupId)).thenReturn(Optional.empty());
		 Page<Bundle> bundleResult3 = bundleService.getBundles(pageNum, pageSize, Optional.of(bundleGroupId.toString()), versions);
		 assertNotNull(bundleResult3);
		 assertEquals(0, bundleResult3.getSize());
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {ADMIN})
	public void getBundlesTest() {
		 List<Bundle> bundleList = new ArrayList<>();
		 Bundle bundle = createBundle();
		 BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));		
		 bundleList.add(bundle);
		 
		 String bundleGroupVersionId = bundleGroupVersion.getId().toString();

		 Mockito.when(bundleRepository.findByBundleGroupVersionsId(any(), any(Sort.class))).thenReturn(bundleList);
		 Mockito.when(securityHelperService.isAdmin()).thenReturn(true);
		 List<Bundle> bundleResult = bundleService.getBundles(Optional.of(bundleGroupVersionId));
		 assertNotNull(bundleResult);
		 assertEquals(bundleList.get(0).getId(), bundleResult.get(0).getId());
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {ADMIN})
	public void getBundlesTestFails() {
		 List<Bundle> bundleList = new ArrayList<>();
		 Bundle bundle = createBundle();
		 bundleList.add(bundle);
		 Mockito.when(bundleRepository.findAll()).thenReturn(bundleList);
		 Mockito.when(securityHelperService.isAdmin()).thenReturn(true);
		 List<Bundle> bundleResult = bundleService.getBundles(Optional.empty());
		 assertNotNull(bundleResult);
		 assertEquals(bundleList.get(0).getId(),bundleResult.get(0).getId());
	}
	
	@Test
	public void getBundleTest() {
		Bundle bundle = createBundle(); 
		 Optional<Bundle> bundleList = Optional.of(bundle);
		 String bundleId = Long.toString(bundle.getId());
		 Mockito.when(bundleRepository.findById(bundle.getId())).thenReturn(bundleList);
		 Optional<Bundle> bundleResult = bundleService.getBundle(bundleId);
		 assertNotNull(bundleResult);
		 assertEquals(bundleList.get().getId(),bundleResult.get().getId());
	}
	
	  @Test
	  public void createBundleTest() {
		 Bundle bundle = createBundle();
		 Mockito.when(bundleRepository.save(bundle)).thenReturn(bundle);
		 Bundle bundleResult = bundleService.createBundle(bundle);
		 assertNotNull(bundleResult);
		 assertEquals(bundle.getId(),bundleResult.getId());
	  }
	  
	  @Test
	  public void createBundlesTest() {
		 Bundle bundle = createBundle();
		 List<Bundle> bundlesList = new ArrayList<>();
		 bundlesList.add(bundle);	
		 Mockito.when(bundleRepository.saveAll(bundlesList)).thenReturn(bundlesList);
		 
		 List<Bundle> bundleResult = bundleService.createBundles(bundlesList);
		 assertNotNull(bundleResult);
		 assertEquals(bundle.getId(),bundleResult.get(0).getId());
	  }
	  
	  @Test
	  public void createBundleEntitiesAndSaveTest() {
		 Bundle bundle = createBundle();
		 BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));		
		 List<Bundle> bundlesList = new ArrayList<>();
		 bundlesList.add(bundle);	
		 BundleDto bundleDto = bundleMapper.toDto(bundle);
		 List<BundleDto> bundleNoIdsList = new ArrayList<>();
		 bundleNoIdsList.add(bundleDto);


		 Mockito.when(bundleRepository.saveAll(bundlesList)).thenReturn(bundlesList);
			Mockito.when(bundleMapper.toEntity(bundleDto)).thenReturn(bundle);
		 
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
		 Bundle bundle = createBundle();
		 List<Bundle> bundlesList = new ArrayList<>();
		 bundlesList.add(bundle);	
		 
		 BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		 bundleGroupVersion.setBundles(new HashSet<>(bundlesList));
		 bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));	
		 
		 bundleService.deleteBundle(bundle);
//		 assertEquals(0, bundleGroupVersion.getBundles().size());
	}

	private Bundle createBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(BUNDLE_ID);
		bundle.setName(BUNDLE_NAME);
		bundle.setDescription(BUNDLE_DESCRIPTION);
		bundle.setGitRepoAddress(BUNDLE_GIT_REPO_ADDRESS);
		bundle.setDependencies(BUNDLE_DEPENDENCIES);
		return bundle;
	}

	private BundleGroup createBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUP_ID);
		bundleGroup.setName(BUNDLE_GROUP_NAME);
		return bundleGroup;
	}

	private BundleGroupVersion createBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroupVersion.setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion(BUNDLE_GROUP_VERSION_VERSION);
		return bundleGroupVersion;
	}
}
	