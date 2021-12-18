package com.entando.hub.catalog.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController;
import com.entando.hub.catalog.rest.BundleGroupVersionController;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;

@Service
public class BundleGroupVersionService {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupVersionRepository bundleGroupVersionRepository;
    
    final private BundleGroupRepository bundleGroupRepository;

    @Autowired
    private Environment environment;

    public BundleGroupVersionService(BundleGroupVersionRepository bundleGroupVersionRepository, BundleGroupRepository bundleGroupRepository) {
    	this.bundleGroupVersionRepository = bundleGroupVersionRepository;
    	this.bundleGroupRepository = bundleGroupRepository;
    }
    public Optional<BundleGroupVersion> getBundleGroupVersion(String bundleGroupVersionId) {
        return bundleGroupVersionRepository.findById(Long.parseLong(bundleGroupVersionId));
    }

    
    @Transactional
    public BundleGroupVersion createBundleGroupVersion(BundleGroupVersion bundleGroupVersionEntity, BundleGroupVersionView bundleGroupVersionView) {
    	if (bundleGroupVersionView.getStatus().equals(BundleGroupVersion.Status.PUBLISHED)) {
    		List<BundleGroupVersion> publishedBundles = bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroupVersionEntity.getBundleGroup(), BundleGroupVersion.Status.PUBLISHED);
    		if (! publishedBundles.isEmpty()) {
    			for(BundleGroupVersion publishedBundle : publishedBundles) {
    				publishedBundle.setStatus(BundleGroupVersion.Status.ARCHIVE);
    			}
    			bundleGroupVersionRepository.saveAll(publishedBundles);
    		}
    	}
    	BundleGroupVersion entity = bundleGroupVersionRepository.save(bundleGroupVersionEntity);
    	return entity;
    }
    
    public Page<BundleGroupVersion> getBundleGroupVersions(Integer pageNum, Integer pageSize, Optional<String> organisationId, String[] categoryIds, String[] statuses) {
        Pageable paging;
        if (pageSize == 0) {
            paging = Pageable.unpaged();
        } else {
            Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
            paging = PageRequest.of(pageNum, pageSize, Sort.by(order));
        }
        Set<Category> categories = Arrays.stream(categoryIds).map(cid -> {
            Category category = new Category();
            category.setId(Long.valueOf(cid));
            return category;
        }).collect(Collectors.toSet());

       Set<BundleGroupVersion.Status> statusSet = Arrays.stream(statuses).map(BundleGroupVersion.Status::valueOf).collect(Collectors.toSet());
        List<BundleGroup> bunleGroups ;
        if (organisationId.isPresent()) {
            Organisation organisation = new Organisation();
            organisation.setId(Long.valueOf(organisationId.get()));
            bunleGroups = bundleGroupRepository.findDistinctByOrganisationAndCategoriesIn(
                    organisation,
                    categories);
        }else {
        	bunleGroups = bundleGroupRepository.findDistinctByCategoriesIn(categories);
        }
        
        Page<BundleGroupVersion> page = bundleGroupVersionRepository.findByBundleGroupInAndStatusIn(bunleGroups, statusSet, paging);  
        setBundleGroupUrl(page);
        return page;
    }
    

	
	/**
	 * Set bundle group url
	 * @param page
	 */
	private void setBundleGroupUrl(Page<BundleGroupVersion> page) {
		String hubGroupDeatilUrl = environment.getProperty("HUB_GROUP_DETAIL_BASE_URL");
		if (Objects.nonNull(hubGroupDeatilUrl)) {
			page.forEach(entity -> entity.setBundleGroupUrl(hubGroupDeatilUrl + "bundlegroupversion/" + entity.getId()));
		}
	}
	
	 public Page<BundleGroupVersion> getBundleGroupVersions(Integer pageNum, Integer pageSize, String[] statuses, BundleGroup bundleGroup) {
	        Pageable paging;
	        if (pageSize == 0) {
	            paging = Pageable.unpaged();
	        } else {
	            Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
	            paging = PageRequest.of(pageNum, pageSize, Sort.by(order));
	        }

	        Set<BundleGroupVersion.Status> statusSet = Arrays.stream(statuses).map(BundleGroupVersion.Status::valueOf).collect(Collectors.toSet());

	        Page<BundleGroupVersion> page = bundleGroupVersionRepository.findByBundleGroupAndStatusIn(bundleGroup, statusSet, paging);  
	        setBundleGroupUrl(page);
	        return page;
	 }
	 
	 @Transactional
	 public void deleteBundleGroupVersion(Optional<com.entando.hub.catalog.persistence.entity.BundleGroupVersion> bundleGroupVersionOptional) {
		 
		    bundleGroupVersionOptional.ifPresent(bundleGroupVersion -> {
		    	bundleGroupVersionRepository.delete(bundleGroupVersion);
		    });
	  }
	    
	    
}
