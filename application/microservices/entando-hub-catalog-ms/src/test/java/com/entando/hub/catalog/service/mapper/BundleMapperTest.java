package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleDtoIn;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

import static junit.framework.TestCase.*;

public class BundleMapperTest {

  private BundleMapper bundleMapper = Mappers.getMapper(BundleMapper.class);

  @Test
  public void bundle2Dto() {
    BundleDtoIn dto = generateBundleDto("2677");
    Bundle bundle = bundleMapper.toEntity(dto);
    assertNotNull(bundle);

    assertEquals((Long)2677L, bundle.getId());
    assertEquals("name", bundle.getName());
    assertEquals("description", bundle.getDescription());
    assertEquals("repo address", bundle.getGitRepoAddress());
    assertEquals("src repo address", bundle.getGitSrcRepoAddress());
    assertEquals(DescriptorVersion.V1, bundle.getDescriptorVersion());
    assertNotNull(bundle.getBundleGroupVersions());
    assertFalse(bundle.getBundleGroupVersions().isEmpty());
    assertEquals(2, bundle.getBundleGroupVersions().size());
    assertEquals("dep 1,dep 2", bundle.getDependencies());
  }

  @Test
  public void bundle2DtoNoId() {
    BundleDtoIn dto = generateBundleDto(null);

    Bundle bundle = bundleMapper.toEntity(dto);
    assertNotNull(bundle);

    assertNull(bundle.getId());
    assertEquals("name", bundle.getName());
    assertEquals("description", bundle.getDescription());
    assertEquals("repo address", bundle.getGitRepoAddress());
    assertEquals("src repo address", bundle.getGitSrcRepoAddress());
    assertEquals(DescriptorVersion.V1, bundle.getDescriptorVersion());
    assertNotNull(bundle.getBundleGroupVersions());
    assertFalse(bundle.getBundleGroupVersions().isEmpty());
    assertEquals(2, bundle.getBundleGroupVersions().size());
    assertEquals("dep 1,dep 2", bundle.getDependencies());
  }


  private static BundleDtoIn generateBundleDto(String id) {
    BundleDtoIn dto = new BundleDtoIn();

    dto.setBundleId(id);
    dto.setName("name");
    dto.setDescription("description");
    dto.setDescriptionImage("image");
    dto.setDescriptorVersion("ara");
    dto.setGitRepoAddress("repo address");
    dto.setGitSrcRepoAddress("src repo address");
    dto.setDependencies(Arrays.asList("dep 1","dep 2"));
    dto.setBundleGroups(Arrays.asList("1","2"));
    return dto;
  }


}
