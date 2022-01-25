package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.security.SecurityHelperService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class BundleGroupVersionControllerTest {

	@InjectMocks
	BundleGroupVersionController bundleGroupVersionController;
	@Mock
	BundleGroupVersionService bundleGroupVersionService;
	@Mock
	BundleGroupService bundleGroupService;
	@Mock
	CategoryService categoryService;
	@Mock
	SecurityHelperService securityHelperService;

	@Test
	public void testGetBundleGroupVersions() {
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1001L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionsList.add(bundleGroupVersion);
		String bundleGroupId = bundleGroup.getId().toString();
		
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
		
		//Case 1: bundle group exists
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, statuses,bundleGroup)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionController.getBundleGroupVersions(bundleGroupId, page, pageSize, statuses);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(bundleGroupVersionResult.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 2: bundle group does not exist
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.empty());
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, statuses,bundleGroup)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult2 = bundleGroupVersionController.getBundleGroupVersions(bundleGroupId, page, pageSize, statuses);
		assertNull(bundleGroupVersionResult2);
		
		//Case 3: statuses list is null
		statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, statuses, bundleGroup)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult3 = bundleGroupVersionController.getBundleGroupVersions(bundleGroupId, page, pageSize, null);
		assertNotNull(bundleGroupVersionResult3);
		assertEquals(bundleGroupVersionResult3.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 4: page number >= 1
		page = 1;
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, statuses,bundleGroup)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult4 = bundleGroupVersionController.getBundleGroupVersions(bundleGroupId, page, pageSize, statuses);
		assertNotNull(bundleGroupVersionResult4);
		assertEquals(bundleGroupVersionResult4.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
	}
	
	@Test
	public void testGetBundleGroupVersionsAndFilterThem() {
		List<Category> categoryList = new ArrayList<>();
		Category category = new Category();
		category.setId(2001L);
		category.setName("abc");
		category.setDescription("new One");
		category.setBundleGroups(null);
		categoryList.add(category);
		String[] categoryIds = new String[]{category.getId().toString()};
		Mockito.when(categoryService.getCategories()).thenReturn(categoryList);
		
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1002L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionsList.add(bundleGroupVersion);
		Organisation organisation = new Organisation();
		organisation.setId(2001L);
		organisation.setName("New Organisation");
		organisation.setDescription("Test Description");
		bundleGroup.setOrganisation(organisation);	
		String bundleGroupId = bundleGroup.getId().toString();
		String organisationId = organisation.getId().toString();
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
		
		//Case 1: all optional parameters given
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> pagedContent = new PagedContent<>(list, response);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));		//Mockito.when(bundleGroupVersionService.getBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, Optional.empty())).thenReturn(pagedContent);
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, null)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, statuses, null);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(bundleGroupVersionResult.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 2: when categories list is null
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult2 = bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, null, statuses, null);
		assertNotNull(bundleGroupVersionResult2);
		assertEquals(bundleGroupVersionResult2.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 3: when statuses list is null
		statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, Optional.of(organisationId), categoryIds, statuses, null)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult3 = bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, null, null);
		assertNotNull(bundleGroupVersionResult3);
		assertEquals(bundleGroupVersionResult3.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 4: page number >= 1
		page = 1;
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> bundleGroupVersionResult4 = bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, statuses, null);
		assertNotNull(bundleGroupVersionResult4);
		assertEquals(bundleGroupVersionResult4.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
	}

	@Test
	public void testGetBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1003L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView> bundleGroupVersionResult = bundleGroupVersionController.getBundleGroupVersion(bundleGroupVersionId);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.OK, bundleGroupVersionResult.getStatusCode());
	}

	@Test
	public void testGetBundleGroupVersionFails() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1004L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(null)).thenReturn(Optional.empty());
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView> bundleGroupVersionResult = bundleGroupVersionController.getBundleGroupVersion(bundleGroupVersionId);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupVersionResult.getStatusCode());
	}

	@Test
	public void testCreateBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1004L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setDescriptionImage("Test Description Image");
		bundleGroupVersion.setDocumentationUrl("www.test.com");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.NOT_PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2005L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Bundle bundle = new Bundle();
		bundle.setId(1005L);
		bundle.setName("Test");
		bundle.setDescription("Test Description");
		bundle.setGitRepoAddress("Test Git Rep");
		bundle.setDependencies("Test Dependencies");
		bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
		bundleGroupVersion.setBundles(Set.of(bundle));
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupVersion);
		Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = Optional.of(bundleGroup);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupVersionView.getBundleGroupId().toString())).thenReturn(bundleGroupOptional);
		BundleGroupVersion bundleGroupVersionEntity = bundleGroupVersionView.createEntity(Optional.empty(), bundleGroupOptional.get());
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(new ArrayList<BundleGroupVersion>());
    	Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn(bundleGroupVersion);
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionController.createBundleGroupVersion(bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.CREATED, bundleGroupVersionResult.getStatusCode());
		
	}
	
	@Test
	public void testCreateBundleGroupVersionFails() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1006L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.NOT_PUBLISHED);
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		List<BundleGroupVersion> versions = new ArrayList<>();
		versions.add(bundleGroupVersion);
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupVersion);
		
		//Case 1: bundle group does not exist
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(new ArrayList<BundleGroupVersion>());
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroup), bundleGroupVersionView)).thenReturn(bundleGroupVersion);
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionController.createBundleGroupVersion(bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupVersionResult.getStatusCode());
		
		//Case 2: when there already exists a version
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupVersionView.getBundleGroupId().toString())).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersionView.getVersion())).thenReturn(versions);
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.empty(), bundleGroup), bundleGroupVersionView)).thenReturn(bundleGroupVersion);
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult2 = bundleGroupVersionController.createBundleGroupVersion(bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult2);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupVersionResult2.getStatusCode());
	}

	@Test
	public void testUpdateBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1007L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setDescriptionImage("Test Description Image");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.NOT_PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		String bundleGroupId = bundleGroup.getId().toString();
		Organisation organisation = new Organisation();
		organisation.setId(2002L);
		organisation.setName("New Organisation");
		organisation.setDescription("Test Description");
		bundleGroup.setOrganisation(organisation);
		bundleGroupVersion.setBundleGroup(bundleGroup);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupId, bundleGroupVersion.getDescription(), bundleGroupVersion.getDescriptionImage(), bundleGroupVersion.getVersion());
		
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));		
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionController.updateBundleGroupVersion(bundleGroupVersionId, bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.OK, bundleGroupVersionResult.getStatusCode());
	}

	@Test
	public void testUpdateBundleGroupVersionFails() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1007L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setDescriptionImage("Test Description Image");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.NOT_PUBLISHED);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		String bundleGroupId = bundleGroup.getId().toString();
		Organisation organisation = new Organisation();
		organisation.setId(2002L);
		organisation.setName("New Organisation");
		organisation.setDescription("Test Description");
		bundleGroup.setOrganisation(organisation);
		bundleGroupVersion.setBundleGroup(bundleGroup);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupId, bundleGroupVersion.getDescription(), bundleGroupVersion.getDescriptionImage(), bundleGroupVersion.getVersion());
		
		//Case 1: bundle group version does not exist
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(null)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionController.updateBundleGroupVersion(bundleGroupVersionId, bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupVersionResult.getStatusCode());
		
		//Case 2: user is not admin
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(false);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult2 = bundleGroupVersionController.updateBundleGroupVersion(bundleGroupVersionId, bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult2);
		assertEquals(HttpStatus.OK, bundleGroupVersionResult2.getStatusCode());
		
		//Case 2: user is not an admin and user is not in the organisation
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(false);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult3 = bundleGroupVersionController.updateBundleGroupVersion(bundleGroupVersionId, bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult3);
		assertEquals(HttpStatus.FORBIDDEN, bundleGroupVersionResult3.getStatusCode());
		
		//Case 3: user is not an admin and organisation is null
		bundleGroupVersion.setBundleGroup(null);
		bundleGroup.setOrganisation(null);
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(null)).thenReturn(Optional.of(bundleGroupVersion));
		//Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(true);
		Mockito.when(bundleGroupVersionService.createBundleGroupVersion(any(BundleGroupVersion.class), eq(bundleGroupVersionView))).thenReturn((bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersion> bundleGroupVersionResult4 = bundleGroupVersionController.updateBundleGroupVersion(bundleGroupVersionId, bundleGroupVersionView);
		assertNotNull(bundleGroupVersionResult4);
		assertEquals(HttpStatus.FORBIDDEN, bundleGroupVersionResult4.getStatusCode());
		
		//TODO: Case 4: both organisation is null, user does not belong to org

	}
	
	@Test
	public void testDeleteBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1008L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.DELETE_REQ);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView> bundleGroupVersionResult = bundleGroupVersionController.deleteBundleGroupVersion(bundleGroupVersionId);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.OK, bundleGroupVersionResult.getStatusCode());
	}

	@Test
	public void testDeleteBundleGroupVersionFails() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(1009L);
		bundleGroupVersion.setDescription("Test Description");
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.DELETE_REQ);
		bundleGroupVersion.setVersion("v1.0.0");
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(2001L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupVersion.setBundleGroup(bundleGroup);
		String bundleGroupVersionId = bundleGroupVersion.getId().toString();
		
		//Case 1: when bundle group version does not exist
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.empty());
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView> bundleGroupVersionResult = bundleGroupVersionController.deleteBundleGroupVersion(bundleGroupVersionId);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupVersionResult.getStatusCode());
		
		//Case 2: when bundle group version is not in delete request status
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.NOT_PUBLISHED);
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.of(bundleGroupVersion));
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView> bundleGroupVersionResult2 = bundleGroupVersionController.deleteBundleGroupVersion(bundleGroupVersionId);
		assertNotNull(bundleGroupVersionResult2);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupVersionResult2.getStatusCode());
		
		//Case 3: when bundle group version does not exist and is not in delete request status
		Mockito.when(bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId)).thenReturn(Optional.empty());
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView> bundleGroupVersionResult3 = bundleGroupVersionController.deleteBundleGroupVersion(bundleGroupVersionId);
		assertNotNull(bundleGroupVersionResult3);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupVersionResult3.getStatusCode());
	}

}
