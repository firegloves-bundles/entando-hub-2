package com.entando.hub.catalog.rest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.CategoryController.CategoryNoId;
import com.entando.hub.catalog.service.CategoryService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class CategoryControllerTest {
	@InjectMocks
	CategoryController categoryController;
	@Mock
	CategoryService categoryService;
	
	@Test
	public void testGetCategories() {
	  List<Category> categoryList = new ArrayList<>();
	  Category category =new Category();
	  category.setId(2001L);
	  category.setName("Tech");
	  category.setDescription("New Category");
	  categoryList.add(category);
	  Mockito.when(categoryService.getCategories()).thenReturn(categoryList);
	  List<com.entando.hub.catalog.rest.CategoryController.Category> categoryResultList = categoryController.getCategories();
	  assertNotNull(categoryResultList);
	  assertEquals(categoryList.get(0).getName(),categoryResultList.get(0).getName());
	}
	
	@Test
	public void testGetCategory() {
		List<Category> categoryList = new ArrayList<>();
		  Category category =new Category();
		  category.setId(2001L);
		  category.setName("Tech");
		  category.setDescription("New Category");
		  categoryList.add(category);
		  String categoryId = Long.toString(category.getId());
		  Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.of(category));
		  ResponseEntity<com.entando.hub.catalog.rest.CategoryController.Category> categoryResultList = categoryController.getCategory(categoryId);
		  assertNotNull(categoryResultList);
		  assertEquals(HttpStatus.OK,categoryResultList.getStatusCode());
	}
	
	@Test
	public void testGetCategoryFails() {
		List<Category> categoryList = new ArrayList<>();
		  Category category =new Category();
		  category.setId(2001L);
		  category.setName("Tech");
		  category.setDescription("New Category");
		  categoryList.add(category);
		  String categoryId = Long.toString(category.getId());
		  Mockito.when(categoryService.getCategory(null)).thenReturn(Optional.of(category));
		  ResponseEntity<com.entando.hub.catalog.rest.CategoryController.Category> categoryResultList = categoryController.getCategory(categoryId);
		  assertNotNull(categoryResultList);
		  assertEquals(HttpStatus.NOT_FOUND,categoryResultList.getStatusCode());
	}
	
	@Test
	public void testCreateCategory() {
		//BundleGroup bundleGroup = new BundleGroup();
		Category category = new Category();
		category.setId(2002L);
		category.setName("abc");
		category.setDescription("new One");
		CategoryNoId categoryNoId= new CategoryNoId(category);
		Mockito.when(categoryService.createCategory(categoryNoId.createEntity(Optional.empty()))).thenReturn(category);
		String categoryId = Long.toString(category.getId());
		com.entando.hub.catalog.rest.CategoryController.Category categoryC = new com.entando.hub.catalog.rest.CategoryController.Category(categoryId, category.getName(), category.getDescription());
		ResponseEntity<com.entando.hub.catalog.rest.CategoryController.Category> categoryResult = categoryController.createCategory(categoryNoId);
		assertNotNull(categoryResult);
		assertEquals(HttpStatus.CREATED,categoryResult.getStatusCode());
	}
	
	@Test
	public void testUpdateCategory() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1001L);
		bundleGroup.setName("New Xyz");
		Category category = new Category();
		category.setId(2002L);
		category.setName("admin");
		category.setDescription("new One");
		Set<BundleGroup> bundleGroups = new HashSet<>();
		bundleGroups.add(bundleGroup);
		category.setBundleGroups(bundleGroups);
		CategoryNoId catNoId= new CategoryNoId(category);
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.of(category));
		Mockito.when(categoryService.createCategory(catNoId.createEntity(Optional.of(categoryId)))).thenReturn(category);
		ResponseEntity<com.entando.hub.catalog.rest.CategoryController.Category> categoryResult = categoryController.updateCategory(categoryId, catNoId);
		assertNotNull(categoryResult);
		assertEquals(HttpStatus.OK,categoryResult.getStatusCode());
	}
	
	@Test
	public void testUpdateCategoryFails() {
	
		Category category = new Category();
		category.setId(2002L);
		category.setName("abc");
		category.setDescription("new One");
		CategoryNoId catNoId= new CategoryNoId(category);
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(null)).thenReturn(Optional.of(category));
		
		ResponseEntity<com.entando.hub.catalog.rest.CategoryController.Category> categoryResult = categoryController.updateCategory(categoryId, catNoId);
		assertNotNull(categoryResult);
		assertEquals(HttpStatus.NOT_FOUND,categoryResult.getStatusCode());
	}
	
	@Test
	public void testDeleteCategory() {
		
		Category category = new Category();
		category.setId(2002L);
		category.setName("abc");
		category.setDescription("new One");
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.of(category));
		categoryService.deleteCategory(categoryId);
		ResponseEntity<String> categoryResult = categoryController.deleteCategory(categoryId);
		assertNotNull(categoryResult);
		assertEquals(HttpStatus.OK,categoryResult.getStatusCode());
	}
	
	@Test
	public void testDeleteCategoryFails() {
		
		Category category = new Category();
		category.setId(2002L);
		category.setName("abc");
		category.setDescription("new One");
		category.setBundleGroups(null);
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(null)).thenReturn(Optional.of(category));
		categoryService.deleteCategory(categoryId);
		ResponseEntity<String> categoryResult = categoryController.deleteCategory(categoryId);
		assertNotNull(categoryResult);
		assertEquals(HttpStatus.NOT_FOUND,categoryResult.getStatusCode());
	}

	@Test
	public void testDeleteCategoryFail() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(1001L);
		bundleGroup.setName("New Xyz");
		Category category = new Category();
		category.setId(2002L);
		category.setName("abc");
		category.setDescription("new One");
		Set<BundleGroup> bundleGroups = new HashSet<>();
		bundleGroups.add(bundleGroup);
		category.setBundleGroups(bundleGroups);
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.of(category));
		categoryService.deleteCategory(categoryId);
		ResponseEntity<String> categoryResult = categoryController.deleteCategory(categoryId);
		assertNotNull(categoryResult);
		assertEquals(HttpStatus.EXPECTATION_FAILED,categoryResult.getStatusCode());
	}
	
}
