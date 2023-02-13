package com.entando.hub.catalog.rest;


import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.domain.CategoryNoId;
import com.entando.hub.catalog.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
	
	@Autowired
	WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;
    
    @InjectMocks
	CategoryController categoryController;
	
	@MockBean
	CategoryService categoryService;
	
    private static final String URI = "/api/category/";
    
    private static final Long CATEGORY_ID = 3000L;
    private static final String CATEGORY_NAME = "Test Category Name";
    private static final String CATEGORY_DESCRIPTION = "Test Category Description";
    
    private static final Long BUNDLE_GROUP_ID = 1000L;
    private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
	
    @Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testGetCategories() throws Exception {
		Category category = getCategoryObj();
		Mockito.when(categoryService.getCategories()).thenReturn(List.of(category));
		
		mockMvc.perform(MockMvcRequestBuilders.get(URI)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].categoryId").value(category.getId().toString()))
				.andExpect(jsonPath("$.[*].name").value(category.getName()));
	}
	
	@Test
	public void testGetCategory() throws Exception {
		Category category = getCategoryObj();
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.of(category));
		
		mockMvc.perform(MockMvcRequestBuilders.get(URI + categoryId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.categoryId").value(category.getId().toString()))
				.andExpect(jsonPath("$.name").value(category.getName()));
	}
	
	@Test
	public void testGetCategoryFails() throws Exception {
		Category category = getCategoryObj();
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(null)).thenReturn(Optional.of(category));

		mockMvc.perform(MockMvcRequestBuilders.get(URI + categoryId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(roles = { ADMIN })
	public void testCreateCategory() throws Exception {
		Category category = getCategoryObj();
		CategoryNoId categoryNoId = new CategoryNoId(category);
		Mockito.when(categoryService.createCategory(categoryNoId.createEntity(Optional.empty()))).thenReturn(category);
		
		mockMvc.perform(MockMvcRequestBuilders.post(URI)
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(categoryNoId))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.categoryId").value(category.getId().toString()))
			.andExpect(jsonPath("$.name").value(category.getName()));
	}
	
	@Test
	@WithMockUser(roles = { ADMIN })
	public void testUpdateCategory() throws Exception {
	Category category = getCategoryObj();
	CategoryNoId categoryNoId = new CategoryNoId(category);
	String categoryId = Long.toString(category.getId());
	Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.of(category));
	Mockito.when(categoryService.createCategory(category)).thenReturn(category);

	mockMvc.perform(MockMvcRequestBuilders.post(URI + categoryId)
		.contentType(MediaType.APPLICATION_JSON)
		.content(asJsonString(categoryNoId))
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.categoryId").value(category.getId().toString()))
		.andExpect(jsonPath("$.name").value(category.getName()));
	}
	
	@Test
	@WithMockUser(roles = { ADMIN })
	public void testUpdateCategoryFails() throws Exception {
		Category category = getCategoryObj();
		CategoryNoId categoryNoId = new CategoryNoId(category);
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.empty());
		Mockito.when(categoryService.createCategory(category)).thenReturn(category);

		mockMvc.perform(MockMvcRequestBuilders.post(URI + categoryId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(categoryNoId))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(roles = { ADMIN })
	public void testDeleteCategory() throws Exception {
		Category category = getCategoryObj();
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.of(category));
		mockMvc.perform(MockMvcRequestBuilders.delete(URI + categoryId)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().is(HttpStatus.EXPECTATION_FAILED.value()));
		
		
		category.setBundleGroups(new HashSet<>());
		mockMvc.perform(MockMvcRequestBuilders.delete(URI + categoryId)
			.accept(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles = { ADMIN })
	public void testDeleteCategoryFails() throws Exception {
		Category category = getCategoryObj();
		String categoryId = Long.toString(category.getId());
		Mockito.when(categoryService.getCategory(null)).thenReturn(Optional.of(category));
		Mockito.when(categoryService.getCategory(categoryId)).thenReturn(Optional.empty());
		categoryService.getCategory(categoryId);
		mockMvc.perform(MockMvcRequestBuilders.delete(URI + categoryId)
			.accept(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isNotFound());
	}

	private Category getCategoryObj() {
		Category category = new Category();
		category.setId(CATEGORY_ID);
		category.setName(CATEGORY_NAME);
		category.setDescription(CATEGORY_DESCRIPTION);
		category.setBundleGroups(Set.of(getBundleGroupObj()));
		return category;
	}

	private BundleGroup getBundleGroupObj() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUP_ID);
		bundleGroup.setName(BUNDLE_GROUP_NAME);
		return bundleGroup;
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
