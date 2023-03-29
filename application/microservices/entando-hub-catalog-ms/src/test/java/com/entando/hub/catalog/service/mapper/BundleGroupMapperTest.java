package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.rest.dto.BundleGroupDto;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.BUNDLE_GROUP_ID;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.BUNDLE_GROUP_ID_STR;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.BUNDLE_GROUP_NAME;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.CAT_ID_1;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.CAT_ID_2;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.ORGANIZATION_ID;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.ORGANIZATION_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class BundleGroupMapperTest {

  private BundleGroupMapper bundleGroupMapper = Mappers.getMapper(BundleGroupMapper.class);

  @Test
  public void toDto() {
    BundleGroupDto dto = MapperGeneratorHelper.generateBundleGroupDto("2381");

    BundleGroup bge = bundleGroupMapper.toEntity(dto);
    assertNotNull(bge);
    assertEquals(BUNDLE_GROUP_NAME, bge.getName());

    assertNotNull(bge.getOrganisation());
    assertNotNull(bge.getId());
    assertEquals((Long)ORGANIZATION_ID, bge.getOrganisation().getId());
    assertEquals(ORGANIZATION_NAME, bge.getOrganisation().getName());
    assertNotNull(bge.getCategories());
    assertTrue(bge.getCategories().isEmpty());
    // versiondetails ignored
  }

  @Test
  public void toDtoNoId() {
    BundleGroupDto dto = MapperGeneratorHelper.generateBundleGroupDto(null);

    BundleGroup bge = bundleGroupMapper.toEntity(dto);
    assertNotNull(bge);
    assertEquals(BUNDLE_GROUP_NAME, bge.getName());

    assertNotNull(bge.getOrganisation());
    assertNull(bge.getId());
    assertEquals((Long) ORGANIZATION_ID, bge.getOrganisation().getId());
    assertEquals(ORGANIZATION_NAME, bge.getOrganisation().getName());
    assertNotNull(bge.getCategories());
    assertTrue(bge.getCategories().isEmpty());
    // versiondetails ignored
  }

  @Test
  public void toEntity() {
    BundleGroup dto = MapperGeneratorHelper.generateBundleGroupEntity(BUNDLE_GROUP_ID);

    BundleGroupDto bundleGroup = bundleGroupMapper.toDto(dto);
    assertNotNull(bundleGroup);
    assertNotNull(bundleGroup.getBundleGroupId());
    assertEquals(BUNDLE_GROUP_ID_STR, bundleGroup.getBundleGroupId());
    checkCommonEntityProperties(bundleGroup);
  }

  @Test
  public void toEntityNoId() {
    BundleGroup dto = MapperGeneratorHelper.generateBundleGroupEntity(null);

    BundleGroupDto bundleGroup = bundleGroupMapper.toDto(dto);
    assertNotNull(bundleGroup);
    assertNull(bundleGroup.getBundleGroupId());
    checkCommonEntityProperties(bundleGroup);
  }

  @Test
  public void studyConversionBehavior() {
    BundleGroup entity = new BundleGroup();

    BundleGroupDto dto = bundleGroupMapper.toDto(entity);
    assertNotNull(dto);
    assertNull(dto.getBundleGroupId());
    assertNotNull(dto.getCategories());
    assertTrue(dto.getCategories().isEmpty());
    assertNull(dto.getName());
    assertNull(dto.getOrganisationId());
    assertNull(dto.getOrganisationName());

    dto = new BundleGroupDto();
    entity = bundleGroupMapper.toEntity(dto);
    assertNotNull(entity);
    assertNull(entity.getId());
  }

  public static void checkCommonEntityProperties(BundleGroupDto bundleGroup) {
    assertEquals(BUNDLE_GROUP_NAME, bundleGroup.getName());
    assertNotNull(bundleGroup.getOrganisationId());
    assertEquals(ORGANIZATION_NAME, bundleGroup.getOrganisationName());
    assertEquals(ORGANIZATION_ID, bundleGroup.getOrganisationId());
    assertNotNull(bundleGroup.getCategories());
    List<String> categories = bundleGroup.getCategories();
    assertFalse(categories.isEmpty());
    assertEquals(2, categories.size());
    assertThat(categories, contains(String.valueOf(CAT_ID_2) , String.valueOf(CAT_ID_1)));
  }


}
