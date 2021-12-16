package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BundleGroupService {
    final private BundleGroupRepository bundleGroupRepository;
    final private CategoryRepository categoryRepository;
    final private BundleRepository bundleRepository;
    private final BundleGroupVersionService bundleGroupVersionService;
    
//    @Autowired
//    private Environment environment;

    public BundleGroupService(BundleGroupRepository bundleGroupRepository, CategoryRepository categoryRepository, BundleRepository bundleRepository, BundleGroupVersionService bundleGroupVersionService) {
        this.bundleGroupRepository = bundleGroupRepository;
        this.categoryRepository = categoryRepository;
        this.bundleRepository = bundleRepository;
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

     //   Set<BundleGroup.Status> statusSet = Arrays.stream(statuses).map(BundleGroupVersion.Status::valueOf).collect(Collectors.toSet());
        if (organisationId.isPresent()) {
            Organisation organisation = new Organisation();
            organisation.setId(Long.valueOf(organisationId.get()));
            Page<BundleGroup> page = bundleGroupRepository.findDistinctByOrganisationAndCategoriesIn(
                    organisation,
                    categories,
                    paging);
          //  setBundleGroupUrl(page);
            return page;
        }

        Page<BundleGroup> page = bundleGroupRepository.findDistinctByCategoriesIn(
                categories
                ,paging);
       // setBundleGroupUrl(page);
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
        if (bundleGroup.getVersion() != null) {
    	      bundleGroupVersionService.createBundleGroupVersion(bundleGroup.getVersion().createEntity(Optional.empty(),toUpdate), bundleGroup.getVersion());
        }

    }

    @Transactional
    public void deleteBundleGroup(String bundleGroupId) {
        Long id = Long.valueOf(bundleGroupId);
        Optional<BundleGroup> byId = bundleGroupRepository.findById(id);
        byId.ifPresent(bundleGroup -> {
            deleteFromCategories(bundleGroup);
            deleteFromBundles(bundleGroup);
            bundleGroupRepository.delete(bundleGroup);
        });
    }
    public void deleteFromCategories(BundleGroup bundleGroup) {
        bundleGroup.getCategories().forEach((category) -> {
            category.getBundleGroups().remove(bundleGroup);
            categoryRepository.save(category);
        });
    }
    public void deleteFromBundles(BundleGroup bundleGroup) {
        bundleGroup.getBundles().forEach((bundle) -> {
            bundle.getBundleGroups().remove(bundleGroup);
            bundleRepository.save(bundle);
        });
    }

//	/**
//	 * Set bundle group url
//	 * @param page
//	 */
//	private void setBundleGroupUrl(Page<BundleGroup> page) {
//		String hubGroupDeatilUrl = environment.getProperty("HUB_GROUP_DETAIL_BASE_URL");
//		if (Objects.nonNull(hubGroupDeatilUrl)) {
//			page.forEach(entity -> entity.setBundleGroupUrl(hubGroupDeatilUrl + "bundlegroup/" + entity.getId()));
//		}
//	}
}
