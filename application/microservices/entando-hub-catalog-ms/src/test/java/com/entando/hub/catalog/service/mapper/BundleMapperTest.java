package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleDto;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;

public class BundleMapperTest extends BaseMapperTest {

  private BundleMapper bundleMapper = Mappers.getMapper(BundleMapper.class);

  @Test
  public void toEntity() {
    BundleDto dto = generateBundleDto("2677");
    Bundle bundle = bundleMapper.toEntity(dto);
    assertNotNull(bundle);

    assertEquals((Long)2677L, bundle.getId());
    assertEquals(BUNDLE_NAME, bundle.getName());
    assertEquals(BUNDLE_DESCRIPTION, bundle.getDescription());
    assertEquals(GIT_REPO_ADDRESS, bundle.getGitRepoAddress());
    assertEquals(GIT_SRC_REPO_ADDRESS, bundle.getGitSrcRepoAddress());
    assertEquals(DescriptorVersion.V1, bundle.getDescriptorVersion());
    assertNotNull(bundle.getBundleGroupVersions());
    assertFalse(bundle.getBundleGroupVersions().isEmpty());
    assertEquals(2, bundle.getBundleGroupVersions().size());
    assertEquals("dep 1,dep 2", bundle.getDependencies());
  }

  @Test
  public void toEntityNoId() {
    BundleDto dto = generateBundleDto(null);

    Bundle bundle = bundleMapper.toEntity(dto);
    assertNotNull(bundle);

    assertNull(bundle.getId());
    assertEquals(BUNDLE_NAME, bundle.getName());
    assertEquals(BUNDLE_DESCRIPTION, bundle.getDescription());

    assertEquals(GIT_REPO_ADDRESS, bundle.getGitRepoAddress());
    assertEquals(GIT_SRC_REPO_ADDRESS, bundle.getGitSrcRepoAddress());
    assertEquals(DescriptorVersion.V1, bundle.getDescriptorVersion());
    assertNotNull(bundle.getBundleGroupVersions());
    assertFalse(bundle.getBundleGroupVersions().isEmpty());
    assertEquals(2, bundle.getBundleGroupVersions().size());
    assertEquals("dep 1,dep 2", bundle.getDependencies());
    assertEquals(DescriptorVersion.V1, bundle.getDescriptorVersion());
    assertNotNull(dto.getBundleGroups());
    assertFalse(dto.getBundleGroups().isEmpty());
    assertEquals(2, dto.getBundleGroups().size());
  }

  @Test
  public void toDto() {
    Bundle entity = generateBundleEntity(2677L);

    BundleDto dto = bundleMapper.toDto(entity);
    assertNotNull(dto);
    assertNotNull(dto.getBundleId());
    assertEquals("2677", dto.getBundleId());
    assertEquals(BUNDLE_NAME, dto.getName());
    assertEquals(BUNDLE_DESCRIPTION, dto.getDescription());
    assertEquals(GIT_REPO_ADDRESS, dto.getGitRepoAddress());
    assertEquals(GIT_SRC_REPO_ADDRESS, dto.getGitSrcRepoAddress());
    assertNotNull(dto.getDependencies());
    assertFalse(dto.getDependencies().isEmpty());
    assertEquals(2, dto.getDependencies().size());
    assertEquals("dep 2", dto.getDependencies().get(1));
    assertEquals("dep 1", dto.getDependencies().get(0));
  }

  @Test
  public void toDtoNoId() {
    Bundle entity = generateBundleEntity(null);

    BundleDto dto = bundleMapper.toDto(entity);
    assertNotNull(dto);
    assertNull(dto.getBundleId());
    assertEquals(BUNDLE_NAME, dto.getName());
    assertEquals(BUNDLE_DESCRIPTION, dto.getDescription());
    assertEquals(GIT_REPO_ADDRESS, dto.getGitRepoAddress());
    assertEquals(GIT_SRC_REPO_ADDRESS, dto.getGitSrcRepoAddress());
    assertNotNull(dto.getDependencies());
    assertFalse(dto.getDependencies().isEmpty());
    assertEquals(2, dto.getDependencies().size());
    assertEquals("dep 2", dto.getDependencies().get(1));
    assertEquals("dep 1", dto.getDependencies().get(0));
  }

  private static BundleDto generateBundleDto(String id) {
    BundleDto dto = new BundleDto();

    dto.setBundleId(id);
    dto.setName(BUNDLE_NAME);
    dto.setDescription(BUNDLE_DESCRIPTION);
    dto.setDescriptionImage("image");
    dto.setDescriptorVersion("V1");
    dto.setGitRepoAddress(GIT_REPO_ADDRESS);

    dto.setGitSrcRepoAddress(GIT_SRC_REPO_ADDRESS);
    dto.setDependencies(Arrays.asList("dep 1","dep 2"));
    dto.setBundleGroups(Arrays.asList("1","2"));
    return dto;
  }

}
