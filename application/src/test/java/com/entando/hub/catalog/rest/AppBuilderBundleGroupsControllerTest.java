package com.entando.hub.catalog.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class AppBuilderBundleGroupsControllerTest {
	
	@InjectMocks
	AppBuilderBundleGroupsController appBuilderBundleGroupsController;
	@Mock
	BundleGroupVersionController bundleGroupVersionController;
	
	@Test
	public void getBundleGroupVersionsAndFilterThemTest() {
		Category category = new Category();
		category.setId(2001L);
		category.setName("abc");
		category.setDescription("new One");
		category.setBundleGroups(null);
		String[] categoryIds = new String[]{category.getId().toString()};
		
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
		String organisationId = organisation.getId().toString();
		Integer page = 0;
		Integer pageSize = 89;
		String[] statuses =  Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
		
		List<BundleGroupVersionFilteredResponseView> list = new ArrayList<>();
		BundleGroupVersionFilteredResponseView viewObj = new BundleGroupVersionFilteredResponseView();
		viewObj.setBundleGroupVersionId(bundleGroupVersion.getId());
		viewObj.setDescription(bundleGroupVersion.getDescription());
		viewObj.setStatus(bundleGroupVersion.getStatus());
		viewObj.setVersion(bundleGroupVersion.getVersion());
		list.add(viewObj);
		
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> pagedContent = new PagedContent<>(list, response);
		
		//Case 1: all optional parameters given
		Mockito.when(bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, statuses, null)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> result = appBuilderBundleGroupsController.getBundleGroupVersionsAndFilterThem(page, pageSize, organisationId, categoryIds, statuses);
		assertNotNull(result);
		assertEquals(bundleGroupVersionsList.get(0).getId(), result.getPayload().get(0).getBundleGroupVersionId());
		
		//Case 2: when statuses list is null
		String[] defaultStatuses = new String[1];
		defaultStatuses[0] = BundleGroupVersion.Status.PUBLISHED.toString();
		Mockito.when(bundleGroupVersionController.getBundleGroupsAndFilterThem(page, pageSize, organisationId, categoryIds, defaultStatuses, null)).thenReturn(pagedContent);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> result2 = appBuilderBundleGroupsController.getBundleGroupVersionsAndFilterThem(page, pageSize, organisationId, categoryIds, null);
		assertNotNull(result2);
		assertEquals(bundleGroupVersionsList.get(0).getId(), result2.getPayload().get(0).getBundleGroupVersionId());
			
	}

	
	
	
	

}
