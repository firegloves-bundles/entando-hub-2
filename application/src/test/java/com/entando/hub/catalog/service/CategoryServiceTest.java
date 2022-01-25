package com.entando.hub.catalog.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.Category;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class CategoryServiceTest {
	@InjectMocks
	CategoryService categoryService;
	@Mock
	CategoryRepository categoryRepository;
	
	@Test
	public void getCategoriesTest() {
		List<Category> categoryList = new ArrayList<>();
		Category category = new Category();
		category.setId(3001L);
		category.setName("Technical");
		category.setDescription("New Organisation");
		categoryList.add(category);
		Mockito.when(categoryRepository.findAll()).thenReturn(categoryList);
		List<Category> categoryResult = categoryService.getCategories();
		assertNotNull(categoryResult);
		assertEquals(categoryList.get(0).getId(), categoryResult.get(0).getId());
		assertEquals(categoryList.get(0).getName(), categoryResult.get(0).getName());
		assertEquals(categoryList.get(0).getDescription(), categoryResult.get(0).getDescription());
}
	
	@Test
	public void getCategoryTest() {
		Category category = new Category();
		Optional<Category> categoryList = Optional.of(category);
		category.setId(3001L);
		category.setName("Technical");
		category.setDescription("New Organisation");
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryRepository.findById(Long.parseLong(categoryId))).thenReturn(categoryList);
		Optional<Category> categoryResult = categoryService.getCategory(categoryId);
		assertNotNull(categoryResult);
		assertEquals(categoryList.get().getId(), categoryResult.get().getId());
		assertEquals(categoryList.get().getName(), categoryResult.get().getName());
		assertEquals(categoryList.get().getDescription(), categoryResult.get().getDescription());
}
	
	@Test
	public void createCategoryTest() {
		Category category = new Category();
		category.setId(3001L);
		category.setName("Technical");
		category.setDescription("New Organisation");
		Mockito.when(categoryRepository.save(category)).thenReturn(category);
		Category categoryResult = categoryService.createCategory(category);
		assertNotNull(categoryResult);
		assertEquals(category, categoryResult);
}
	@Test
	public void deleteCategoryTest() {
		Category category = new Category();
		category.setId(3001L);
		category.setName("Technical");
		category.setDescription("New Organisation");
		categoryRepository.deleteById(category.getId());
		String categoryId = String.valueOf(category.getId());
		categoryService.deleteCategory(categoryId);
}

}
