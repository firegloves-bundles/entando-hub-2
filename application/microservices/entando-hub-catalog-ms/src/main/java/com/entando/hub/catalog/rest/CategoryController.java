package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.config.ApplicationConstants;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.dto.CategoryDto;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;
    
    public CategoryController(CategoryMapper categoryMapper, CategoryService categoryService) {
        this.categoryMapper = categoryMapper;
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all the categories", description = "Public api, no authentication required.")
    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<CategoryDto> getCategories() {
        logger.debug("REST request to get Categories");
        return categoryService.getCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get the category details", description = "Public api, no authentication required. You have to provide the categoryId")
    @GetMapping(value = "/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable String categoryId) {
        logger.debug("REST request to get CategoryDto Id: {}", categoryId);
        Optional<com.entando.hub.catalog.persistence.entity.Category> categoryOptional = categoryService.getCategory(categoryId);
        if (categoryOptional.isPresent()) {
            return new ResponseEntity<>(categoryMapper.toDto(categoryOptional.get()), HttpStatus.OK);
        } else {
            logger.warn("Requested category '{}' does not exist", categoryId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new category", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto category) {
        logger.debug("REST request to create CategoryDto: {}", category);
        Category entity = categoryMapper.toEntity(category);
        entity = categoryService.createCategory(entity);
        return new ResponseEntity<>(categoryMapper.toDto(entity), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a category", description = "Protected api, only eh-admin can access it. You have to provide the categoryId identifying the category")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto category) {
        logger.debug("REST request to update CategoryDto {}: {}", categoryId, category);
        Optional<com.entando.hub.catalog.persistence.entity.Category> categoryOptional = categoryService.getCategory(categoryId.toString());
        if (!categoryOptional.isPresent()) {
            logger.warn("CategoryDto '{}' does not exist", categoryId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            Category entity = categoryMapper.toEntity(category);
            entity.setId(categoryId);
            Category savedEntity = categoryService.createCategory(entity);
            return new ResponseEntity<>(categoryMapper.toDto(savedEntity), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete a category", description = "Protected api, only eh-admin can access it. You have to provide the categoryId identifying the category")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<String> deleteCategory(@PathVariable String categoryId) {
        logger.debug("REST request to delete gategory {}", categoryId);
        Optional<com.entando.hub.catalog.persistence.entity.Category> categoryOptional = categoryService.getCategory(categoryId);
        if (!categoryOptional.isPresent()) {
            logger.warn("Requested category '{}' does not exist", categoryId);
            return new ResponseEntity<>(ApplicationConstants.CATEGORY_NOT_EXIST_MSG , HttpStatus.NOT_FOUND);
        } else {
            if (!categoryOptional.get().getBundleGroups().isEmpty()) {
                logger.warn("Requested category '{}' applied to some bundle groups", categoryId);
                return new ResponseEntity<>(ApplicationConstants.CATEGORY_APPLIED_ON_BUNDLE_GROUP_MSG,
                        HttpStatus.EXPECTATION_FAILED);
            } else {
                categoryService.deleteCategory(categoryId);
                return new ResponseEntity<>(ApplicationConstants.CATEGORY_DELETED, HttpStatus.OK);
            }
        }
    }


}
