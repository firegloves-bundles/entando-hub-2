package com.entando.hub.catalog.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.springframework.data.domain.Pageable;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroupNoId;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class BundleGroupServiceTest {
	@InjectMocks
	BundleGroupService bundleGroupService;
	@Mock
	BundleGroupRepository bundleGroupRepository;
	@Mock
	CategoryRepository categoryRepository;
	@Mock
	BundleGroupVersionService bundleGroupVersionService;
	
	private final Long BUNDLE_GROUP_VERSION_ID =  2001L;
	private final Long BUNDLE_GROUPID =  2002L;
	private final Long CATEGORY_ID =  2003L;
	private final Long ORG_ID =  2005L;
	private final String NAME = "New Name";
	private final String DESCRIPTION = "New Description";
	private final String DESCRIPTION_IMAGE = "New Description Image";
	private final String DOCUMENTATION_URL = "New Documentation Url";
	private final String VERSION = "V1.V2";
	private final String DEPENDENCIES = "Test Dependencies";
	
	@Test
	public void getBundleGroupsTests() {
		List<BundleGroup> bundleGrouplist = new ArrayList<>();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGrouplist.add(bundleGroup);
		Organisation organisation = bundleGroup.getOrganisation();
		Optional<String> organisationId =  Optional.of(String.valueOf(organisation.getId()));
		Mockito.when(bundleGroupRepository.findByOrganisationId(organisation.getId())).thenReturn(bundleGrouplist);
		List<BundleGroup> bundleGroupresult = bundleGroupService.getBundleGroups(organisationId);
		assertNotNull(bundleGroupresult);
		assertEquals(bundleGrouplist.get(0).getName(), bundleGroupresult.get(0).getName());
	}
	
	@Test
	public void getBundleGroupsTestsFails() {
		List<BundleGroup> bundleGrouplist = new ArrayList<>();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGrouplist.add(bundleGroup);
		Optional<String> organisationId = Optional.empty();
		Mockito.when(bundleGroupRepository.findAll()).thenReturn(bundleGrouplist);
		List<BundleGroup> bundleGroupresult = bundleGroupService.getBundleGroups(organisationId);
		assertNotNull(bundleGroupresult);
		assertEquals(bundleGrouplist.get(0).getName(), bundleGroupresult.get(0).getName());
	}
	
	@Test
	public void findByOrganisationId() {
		BundleGroup bundleGroup = createBundleGroup();
		Organisation organisation = bundleGroup.getOrganisation();
		String organisationId =  String.valueOf(organisation.getId());
		Pageable pageable = null;
		Page<BundleGroup> bundleGrouplist = new PageImpl<>(new ArrayList<BundleGroup>());
		Mockito.when(bundleGroupRepository.findByOrganisationId(organisation.getId(), pageable)).thenReturn(bundleGrouplist);
		Page<BundleGroup> bundleGroupresult = bundleGroupService.findByOrganisationId(organisationId,pageable);
		assertNotNull(bundleGroupresult);
		assertEquals(bundleGrouplist.getSize(),bundleGroupresult.getSize());
	}
	
	@Test
	public void getBundleGroupsTest() {
		List<BundleGroup> bundleGrouplist = new ArrayList<>();
		BundleGroup bundleGroup = createBundleGroup();
		Organisation organisation = bundleGroup.getOrganisation();
		Category category = bundleGroup.getCategories().iterator().next();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGrouplist.add(bundleGroup);
		Optional<String> organisationId =  Optional.of(String.valueOf(organisation.getId()));
		String[] categoryId =  {category.getId().toString()};
		Optional<String[]> categoryIds = Optional.of(categoryId);
		String[] statuse =  {bundleGroupVersion.getStatus().toString()};
		Optional<String[]> statuses = Optional.of(statuse);
		Mockito.when(bundleGroupRepository.findByOrganisationId(Long.parseLong(organisationId.get()))).thenReturn(bundleGrouplist);
		List<BundleGroup> bundleGroupresult = bundleGroupService.getBundleGroups(organisationId, categoryIds, statuses);
		assertNotNull(bundleGroupresult);
		assertEquals(bundleGrouplist.get(0).getId(), bundleGroupresult.get(0).getId());
	}
	
	@Test
	public void getBundleGroupsTestFails() {
		List<BundleGroup> bundleGrouplist = new ArrayList<>();
		BundleGroup bundleGroup = createBundleGroup();
		Category category = bundleGroup.getCategories().iterator().next();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGrouplist.add(bundleGroup);
		Optional<String> organisationId =  Optional.empty();
		String[] categoryId =  {category.getId().toString()};
		Optional<String[]> categoryIds = Optional.of(categoryId);
		String[] statuse =  {bundleGroupVersion.getStatus().toString()};
		Optional<String[]> statuses = Optional.of(statuse);
		Mockito.when(bundleGroupRepository.findAll()).thenReturn(bundleGrouplist);
		List<BundleGroup> bundleGroupresult = bundleGroupService.getBundleGroups(organisationId, categoryIds, statuses);
		assertNotNull(bundleGroupresult);
		assertEquals(bundleGrouplist.get(0).getId(), bundleGroupresult.get(0).getId());
	}
	
	@Test
	public void getBundleGroupTest() {
		BundleGroup bundleGroup = createBundleGroup();
		Organisation organisation = bundleGroup.getOrganisation();
		Long organisationId =  organisation.getId();
		Optional<BundleGroup> bundleGrouplist = Optional.of(bundleGroup);
		Mockito.when(bundleGroupRepository.findById(organisation.getId())).thenReturn(bundleGrouplist);
		Optional<BundleGroup> bundleGroupresult = bundleGroupService.getBundleGroup(organisationId);
		assertNotNull(bundleGroupresult);
		assertEquals(bundleGrouplist.get().getId(), bundleGroupresult.get().getId());
	}
	
	@Test
	public void createBundleGroupTest() {
		BundleGroup bundleGroup = createBundleGroup();
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);
		bundleGroupNoId.setVersionDetails(null);
		Category category = bundleGroup.getCategories().iterator().next();
		String categoryId = category.getId().toString();
		Mockito.when(bundleGroupRepository.save(bundleGroup)).thenReturn(bundleGroup);
		Mockito.when(categoryRepository.findByBundleGroupsIs(bundleGroup)).thenReturn(List.of(category));
		Mockito.when(categoryRepository.findById(Long.valueOf(categoryId))).thenReturn(Optional.of(category));
		Mockito.when(categoryRepository.save(category)).thenReturn(category);
		BundleGroup bundleGroupresult = bundleGroupService.createBundleGroup(bundleGroup, bundleGroupNoId);
		assertNotNull(bundleGroupresult);
		assertEquals(bundleGroup.getId(), bundleGroupresult.getId());
	}
	
	@Test
	public void updateMappedByTest() {
		BundleGroup bundleGroup = createBundleGroup();
		BundleGroupNoId bundleGroupNoId = new BundleGroupNoId(bundleGroup);
		Category category = bundleGroup.getCategories().iterator().next();
		String categoryId = category.getId().toString();
		BundleGroupVersion bundleGroupVersion = bundleGroup.getVersion().iterator().next();
		BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(bundleGroupVersion);
		
		Mockito.when(bundleGroupRepository.save(bundleGroup)).thenReturn(bundleGroup);
		Mockito.when(categoryRepository.findByBundleGroupsIs(bundleGroup)).thenReturn(List.of(category));
		Mockito.when(categoryRepository.findById(Long.valueOf(categoryId))).thenReturn(Optional.of(category));
		Mockito.when(categoryRepository.save(category)).thenReturn(category);
		
		//Case 1: bundleGroup has version details
		bundleGroupVersionView.setBundleGroupVersionId(bundleGroupVersion.getId().toString());
		bundleGroupNoId.setVersionDetails(bundleGroupVersionView);
		bundleGroupService.updateMappedBy(bundleGroup, bundleGroupNoId);
		
		//Case 2: versionId is null
		bundleGroupVersion.setId(null);
		BundleGroupVersionView bundleGroupVersionView2 = new BundleGroupVersionView(bundleGroupVersion);
		bundleGroupNoId.setVersionDetails(bundleGroupVersionView2);
		bundleGroupService.updateMappedBy(bundleGroup, bundleGroupNoId);
	
	}
	
	@Test
	public void deleteBundleGroupTest() {
		BundleGroup bundleGroup = createBundleGroup();
		Long bundleGroupId = bundleGroup.getId();
		Mockito.when(bundleGroupRepository.findById(bundleGroup.getId())).thenReturn(Optional.of(bundleGroup));
		bundleGroupService.deleteBundleGroup(bundleGroupId);
	}
	
	@Test
	public void deleteFromCategoriesTest() {
		BundleGroup bundleGroup = createBundleGroup();
		Category category = bundleGroup.getCategories().iterator().next();
		category.getBundleGroups().remove(bundleGroup);
		categoryRepository.save(category);
		bundleGroupService.deleteFromCategories(bundleGroup);
	}
	
	@Test
	public void deleteFromOrganisationTest() {
		BundleGroup bundleGroup = createBundleGroup();
		Organisation organisation = bundleGroup.getOrganisation();
		Set<BundleGroup> bundleGroupSet = new HashSet<>();
		bundleGroupSet.add(bundleGroup);
		organisation.setBundleGroups(bundleGroupSet);
		bundleGroupService.deleteFromOrganisations(bundleGroup);
	}
	
	private BundleGroup createBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUPID);
		bundleGroup.setName(NAME);
		Organisation organisation = createOrganisation();
		bundleGroup.setOrganisation(organisation);
		Set<BundleGroup> bundleGroupSet = new HashSet<>();
		bundleGroupSet.add(bundleGroup);
		Category category = createCategory();
		category.setBundleGroups(bundleGroupSet);		
		Set<Category> categorySet = new HashSet<>();
		categorySet.add(category);
		bundleGroup.setCategories(categorySet);
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Set<BundleGroupVersion> versionSet = new HashSet<>();
		versionSet.add(bundleGroupVersion);
		bundleGroup.setVersion(versionSet);
		return bundleGroup;
	}
	
	private Organisation createOrganisation() {
		Organisation organisation = new Organisation();
		organisation.setId(ORG_ID);
		organisation.setName(NAME);
		organisation.setDescription(DEPENDENCIES);
		return organisation;
	}
	
	private Category createCategory() {
		Category category = new Category();
		category.setId(CATEGORY_ID);
		category.setName(NAME);
		category.setDescription(DESCRIPTION);
		category.setBundleGroups(null);
		return category;
	}
	
	private BundleGroupVersion createBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroupVersion.setDescription(DESCRIPTION);
		bundleGroupVersion.setDescriptionImage(DESCRIPTION_IMAGE);
		bundleGroupVersion.setDocumentationUrl(DOCUMENTATION_URL);
		bundleGroupVersion.setVersion(VERSION);
		bundleGroupVersion.setStatus(Status.PUBLISHED);
		return bundleGroupVersion;
	}
}
