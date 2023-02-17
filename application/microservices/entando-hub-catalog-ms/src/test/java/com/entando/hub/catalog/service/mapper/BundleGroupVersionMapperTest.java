package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.domain.BundleGroupVersionDto;
import com.entando.hub.catalog.rest.dto.BundleDto;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.*;

public class BundleGroupVersionMapperTest extends BaseMapperTest {

  private BundleGroupVersionMapper bundleGroupVersionMapper = Mappers.getMapper(BundleGroupVersionMapper.class);

  @Test
  public void testToEntity() {
    BundleGroupVersionDto dto = generateBundleGroupVersionDto("2381");
    BundleGroup bg = generateBundleGroupEntity(2382L);

    BundleGroupVersion entity = bundleGroupVersionMapper.toEntity(dto, bg);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    assertEquals((Long)2381L, entity.getId());
    testCommonData(bg, entity);
  }

  @Test
  public void testToEntityNoId() {
    BundleGroupVersionDto dto = generateBundleGroupVersionDto(null);
    BundleGroup bg = generateBundleGroupEntity(2382L);

    BundleGroupVersion entity = bundleGroupVersionMapper.toEntity(dto, bg);
    assertNotNull(entity);
    assertNull(entity.getId());
    testCommonData(bg, entity);
  }

  public static void testCommonData(BundleGroup bg, BundleGroupVersion entity) {
    assertEquals(BUNDLE_GROUP_DESCRIPTION, entity.getDescription());
    assertEquals(BUNDLE_GROUP_DESCRIPTION_IMG, entity.getDescriptionImage());
    assertEquals(BUNDLE_DOC_URL, entity.getDocumentationUrl());
    assertEquals(BUNDLE_STATUS, entity.getStatus());
    assertEquals(BUNDLE_VERSION, entity.getVersion());
    assertEquals(BUNDLE_GROUP_DISPLAY_CONTACT_URL, entity.getDisplayContactUrl());
    assertEquals(BUNDLE_GROUP_CONTACT_URL, entity.getContactUrl());
    assertEquals(BUNDLE_STATUS, entity.getStatus());
    assertEquals(bg, entity.getBundleGroup());
  }

  private BundleGroupVersionDto generateBundleGroupVersionDto(String id) {
    BundleGroupVersionDto dto = new BundleGroupVersionDto();

    dto.setBundleGroupVersionId(id); // ??? shared between BundleGroupVersionDto AND BundleGroupVersionView
    dto.setDescription(BUNDLE_GROUP_DESCRIPTION);
    dto.setDescriptionImage(BUNDLE_GROUP_DESCRIPTION_IMG);
    dto.setDocumentationUrl(BUNDLE_DOC_URL);
    dto.setStatus(BUNDLE_STATUS);
    dto.setVersion(BUNDLE_VERSION);
    dto.setDisplayContactUrl(BUNDLE_GROUP_DISPLAY_CONTACT_URL);
    dto.setContactUrl(BUNDLE_GROUP_CONTACT_URL);

    // not converted to entity apparently
    dto.setName(BUNDLE_NAME);
    dto.setBundleGroupId(BUNDLE_GROUP_ID_STR);
    dto.setLastUpdate(LocalDateTime.now());
    dto.setCategories(Arrays.asList("cat 1","cat 2"));
    dto.setChildren(Arrays.asList(26L, 77L));
    dto.setBundles(Arrays.asList(new BundleDto()));

    return dto;
  }

  private BundleGroupVersion generateBundleGroupVersionEntity(Long id) {
    BundleGroupVersion bgv = new BundleGroupVersion();
    BundleGroup bg = generateBundleGroupEntity(id != null ? id++ : null);

    bgv.setId(id);
    bgv.setDescriptionImage(BUNDLE_GROUP_DESCRIPTION);
    bgv.setDescriptionImage(BUNDLE_GROUP_DESCRIPTION_IMG);
    bgv.setDocumentationUrl(BUNDLE_DOC_URL);
    bgv.setStatus(BUNDLE_STATUS);
    bgv.setVersion(BUNDLE_VERSION);
    bgv.setBundleGroup(bg);
    bgv.setDisplayContactUrl(BUNDLE_GROUP_DISPLAY_CONTACT_URL);
    bgv.setContactUrl(BUNDLE_GROUP_CONTACT_URL);

    // SHOULD NOT BE MAPPED
    bgv.setBundles(Stream.of(generateBundleEntity(27147L)).collect(Collectors.toSet()));

    return bgv;
  }

  private static final String BUNDLE_DOC_URL = "DOC URL";
  public static final String BUNDLE_GROUP_DESCRIPTION = "description";
  public static final String BUNDLE_GROUP_DESCRIPTION_IMG = "1234image";
  private static final BundleGroupVersion.Status BUNDLE_STATUS = BundleGroupVersion.Status.PUBLISHED;
  private static final String BUNDLE_VERSION = "v1.0.0";
  private static final Boolean BUNDLE_GROUP_DISPLAY_CONTACT_URL = true;
  private static final String BUNDLE_GROUP_CONTACT_URL = "contact url meh";


}
