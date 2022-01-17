package com.entando.hub.catalog.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.rest.BundleGroupController;

@Service
public class BundleGroupService {
    final private BundleGroupRepository bundleGroupRepository;
    final private CategoryRepository categoryRepository;
    private final BundleGroupVersionService bundleGroupVersionService;

    private final Logger logger = LoggerFactory.getLogger(BundleGroupService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();

    public BundleGroupService(BundleGroupRepository bundleGroupRepository, CategoryRepository categoryRepository, 
    		BundleGroupVersionService bundleGroupVersionService,
    		BundleService bundleService) {
        this.bundleGroupRepository = bundleGroupRepository;
        this.categoryRepository = categoryRepository;
        this.bundleGroupVersionService = bundleGroupVersionService;
    }

    public List<BundleGroup> getBundleGroups(Optional<String> organisationId) {
    	logger.debug("{}: getBundleGroups: Get bundle groups organisation id: {}", CLASS_NAME, organisationId);
        if (organisationId.isPresent()) {
            return bundleGroupRepository.findByOrganisationId(Long.parseLong(organisationId.get()));
        }
        return bundleGroupRepository.findAll();
    }

    public Page<BundleGroup> findByOrganisationId(String organisationId, Pageable pageable) {
    	logger.debug("{}: findByOrganisationId: Get bundle groups paginated by organisation id: {}", CLASS_NAME, organisationId);
    	return bundleGroupRepository.findByOrganisationId(Long.valueOf(organisationId), pageable);
    }

    public List<BundleGroup> getBundleGroups(Optional<String> organisationId, Optional<String[]> categoryIds, Optional<String[]> statuses) {
    	logger.debug("{}: getBundleGroups: Get bundle groups paginated by organisation id: {}, categories: {}, statuses: {}", CLASS_NAME, organisationId, categoryIds, statuses);
        if (organisationId.isPresent()) {
            return bundleGroupRepository.findByOrganisationId(Long.parseLong(organisationId.get()));
        }
        return bundleGroupRepository.findAll();
    }

    public Optional<BundleGroup> getBundleGroup(String bundleGroupId) {
    	logger.debug("{}: getBundleGroup: Get a bundle group by bundle group id: {}", CLASS_NAME, bundleGroupId);
    	return bundleGroupRepository.findById(Long.parseLong(bundleGroupId));
    }

    @Transactional
    public BundleGroup createBundleGroup(BundleGroup bundleGroupEntity, BundleGroupController.BundleGroupNoId bundleGroupNoId) {
    	logger.debug("{}: createBundleGroup: Create a bundle group: {}", CLASS_NAME, bundleGroupNoId);
        BundleGroup entity = bundleGroupRepository.save(bundleGroupEntity);
        updateMappedBy(entity, bundleGroupNoId);
        return entity;
    }

    public void updateMappedBy(BundleGroup toUpdate, BundleGroupController.BundleGroupNoId bundleGroup) {
    	logger.debug("{}: updateMappedBy: Update mappings with bundle group", CLASS_NAME);
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

        if (bundleGroup.getVersionDetails() != null) {
        	 Optional<String> optBundleGroupVersionId =  Objects.nonNull(bundleGroup.getVersionDetails().getBundleGroupVersionId()) 
        			 ?  Optional.of(bundleGroup.getVersionDetails().getBundleGroupVersionId())
        					 : Optional.empty();
        	 logger.debug("{}: updateMappedBy: bundle group version id: {}", CLASS_NAME, optBundleGroupVersionId);
        	 bundleGroupVersionService.createBundleGroupVersion(bundleGroup.getVersionDetails().createEntity(optBundleGroupVersionId, toUpdate), bundleGroup.getVersionDetails());
        }
    }

    //This method is called from deleteBundleGroup() from BundleGroupController. In case if we remove Delete Bundle Group api this method also can be removed.
    @Transactional
    public void deleteBundleGroup(String bundleGroupId) {
    	logger.debug("{}: deleteBundleGroup: Delete a bundle group by id: {}", CLASS_NAME, bundleGroupId);
        Long id = Long.valueOf(bundleGroupId);
        Optional<BundleGroup> byId = bundleGroupRepository.findById(id);
        byId.ifPresent(bundleGroup -> {
            deleteFromCategories(bundleGroup);
            bundleGroupRepository.delete(bundleGroup);
        });
    }

    public void deleteFromCategories(BundleGroup bundleGroup) {
    	logger.debug("{}: deleteFromCategories: Delete a bundle group from categoris", CLASS_NAME);
        bundleGroup.getCategories().forEach((category) -> {
            category.getBundleGroups().remove(bundleGroup);
            categoryRepository.save(category);
        });
    }

    public void deleteFromOrganisations(BundleGroup bundleGroup) {
    	logger.debug("{}: deleteFromOrganisations: Delete a bundle group from organisation", CLASS_NAME);
        bundleGroup.getOrganisation().getBundleGroups().remove(bundleGroup);
    }
}
