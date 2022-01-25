package com.entando.hub.catalog.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroupNoId;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.security.SecurityHelperService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class BundleGroupControllerTest {
	
	@InjectMocks
	BundleGroupController bundleGroupController;
	@Mock
	BundleGroupService bundleGroupService;
	@Mock
	CategoryService categoryService;
	@Mock
	SecurityHelperService securityHelperService;
	@Mock
	BundleGroupVersionService bundleGroupVersionService;
	
	@Test
	public void testGetBundleGroupsByOrganisationId() {
		List<BundleGroup> bundleGroupList = new ArrayList<>();
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1001L);
		bundleGroup.setName("New Bundle Group");
		Organisation organisation = new Organisation();
		organisation.setId(2001L);
		organisation.setName("New Organisation");
		organisation.setDescription("Test Description");
		bundleGroup.setOrganisation(organisation);	
		bundleGroupList.add(bundleGroup);
		String organisationId = organisation.getId().toString();
		Mockito.when(bundleGroupService.getBundleGroups(Optional.ofNullable(null))).thenReturn(bundleGroupList);
		Mockito.when(bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId))).thenReturn(bundleGroupList);
		
		//Case 1: no organisation specified
		List<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResultList = bundleGroupController.getBundleGroupsByOrgnisationId(null);
		assertNotNull(bundleGroupResultList);
		assertEquals(bundleGroupList.get(0).getName(), bundleGroupResultList.get(0).getName());
		
		//Case 2: testing with specific organisation
		List<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResultList2 = bundleGroupController.getBundleGroupsByOrgnisationId(organisationId);
		assertNotNull(bundleGroupResultList2);
		assertEquals(bundleGroupList.get(0).getName(), bundleGroupResultList2.get(0).getName());	
	}
	
	@Test
	public void getBundleGroup() {
		List<BundleGroup> bundleGroupList = new ArrayList<>();
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1002L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupList.add(bundleGroup);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResultList = bundleGroupController.getBundleGroup(bundleGroupId);
		assertNotNull(bundleGroupResultList);
		assertEquals(HttpStatus.OK, bundleGroupResultList.getStatusCode());
	}
	
	@Test
	public void getBundleGroupFails() {
		List<BundleGroup> bundleGroupList = new ArrayList<>();
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1003L);
		bundleGroup.setName("New Bundle Group");
		bundleGroupList.add(bundleGroup);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResultList = bundleGroupController.getBundleGroup(bundleGroupId);
		assertNotNull(bundleGroupResultList);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupResultList.getStatusCode());
	}
	
	@Test
	public void testCreateBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1003L);
		bundleGroup.setName("New Bundle Group");
		Organisation organisation = new Organisation();
		organisation.setId(2001L);
		organisation.setName("New Organisation");
		organisation.setDescription("Test Description");
		bundleGroup.setOrganisation(organisation);	
		Category category = new Category();
		category.setId(2001L);
		category.setName("abc");
		category.setDescription("new One");
		category.setBundleGroups(null);
		bundleGroup.setCategories(Set.of(category));
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);
	
		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.empty()), bundleGroupNoId)).thenReturn(bundleGroup);
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResult = bundleGroupController.createBundleGroup(bundleGroupNoId);
		assertNotNull(bundleGroupResult);
		assertEquals(HttpStatus.CREATED, bundleGroupResult.getStatusCode());
		
	}
	
	@Test
	public void testCreateBundleGroupFails() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1003L);
		bundleGroup.setName("New Bundle Group");	
		bundleGroup.setOrganisation(null);	
		bundleGroup.setCategories(null);
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);
		Mockito.when(securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroupNoId.getOrganisationId())).thenReturn(true);
		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.empty()), bundleGroupNoId)).thenReturn(bundleGroup);
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResult = bundleGroupController.createBundleGroup(bundleGroupNoId);
		assertNotNull(bundleGroupResult);
		assertEquals(HttpStatus.FORBIDDEN, bundleGroupResult.getStatusCode());
	}
	
	
	
	@Test
	public void testUpdateBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1008L);
		bundleGroup.setName("New Bundle Group");
		Organisation organisation = new Organisation();
		organisation.setId(2002L);
		organisation.setName("New Organisation");
		organisation.setDescription("Test Description");
		bundleGroup.setOrganisation(organisation);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);
		
		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(true);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(true);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(true);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.empty()), bundleGroupNoId)).thenReturn(bundleGroup);
		Mockito.when(bundleGroupService.createBundleGroup(bundleGroupNoId.createEntity(Optional.of(bundleGroupId)), bundleGroupNoId)).thenReturn(bundleGroup);
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResult = bundleGroupController.updateBundleGroup(bundleGroupId, bundleGroupNoId);
		assertNotNull(bundleGroupResult);
		assertEquals(HttpStatus.OK, bundleGroupResult.getStatusCode());
		
	}
	
	@Test
	public void testUpdateBundleGroupFails() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1008L);
		bundleGroup.setName("New Bundle Group");
		Organisation organisation = new Organisation();
		organisation.setId(2002L);
		organisation.setName("New Organisation");
		organisation.setDescription("Test Description");
		bundleGroup.setOrganisation(organisation);
		String bundleGroupId = Long.toString(bundleGroup.getId());
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);
		
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResult = bundleGroupController.updateBundleGroup(bundleGroupId, bundleGroupNoId);
		assertNotNull(bundleGroupResult);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupResult.getStatusCode());
		
		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(false);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResult2 = bundleGroupController.updateBundleGroup(bundleGroupId, bundleGroupNoId);
		assertNotNull(bundleGroupResult2);
		assertEquals(HttpStatus.CONFLICT, bundleGroupResult2.getStatusCode());
		
		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(true);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(false);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(false);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResult3 = bundleGroupController.updateBundleGroup(bundleGroupId, bundleGroupNoId);
		assertNotNull(bundleGroupResult3);
		assertEquals(HttpStatus.FORBIDDEN, bundleGroupResult3.getStatusCode());
		
		Mockito.when(bundleGroupVersionService.isBundleGroupEditable(bundleGroup)).thenReturn(true);
		Mockito.when(securityHelperService.hasRoles(Set.of(ADMIN))).thenReturn(false);
		Mockito.when(securityHelperService.userIsInTheOrganisation(bundleGroup.getOrganisation().getId())).thenReturn(false);
		bundleGroup.setOrganisation(null);
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		ResponseEntity<com.entando.hub.catalog.rest.BundleGroupController.BundleGroup> bundleGroupResult4 = bundleGroupController.updateBundleGroup(bundleGroupId, bundleGroupNoId);
		assertNotNull(bundleGroupResult4);
		assertEquals(HttpStatus.FORBIDDEN, bundleGroupResult4.getStatusCode());
	}
	
	@Test
	public void testDeleteBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1005L);
		bundleGroup.setName("New Bundle Group");
		String bundleGroupId = Long.toString(bundleGroup.getId());
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(bundleGroup));
		bundleGroupService.deleteBundleGroup(bundleGroupId);
		ResponseEntity<CategoryController.Category> bundleGroupResult = bundleGroupController.deleteBundleGroup(bundleGroupId);
		assertNotNull(bundleGroupResult);
		assertEquals(HttpStatus.NO_CONTENT, bundleGroupResult.getStatusCode());
	}
	
	@Test
	public void testDeleteBundleGroupFails() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1005L);
		bundleGroup.setName("New Bundle Group");
		String bundleGroupId = Long.toString(bundleGroup.getId());
		Mockito.when(bundleGroupService.getBundleGroup(null)).thenReturn(Optional.of(bundleGroup));
		Mockito.when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.empty());
		bundleGroupService.deleteBundleGroup(bundleGroupId);
		ResponseEntity<CategoryController.Category> bundleGroupResult = bundleGroupController.deleteBundleGroup(bundleGroupId);
		assertNotNull(bundleGroupResult);
		assertEquals(HttpStatus.NOT_FOUND, bundleGroupResult.getStatusCode());
	}

}
