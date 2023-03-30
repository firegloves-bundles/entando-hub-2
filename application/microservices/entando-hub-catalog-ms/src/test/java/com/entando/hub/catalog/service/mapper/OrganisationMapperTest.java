package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.rest.dto.OrganisationDto;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.entando.hub.catalog.service.mapper.MapperGeneratorHelper.*;
import static junit.framework.TestCase.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class OrganisationMapperTest {

  private OrganizationMapper organizationMapper = Mappers.getMapper(OrganizationMapper.class);

  @Test
  public void toEntity() {
    OrganisationDto dto = generateOrganizationDto("951");

    Organisation entity = organizationMapper.toEntity(dto);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    assertEquals((Long)951L, entity.getId());
    assertEquals(ORGANIZATION_NAME, entity.getName());
    assertEquals(ORGANIZATION_DESCRIPTION, entity.getDescription());
    assertNull(entity.getBundleGroups());
  }

  @Test
  public void toDto() {
    Organisation entity = generateOrganizationEntity(157L);

    OrganisationDto dto = organizationMapper.toDto(entity);
    assertNotNull(dto);
    assertNotNull(dto.getOrganisationId());
    assertEquals("157", dto.getOrganisationId());
    assertEquals(ORGANIZATION_NAME, dto.getName());
    assertEquals(ORGANIZATION_DESCRIPTION, dto.getDescription());
    assertNotNull(dto.getBundleGroups());
    assertEquals((long)2, dto.getBundleGroups().size());
    assertThat(dto.getBundleGroups(), hasItems("2677", "2676"));
  }

  protected OrganisationDto generateOrganizationDto(String id) {
    OrganisationDto dto = new OrganisationDto();

    dto.setOrganisationId(id);
    dto.setDescription(ORGANIZATION_DESCRIPTION);
    dto.setName(ORGANIZATION_NAME);
    dto.setBundleGroups(Arrays.asList(BUNDLE_GROUP_ID_STR));

    return dto;
  }

  protected Organisation generateOrganizationEntity(Long id) {
    Organisation entity = new Organisation();
    BundleGroup bg1 = MapperGeneratorHelper.generateBundleGroupEntity(BUNDLE_GROUP_ID);
    BundleGroup bg2 = MapperGeneratorHelper.generateBundleGroupEntity(BUNDLE_GROUP_ID - 1);
    PortalUser user = new PortalUser();

    user.setUsername(USER_NAME);
    user.setOrganisations(Stream.of(entity).collect(Collectors.toSet()));
    user.setEmail(USER_EMAIL);
    user.setId(USER_ID);

    entity.setId(id);
    entity.setDescription(ORGANIZATION_DESCRIPTION);
    entity.setName(ORGANIZATION_NAME);
    entity.setBundleGroups(Stream.of(bg1, bg2).collect(Collectors.toSet()));
    entity.setPortalUsers(Stream.of(user).collect(Collectors.toSet()));
    return entity;
  }

  public final Long USER_ID = 357L;
  public final String USER_NAME = "USERNAME";
  public static final String USER_EMAIL = "mail@gmail.it";

}
