package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.BundleGroupController;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BundleGroupService {
    final private BundleGroupRepository bundleGroupRepository;
    final private CategoryRepository categoryRepository;
    final private BundleRepository bundleRepository;

    public BundleGroupService(BundleGroupRepository bundleGroupRepository, CategoryRepository categoryRepository, BundleRepository bundleRepository) {
        this.bundleGroupRepository = bundleGroupRepository;
        this.categoryRepository = categoryRepository;
        this.bundleRepository = bundleRepository;
    }

    public List<BundleGroup> getBundleGroups(Optional<String> organisationId) {
        if (organisationId.isPresent()) {
            return bundleGroupRepository.findByOrganisationId(Long.parseLong(organisationId.get()));
        }
        return bundleGroupRepository.findAll();
    }

    public Optional<BundleGroup> getBundleGroup(String bundleGroupId) {
        return bundleGroupRepository.findById(Long.parseLong(bundleGroupId));
    }

    @Transactional
    public BundleGroup createBundleGroup(BundleGroup bundleGroupEntity, BundleGroupController.BundleGroupNoId bundleGroupNoId) {
        BundleGroup entity = bundleGroupRepository.save(bundleGroupEntity);
        updateMappedBy(entity, bundleGroupNoId);
        return entity;
    }


    public void updateMappedBy(BundleGroup toUpdate, BundleGroupController.BundleGroupNoId bundleGroup) {
        Objects.requireNonNull(toUpdate.getId());
        if (bundleGroup.getCategories() != null) {
            //remove the bundle group from all the categories containing it
            //TODO native query to improve performance
            categoryRepository.findByBundleGroupsIs(toUpdate).stream().forEach(category -> {
                category.getBundleGroups().remove(toUpdate);
                categoryRepository.save(category);
            });
            Set<Category> categorySet = bundleGroup.getCategories().stream().map((categoryId) -> {
                Category category = categoryRepository.findById(Long.valueOf(categoryId)).get();
                category.getBundleGroups().add(toUpdate);
                categoryRepository.save(category);
                return category;
            }).collect(Collectors.toSet());
            toUpdate.setCategories(categorySet);
        }
        if (bundleGroup.getChildren() != null) {
            //TODO native query to improve performance
            bundleRepository.findByBundleGroupsIs(toUpdate).stream().forEach(bundle -> {
                bundle.getBundleGroups().remove(toUpdate);
                bundleRepository.save(bundle);
            });
            Set<Bundle> bundleSet = bundleGroup.getChildren().stream().map((bundleChildId) -> {
                com.entando.hub.catalog.persistence.entity.Bundle bundle = bundleRepository.findById(Long.valueOf(bundleChildId)).get();
                bundle.getBundleGroups().add(toUpdate);
                bundleRepository.save(bundle);
                return bundle;
            }).collect(Collectors.toSet());
            toUpdate.setBundles(bundleSet);
        }

    }
}
