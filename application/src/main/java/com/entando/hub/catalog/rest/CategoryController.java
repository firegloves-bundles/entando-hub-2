package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.service.CategoryService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    //@RolesAllowed("codemotion-bff-admin")
    //@PreAuthorize("hasAuthority('ROLE_mf-widget-admin')")
    @CrossOrigin
    @GetMapping("/")
    public List<Category> getCategories() {
        logger.debug("REST request to get Categories");
        return categoryService.getCategories().stream().map(Category::new).collect(Collectors.toList());
    }

    @CrossOrigin
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable String categoryId) {
        logger.debug("REST request to get Category Id: {}", categoryId);
        Optional<com.entando.hub.catalog.persistence.entity.Category> categoryOptional = categoryService.getCategory(categoryId);
        if (categoryOptional.isPresent()) {
            return new ResponseEntity<>(categoryOptional.map(Category::new).get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PostMapping("/")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryNoId category) {
        logger.debug("REST request to create Category: {}", category);
        com.entando.hub.catalog.persistence.entity.Category entity = categoryService.createCategory(category.createEntity(Optional.empty()));
        return new ResponseEntity<>(new Category(entity), HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable String categoryId, @RequestBody CategoryNoId category) {
        logger.debug("REST request to update Category {}: {}", categoryId, category);
        Optional<com.entando.hub.catalog.persistence.entity.Category> categoryOptional = categoryService.getCategory(categoryId);
        if (!categoryOptional.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            com.entando.hub.catalog.persistence.entity.Category savedEntity = categoryService.createCategory(category.createEntity(Optional.of(categoryId)));
            return new ResponseEntity<>(new Category(savedEntity), HttpStatus.OK);
        }
    }


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = false)
    public static class Category extends CategoryNoId {
        private final String categoryId;

        public Category(com.entando.hub.catalog.persistence.entity.Category entity) {
            super(entity);
            this.categoryId = entity.getId().toString();
        }

        public Category(String organisationId, String name, String description) {
            super(name, description);
            this.categoryId = organisationId;
        }


    }

    @Data
    public static class CategoryNoId {
        protected final String name;
        protected final String description;
        protected List<String> bundleGroups;

        public CategoryNoId(String name, String description) {
            this.name = name;
            this.description = description;

        }

        public CategoryNoId(com.entando.hub.catalog.persistence.entity.Category entity) {
            this.name = entity.getName();
            this.description = entity.getDescription();
            if (entity.getBundleGroups() != null) {
                this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());
            }
        }

        public com.entando.hub.catalog.persistence.entity.Category createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.Category ret = new com.entando.hub.catalog.persistence.entity.Category();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            if (this.getBundleGroups() != null) {
                ret.setBundleGroups(this.getBundleGroups().stream().map(bundleGroupId -> {
                    BundleGroup bundleGroup = new BundleGroup();
                    bundleGroup.setId(Long.valueOf(bundleGroupId));
                    return bundleGroup;
                }).collect(Collectors.toSet()));
            }
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }

    }



}
