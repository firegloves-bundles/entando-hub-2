package com.entando.hub.catalog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;

@Service
public class BundleService {

    final private BundleRepository bundleRepository;
    final private BundleGroupVersionRepository bundleGroupVersionRepository;
    final private BundleGroupRepository bundleGroupRepository;

    private final Logger logger = LoggerFactory.getLogger(BundleService.class);
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

    /**
     * Save list of bundles
     * @param bundles
     * @return list of saved bundles
     */
	public List<Bundle> createBundles(List<Bundle> bundles) {
		logger.debug("{}: createBundles: Create bundles: {}", CLASS_NAME, bundles);
		return bundleRepository.saveAll(bundles);
	}

	/**
	 * Convert list of bundle request into list of Bundle entity.
	 * @param bundleRequest
	 * @return list of saved bundles or empty list
	 */
	public List<Bundle> createBundleEntitiesAndSave(List<BundleNoId> bundleRequest) {
		logger.debug("{}: createBundleEntitiesAndSave: Create bundles: {}", CLASS_NAME, bundleRequest);
		try {
			List<Bundle> bundles = new ArrayList<Bundle>();
			if (!CollectionUtils.isEmpty(bundleRequest)) {
				bundleRequest.forEach((element) -> {
					Optional<String> opt = Objects.nonNull(element.getBundleId()) ? Optional.of(element.getBundleId())
							: Optional.empty();
					bundles.add(element.createEntity(opt));
				});
				return createBundles(bundles);
			}
		} catch (Exception e) {
			logger.debug("{}: createBundleEntitiesAndSave: Error {} {}", CLASS_NAME, e.getMessage(), e.getStackTrace());
		}
		return Collections.emptyList();
	}
}
