package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    final private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;

    }

    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategory(String categoryId){
        return categoryRepository.findById(Long.parseLong(categoryId));
    }

    public Category createCategory(Category toSave){
        return categoryRepository.save(toSave);
    }
}
