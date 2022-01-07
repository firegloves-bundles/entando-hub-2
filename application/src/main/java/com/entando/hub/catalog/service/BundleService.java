package com.entando.hub.catalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.BundleController;

@Service
public class BundleService {

    final private BundleRepository bundleRepository;
    final private BundleGroupVersionRepository bundleGroupVersionRepository;
    final private BundleGroupRepository bundleGroupRepository;

    private final Logger logger = LoggerFactory.getLogger(BundleController.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();

    public BundleService(BundleRepository bundleRepository, BundleGroupVersionRepository bundleGroupVersionRepository,BundleGroupRepository bundleGroupRepository) {
        this.bundleRepository = bundleRepository;
        this.bundleGroupVersionRepository = bundleGroupVersionRepository;
        this.bundleGroupRepository =  bundleGroupRepository;
    }

    public Page<Bundle> getBundles(Integer pageNum, Integer pageSize, Optional<String> bundleGroupId) {
		logger.debug("{}: getBundles: Get bundles paginated by bundle group  id: {}", CLASS_NAME, bundleGroupId);
		Pageable paging;
		if (pageSize == 0) {
			paging = Pageable.unpaged();
		} else {
			paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "name"));
		}
		Page<Bundle> response = new PageImpl<>(new ArrayList<Bundle>());
		if (bundleGroupId.isPresent()) {
			Long bundleGroupEntityId = Long.parseLong(bundleGroupId.get());
			Optional<BundleGroup> bundleGroupEntity = bundleGroupRepository.findById(bundleGroupEntityId);
			if (bundleGroupEntity.isPresent()) {
				BundleGroupVersion publishedVersion = bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroupEntity.get(), BundleGroupVersion.Status.PUBLISHED);
				if (publishedVersion != null)
					response = bundleRepository.findByBundleGroupVersionsIs(publishedVersion, paging);
			} else {
				logger.warn("{}: getBundles: bundle group does not exist: {}", CLASS_NAME, bundleGroupEntityId);
			}
		} else {
			logger.debug("{}: getBundles: bundle group id is not present: {}", CLASS_NAME, bundleGroupId);
			List<BundleGroupVersion> budlegroupsVersion = bundleGroupVersionRepository.findDistinctByStatus(BundleGroupVersion.Status.PUBLISHED);
			response = bundleRepository.findByBundleGroupVersionsIn(budlegroupsVersion, paging);
		}
		return response;
	}

    public List<Bundle> getBundles(Optional<String> bundleGroupVersionId) {
    	logger.debug("{}: getBundles: Get Bundles by bundle group version id: {}", CLASS_NAME, bundleGroupVersionId);
        if (bundleGroupVersionId.isPresent()) {
            Long bundleGroupVersioEntityId = Long.parseLong(bundleGroupVersionId.get());
            BundleGroupVersion bundleGroupVersionEntity = new BundleGroupVersion();
            bundleGroupVersionEntity.setId(bundleGroupVersioEntityId);
            return bundleRepository.findByBundleGroupVersionsIs(bundleGroupVersionEntity);
        }
        return bundleRepository.findAll();
    }

    public Optional<Bundle> getBundle(String bundleId) {
    	logger.debug("{}: getBundle: Get a Bundle by bundle id: {}", CLASS_NAME, bundleId);
        return bundleRepository.findById(Long.parseLong(bundleId));
    }

    public Bundle createBundle(Bundle toSave) {
    	logger.debug("{}: createBundle: Create a Bundle: {}", CLASS_NAME, toSave);
        return bundleRepository.save(toSave);
    }
    
    public void deleteBundle(Bundle toDelete){
    	logger.debug("{}: deleteBundle: Delete a Bundle: {}", CLASS_NAME, toDelete);
         deleteFromBundleGroupVersion(toDelete);
         bundleRepository.delete(toDelete);
    }
    
    public void deleteFromBundleGroupVersion(Bundle bundle) {
       
    }
}
