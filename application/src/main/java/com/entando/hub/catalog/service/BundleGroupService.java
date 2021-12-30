package com.entando.hub.catalog.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController;

@Service
public class BundleGroupService {
    final private BundleGroupRepository bundleGroupRepository;
    final private CategoryRepository categoryRepository;
    private final BundleGroupVersionService bundleGroupVersionService;

    public BundleGroupService(BundleGroupRepository bundleGroupRepository, CategoryRepository categoryRepository, BundleGroupVersionService bundleGroupVersionService) {
        this.bundleGroupRepository = bundleGroupRepository;
        this.categoryRepository = categoryRepository;
        this.bundleGroupVersionService = bundleGroupVersionService;
    }

    public List<BundleGroup> getBundleGroups(Optional<String> organisationId) {
        if (organisationId.isPresent()) {
            return bundleGroupRepository.findByOrganisationId(Long.parseLong(organisationId.get()));
        }
        return bundleGroupRepository.findAll();
    }

    public Page<BundleGroup> getBundleGroups(Integer pageNum, Integer pageSize, Optional<String> organisationId, String[] categoryIds, String[] statuses) {
        Pageable paging;
        if (pageSize == 0) {
            paging = Pageable.unpaged();
        } else {
            Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name");
            paging = PageRequest.of(pageNum, pageSize, Sort.by(order));
        }
        Set<Category> categories = Arrays.stream(categoryIds).map(cid -> {
            Category category = new Category();
            category.setId(Long.valueOf(cid));
            return category;
        }).collect(Collectors.toSet());

        if (organisationId.isPresent()) {
            Organisation organisation = new Organisation();
            organisation.setId(Long.valueOf(organisationId.get()));
            Page<BundleGroup> page = bundleGroupRepository.findDistinctByOrganisationAndCategoriesIn(
                    organisation,
                    categories,
                    paging);
            return page;
        }

        Page<BundleGroup> page = bundleGroupRepository.findDistinctByCategoriesIn(
                categories
                ,paging);
        return page;
    }

    public Page<BundleGroup> findByOrganisationId(String organisationId, Pageable pageable) {
    	return bundleGroupRepository.findByOrganisationId(Long.valueOf(organisationId), pageable);
    }

    public List<BundleGroup> getBundleGroups(Optional<String> organisationId, Optional<String[]> categoryIds, Optional<String[]> statuses) {
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
	    if (bundleGroup.getChildren() != null && Objects.nonNull(bundleGroup.getVersionDetails())) {
	    	bundleGroup.getVersionDetails().setChildren(bundleGroup.getChildren());
	    }

        if (bundleGroup.getVersionDetails() != null) {
        	 Optional<String> opt =  Objects.nonNull(bundleGroup.getVersionDetails().getBundleGroupVersionId()) 
        			 ?  Optional.of(bundleGroup.getVersionDetails().getBundleGroupVersionId())
        					 : Optional.empty();

        	 bundleGroupVersionService.createBundleGroupVersion(bundleGroup.getVersionDetails().createEntity(opt, toUpdate), bundleGroup.getVersionDetails());
        }
    }

    @Transactional
    public void deleteBundleGroup(String bundleGroupId) {
        Long id = Long.valueOf(bundleGroupId);
        Optional<BundleGroup> byId = bundleGroupRepository.findById(id); //No need to fetch here from db, can be passed from controller
        byId.ifPresent(bundleGroup -> {
            deleteFromCategories(bundleGroup);
//            TODO: Delete the bundle group from Bundle Group Versions
            bundleGroupRepository.delete(bundleGroup);
        });
    }

    public void deleteFromCategories(BundleGroup bundleGroup) {
        bundleGroup.getCategories().forEach((category) -> {
            category.getBundleGroups().remove(bundleGroup);
            categoryRepository.save(category);
        });
    }
}
