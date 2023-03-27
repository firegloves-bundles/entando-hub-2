package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionEntityMapper;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionStandardMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.BUNDLE_GROUP_ID;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.BUNDLE_GROUP_ID_STR;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.BUNDLE_GROUP_NAME;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.BUNDLE_NAME;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.CAT_ID_1;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.CAT_ID_2;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.ORGANIZATION_NAME;
import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.ORGANIZATION_ID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class BundleGroupVersionMapperTest {


  private BundleGroupVersionStandardMapper bundleGroupVersionStandardMapper = Mappers.getMapper(BundleGroupVersionStandardMapper.class);

  private BundleGroupVersionEntityMapper bundleGroupVersionEntityMapper = Mappers.getMapper(BundleGroupVersionEntityMapper.class);

  @Test
  public void testToEntity() {
    BundleGroupVersionDto dto = generateBundleGroupVersionDto("2381");
    BundleGroup bg = MapperGeneratorHelper.generateBundleGroupEntity(2382L);

    BundleGroupVersion entity = bundleGroupVersionStandardMapper.toEntity(dto, bg);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    assertEquals((Long)2381L, entity.getId());
    testCommonData(bg, entity);
  }

  @Test
  public void testToEntityNoId() {
    BundleGroupVersionDto dto = generateBundleGroupVersionDto(null);
    BundleGroup bg = MapperGeneratorHelper.generateBundleGroupEntity(2382L);

    BundleGroupVersion entity = bundleGroupVersionStandardMapper.toEntity(dto, bg);
    assertNotNull(entity);
    assertNull(entity.getId());
    testCommonData(bg, entity);
  }

  @Test
  public void toDto() {
    BundleGroupVersion entity = generateBundleGroupVersionEntity(191045L);

    BundleGroupVersionDto dto = bundleGroupVersionStandardMapper.toDto(entity);
    assertNotNull(dto);
    assertEquals(BUNDLE_GROUP_VERSION_DESCRIPTION, dto.getDescription());
    assertEquals(BUNDLE_GROUP_VERSION_DESCRIPTION_IMG, dto.getDescriptionImage());
    assertEquals(BUNDLE_GROUP_VERSION_STATUS, dto.getStatus());
    assertEquals(BUNDLE_GROUP_VERSION_DOCUMENTATION_URL, dto.getDocumentationUrl());
    assertEquals(BUNDLE_GROUP_VERSION_VERSION, dto.getVersion());
    assertNotNull(dto.getBundleGroupId());
    assertEquals(String.valueOf(BUNDLE_GROUP_ID), dto.getBundleGroupId());
    assertEquals((Long) ORGANIZATION_ID, dto.getOrganisationId());
    assertEquals(ORGANIZATION_NAME, dto.getOrganisationName());
    assertEquals(BUNDLE_GROUP_NAME, dto.getName());
    assertEquals(BUNDLE_GROUP_VERSION_UPDATED, dto.getLastUpdate());
    assertNotNull(dto.getCategories());
    assertFalse(dto.getCategories().isEmpty());
    assertEquals(2, dto.getCategories().size());
    assertThat(dto.getCategories(), hasItems(String.valueOf(CAT_ID_1), String.valueOf(CAT_ID_2)));
    assertNotNull(dto.getChildren());
    assertFalse(dto.getChildren().isEmpty());
    assertEquals(2, dto.getChildren().size());
    assertThat(dto.getChildren(), hasItems(BUNDLE_ID, BUNDLE_ID + 1));
    assertEquals(BUNDLE_GROUP_VERSION_DISPLAY_CONTACT_URL, dto.getDisplayContactUrl());
    assertEquals(BUNDLE_GROUP_VERSION_CONTACT_URL, dto.getContactUrl());
  }

  @Test
  public void toOutDto() {
    BundleGroupVersion entity = generateBundleGroupVersionEntity(153L);

    BundleGroupVersionEntityDto dto = bundleGroupVersionEntityMapper.toDto(entity);
    assertNotNull(dto);
    assertEquals(entity.getId(), dto.getId());
    assertEquals(entity.getDescription(), dto.getDescription());
    assertEquals(entity.getDocumentationUrl(), dto.getDocumentationUrl());
    assertEquals(entity.getVersion(), dto.getVersion());
    assertEquals(entity.getDescriptionImage(), dto.getDescriptionImage());
    assertEquals(entity.getStatus(), dto.getStatus());
    assertEquals(entity.getDisplayContactUrl(), dto.getDisplayContactUrl());
    assertEquals(entity.getContactUrl(), dto.getContactUrl());
    assertEquals(entity.getBundleGroup(), dto.getBundleGroup());
    assertEquals(entity.getBundles().size(), dto.getBundles().size());
    assertEquals(entity.getLastUpdated(), dto.getLastUpdated());
  }

  public static void testCommonData(BundleGroup bg, BundleGroupVersion entity) {
    assertEquals(BUNDLE_GROUP_VERSION_DESCRIPTION, entity.getDescription());
    assertEquals(BUNDLE_GROUP_VERSION_DESCRIPTION_IMG, entity.getDescriptionImage());
    assertEquals(BUNDLE_GROUP_VERSION_DOCUMENTATION_URL, entity.getDocumentationUrl());
    assertEquals(BUNDLE_GROUP_VERSION_STATUS, entity.getStatus());
    assertEquals(BUNDLE_GROUP_VERSION_VERSION, entity.getVersion());
    assertEquals(BUNDLE_GROUP_VERSION_DISPLAY_CONTACT_URL, entity.getDisplayContactUrl());
    assertEquals(BUNDLE_GROUP_VERSION_CONTACT_URL, entity.getContactUrl());
    assertEquals(BUNDLE_GROUP_VERSION_STATUS, entity.getStatus());
    assertEquals(bg, entity.getBundleGroup());
  }

  public BundleGroupVersionDto generateBundleGroupVersionDto(String id) {
    BundleGroupVersionDto dto = BundleGroupVersionDto.builder()

            .bundleGroupVersionId(id) // ??? shared between BundleGroupVersionDto AND BundleGroupVersionView
            .description(BUNDLE_GROUP_VERSION_DESCRIPTION)
            .descriptionImage(BUNDLE_GROUP_VERSION_DESCRIPTION_IMG)
            .documentationUrl(BUNDLE_GROUP_VERSION_DOCUMENTATION_URL)
            .status(BUNDLE_GROUP_VERSION_STATUS)
            .version(BUNDLE_GROUP_VERSION_VERSION)
            .displayContactUrl(BUNDLE_GROUP_VERSION_DISPLAY_CONTACT_URL)
            .contactUrl(BUNDLE_GROUP_VERSION_CONTACT_URL)

            .name(BUNDLE_NAME)
            .bundleGroupId(BUNDLE_GROUP_ID_STR)
            .lastUpdate(LocalDateTime.now())
            .categories(Arrays.asList("cat 1","cat 2"))
            .children(Arrays.asList(26L, 77L))
            .bundles(Arrays.asList(new BundleDto()))
            .build();

    return dto;
  }

  public BundleGroupVersion generateBundleGroupVersionEntity(Long id) {
    BundleGroupVersion bgv = new BundleGroupVersion();
    BundleGroup bg = MapperGeneratorHelper.generateBundleGroupEntity(BUNDLE_GROUP_ID);

    Bundle bundle_1 = MapperGeneratorHelper.generateBundleEntity(BUNDLE_ID);
    Bundle bundle_2 = MapperGeneratorHelper.generateBundleEntity(BUNDLE_ID + 1);

    bgv.setId(id);
    bgv.setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION);
    bgv.setDescriptionImage(BUNDLE_GROUP_VERSION_DESCRIPTION_IMG);
    bgv.setDocumentationUrl(BUNDLE_GROUP_VERSION_DOCUMENTATION_URL);
    bgv.setStatus(BUNDLE_GROUP_VERSION_STATUS);
    bgv.setVersion(BUNDLE_GROUP_VERSION_VERSION);
    bgv.setBundleGroup(bg);
    bgv.setBundles(Stream.of(bundle_2, bundle_1).collect(Collectors.toSet()));
    bgv.setDisplayContactUrl(BUNDLE_GROUP_VERSION_DISPLAY_CONTACT_URL);
    bgv.setContactUrl(BUNDLE_GROUP_VERSION_CONTACT_URL);
    bgv.setLastUpdated(BUNDLE_GROUP_VERSION_UPDATED);

    return bgv;
  }

  public static final Long BUNDLE_ID = 2381L;

  public static final Long BUNDLE_GROUP_VERSION_ID = 29876L;
  public static final String BUNDLE_GROUP_VERSION_DOCUMENTATION_URL = "DOC URL";
  public static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "description";
  public static final String BUNDLE_GROUP_VERSION_DESCRIPTION_IMG = "1234image";
  public static final BundleGroupVersion.Status BUNDLE_GROUP_VERSION_STATUS = BundleGroupVersion.Status.PUBLISHED;
  public static final String BUNDLE_GROUP_VERSION_VERSION = "v1.0.0";
  public static final Boolean BUNDLE_GROUP_VERSION_DISPLAY_CONTACT_URL = true;
  public static final String BUNDLE_GROUP_VERSION_CONTACT_URL = "contact url meh";
  public static final LocalDateTime BUNDLE_GROUP_VERSION_UPDATED = LocalDateTime.now();


}
