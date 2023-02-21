package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.dto.CategoryDto;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class CategoryMapperTest extends BaseMapperTest {

  private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

  @Test
  public void toEntity() {
    CategoryDto dto = generateCategoryDto(CATEGORY_ID.toString());
    Category entity = categoryMapper.toEntity(dto);

    assertNotNull(entity);
    assertNotNull(entity.getId());
    assertEquals(CATEGORY_ID.toString(), dto.getCategoryId());
    assertEquals(CATEGORY_NAME, entity.getName());
    assertEquals(CATEGORY_DESCRIPTION, entity.getDescription());
    assertNotNull(entity.getBundleGroups());
    assertFalse(entity.getBundleGroups().isEmpty());
    assertEquals(2, entity.getBundleGroups().size());
    assertThat(entity.getBundleGroups().stream().map(b -> b.getId()).collect(Collectors.toList()),
      hasItems(BUNDLE_GROUP_ID + 1, BUNDLE_GROUP_ID));
  }

  @Test
  public void dtoDto() {
    Category entity = generateCategoryEntity(CATEGORY_ID);
    CategoryDto dto = categoryMapper.toDto(entity);

    assertNotNull(dto);
    assertNotNull(dto.getCategoryId());
    assertEquals(CATEGORY_ID.toString(), dto.getCategoryId());
    assertEquals(CATEGORY_NAME, dto.getName());
    assertEquals(CATEGORY_DESCRIPTION, dto.getDescription());
    assertNotNull(dto.getBundleGroups());
    assertFalse(dto.getBundleGroups().isEmpty());
    assertEquals(2, entity.getBundleGroups().size());
    assertThat(dto.getBundleGroups(), hasItems(String.valueOf(654L), String.valueOf(789L)));
  }

  protected CategoryDto generateCategoryDto(String id) {
    CategoryDto dto = new CategoryDto();
    BundleGroup bg1 = generateBundleGroupEntity(BUNDLE_GROUP_ID);
    BundleGroup bg2 = generateBundleGroupEntity(BUNDLE_GROUP_ID + 1);

    dto.setCategoryId(id);
    dto.setCategoryId(CATEGORY_ID.toString());
    dto.setName(CATEGORY_NAME);
    dto.setDescription(CATEGORY_DESCRIPTION);
    dto.setBundleGroups(Stream.of(bg2, bg1).map(c -> c.getId().toString()).collect(Collectors.toList()));
    return dto;
  }

  protected Category generateCategoryEntity(Long id) {
    Category category = new Category();
    BundleGroup bg1 = generateBundleGroupEntity(789L);
    BundleGroup bg2 = generateBundleGroupEntity(654L);

    category.setId(id);
    category.setName(CATEGORY_NAME);
    category.setDescription(CATEGORY_DESCRIPTION);
    category.setBundleGroups(Stream.of(bg2, bg1).collect(Collectors.toSet()));
    return category;
  }

  public final static Long CATEGORY_ID = 123L;
  public final static String CATEGORY_NAME = "CATEGORY 1 NAME";
  public final static String CATEGORY_DESCRIPTION = "CATEGORY 1 DESCRIPTION";

}
