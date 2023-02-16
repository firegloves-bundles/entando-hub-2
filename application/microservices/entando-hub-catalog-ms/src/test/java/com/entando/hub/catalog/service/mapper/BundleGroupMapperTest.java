package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.domain.BundleGroupDto;
import com.entando.hub.catalog.rest.domain.BundleGroupVersionView;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class BundleGroupMapperTest {

  private BundleGroupMapper bundleGroupMapper = Mappers.getMapper(BundleGroupMapper.class);

  @Test
  public void toDto() {
    BundleGroupDto dto = createBundleGroupDto("2381");

    BundleGroup bge = bundleGroupMapper.toEntity(dto);
    assertNotNull(bge);
    assertEquals(BUNDLE_GROUP_NAME, bge.getName());

    assertNotNull(bge.getOrganisation());
    assertNotNull(bge.getId());
    assertEquals((long)bge.getOrganisation().getId(), 2677L);
    assertEquals(bge.getOrganisation().getName(), ORGANIZATION_NAME);
    assertNull(bge.getCategories());
    // versiondetails ignored
  }

  @Test
  public void toDtoNoId() {
    BundleGroupDto dto = createBundleGroupDto(null);

    BundleGroup bge = bundleGroupMapper.toEntity(dto);
    assertNotNull(bge);
    assertEquals(BUNDLE_GROUP_NAME, bge.getName());

    assertNotNull(bge.getOrganisation());
    assertNull(bge.getId());
    assertEquals((long)bge.getOrganisation().getId(), 2677L);
    assertEquals(bge.getOrganisation().getName(), ORGANIZATION_NAME);
    assertNull(bge.getCategories());
    // versiondetails ignored
  }

  @Test
  public void toEntity() {
    BundleGroup dto = getBundleGroupEntity(ORGANIZATION_ID);

    BundleGroupDto bundleGroup = bundleGroupMapper.toDto(dto);
    assertNotNull(bundleGroup);
    assertNotNull(bundleGroup.getBundleGroupId());
    assertEquals("2677", bundleGroup.getBundleGroupId());
    assertEquals(BUNDLE_GROUP_NAME, bundleGroup.getName());
    assertNotNull(bundleGroup.getOrganisationId());
    assertNotNull(bundleGroup.getOrganisationId());
    assertEquals(ORGANIZATION_NAME, bundleGroup.getOrganisationName());
    assertEquals(ORGANIZATION_ID_STR, bundleGroup.getOrganisationId());
    assertNotNull(bundleGroup.getCategories());
    List<String> categories = bundleGroup.getCategories();
    assertFalse(categories.isEmpty());
    assertEquals(2, categories.size());
    assertThat(categories, contains( "191045", "27147"));
  }

  @Test
  public void toEntityNoId() {
    BundleGroup dto = getBundleGroupEntity(null);

    BundleGroupDto bundleGroup = bundleGroupMapper.toDto(dto);
    assertNotNull(bundleGroup);
    assertNull(bundleGroup.getBundleGroupId());
    assertEquals(BUNDLE_GROUP_NAME, bundleGroup.getName());
    assertNotNull(bundleGroup.getOrganisationId());
    assertNotNull(bundleGroup.getOrganisationId());
    assertEquals(ORGANIZATION_NAME, bundleGroup.getOrganisationName());
    assertEquals(ORGANIZATION_ID_STR, bundleGroup.getOrganisationId());
    assertNotNull(bundleGroup.getCategories());
    List<String> categories = bundleGroup.getCategories();
    assertFalse(categories.isEmpty());
    assertEquals(2, categories.size());
    assertThat(categories, contains( "191045", "27147"));
  }

  private BundleGroupDto createBundleGroupDto(String id) {
    BundleGroupDto dto = new BundleGroupDto();

    dto.setBundleGroupId(id);
    dto.setName("bundleGroupName");
    dto.setOrganisationId("2677");
    dto.setOrganisationName("Organization2677Name");
    dto.setCategories(Arrays.asList("cat 1", "cat 2")); // IGNORED
    dto.setVersionDetails(new BundleGroupVersionView("191045",
      "description",
      "descriptionImage", "v123"));
    return dto;
  }

  private BundleGroup getBundleGroupEntity(Long id) {
    BundleGroup bg = new BundleGroup();
    Organisation org = new Organisation();
    BundleGroupVersion bgv = new BundleGroupVersion();

    org.setId(2677L);
    org.setName(ORGANIZATION_NAME);
    org.setDescription(ORGANISATION_DESCRIPTION);

    bg.setOrganisation(org);

    bgv.setId(2381L);
    bgv.setDescription("Bundle group Version description");
    bgv.setDocumentationUrl("documentation URL");
    bgv.setVersion("v0.0.1");
    bgv.setDescriptionImage("description image");
    bgv.setStatus(BundleGroupVersion.Status.ARCHIVE);

    Set<BundleGroupVersion> versions = Stream.of(bgv, bgv)
      .collect(Collectors.toCollection(HashSet::new));
    bg.setVersion(versions);

    Category cat1 = new Category();

    cat1.setId(27147L);
    cat1.setName("category name 1");
    cat1.setDescription("category description 1");

    Category cat2 = new Category();

    cat2.setId(191045L);
    cat2.setName("category name 2");
    cat2.setDescription("category description 2");

    Set<Category> categories = Stream.of(cat2, cat1).collect(Collectors.toSet());
    bg.setCategories(categories);

    bg.setName(BUNDLE_GROUP_NAME);
    bg.setId(id);
    return bg;
  }
  
  public final static String BUNDLE_GROUP_NAME = "bundleGroupName";
  public final static String ORGANIZATION_NAME = "Organization2677Name";
  public final static String ORGANISATION_DESCRIPTION = "organisation description";
  public  static final long ORGANIZATION_ID = 2677;
  public  static final String ORGANIZATION_ID_STR = "2677";
}
