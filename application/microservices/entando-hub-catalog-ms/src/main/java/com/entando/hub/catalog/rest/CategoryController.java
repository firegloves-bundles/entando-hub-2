package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.config.ApplicationConstants;
import com.entando.hub.catalog.rest.domain.CategoryDto;
import com.entando.hub.catalog.rest.domain.CategoryNoId;
import com.entando.hub.catalog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all the categories", description = "Public api, no authentication required.")
    @GetMapping(value = "/", produces = {"application/json"})
    public List<CategoryDto> getCategories() {
        logger.debug("REST request to get Categories");
        return categoryService.getCategories().stream().map(CategoryDto::new).collect(Collectors.toList());
    }

    @Operation(summary = "Get the category details", description = "Public api, no authentication required. You have to provide the categoryId")
    @GetMapping(value = "/{categoryId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable String categoryId) {
        logger.debug("REST request to get CategoryDto Id: {}", categoryId);
        Optional<com.entando.hub.catalog.persistence.entity.Category> categoryOptional = categoryService.getCategory(categoryId);
        if (categoryOptional.isPresent()) {
            return new ResponseEntity<>(categoryOptional.map(CategoryDto::new).get(), HttpStatus.OK);
        } else {
            logger.warn("Requested category '{}' does not exist", categoryId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new category", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryNoId category) {
        logger.debug("REST request to create CategoryDto: {}", category);
        com.entando.hub.catalog.persistence.entity.Category entity = categoryService.createCategory(category.createEntity(Optional.empty()));
        return new ResponseEntity<>(new CategoryDto(entity), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a category", description = "Protected api, only eh-admin can access it. You have to provide the categoryId identifying the category")
    @RolesAllowed({ADMIN})
    @PostMapping(value = "/{categoryId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String categoryId, @RequestBody CategoryNoId category) {
        logger.debug("REST request to update CategoryDto {}: {}", categoryId, category);
        Optional<com.entando.hub.catalog.persistence.entity.Category> categoryOptional = categoryService.getCategory(categoryId);
        if (!categoryOptional.isPresent()) {
            logger.warn("CategoryDto '{}' does not exist", categoryId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            com.entando.hub.catalog.persistence.entity.Category savedEntity = categoryService.createCategory(category.createEntity(Optional.of(categoryId)));
            return new ResponseEntity<>(new CategoryDto(savedEntity), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete a category", description = "Protected api, only eh-admin can access it. You have to provide the categoryId identifying the category")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{categoryId}", produces = {"application/json"})
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
