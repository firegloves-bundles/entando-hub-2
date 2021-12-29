package com.entando.hub.catalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

@Service
public class BundleService {
    final private BundleRepository bundleRepository;
    final private BundleGroupVersionRepository bundleGroupVersionRepository;

    public BundleService(BundleRepository bundleRepository, BundleGroupVersionRepository bundleGroupVersionRepository) {
        this.bundleRepository = bundleRepository;
        this.bundleGroupVersionRepository = bundleGroupVersionRepository;
    }

	public Page<Bundle> getBundles(Integer pageNum, Integer pageSize, Optional<String> bundleGroupVersionId) {
		Pageable paging;
		if (pageSize == 0) {
			paging = Pageable.unpaged();
		} else {
			paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "name"));
		}
		Page<Bundle> response = new PageImpl<>(new ArrayList<Bundle>());
		if (bundleGroupVersionId.isPresent()) {
			Long bundleGroupVersoinEntityId = Long.parseLong(bundleGroupVersionId.get());
			Optional<BundleGroupVersion> bundleGroupVersionEntity = bundleGroupVersionRepository.findById(bundleGroupVersoinEntityId);
			if (bundleGroupVersionEntity.isPresent()) {
				BundleGroupVersion version = bundleGroupVersionRepository.findDistinctByIdAndStatus(bundleGroupVersionEntity.get().getId(), BundleGroupVersion.Status.PUBLISHED);
				if (version != null)
					response = bundleRepository.findByBundleGroupVersionsIs(version, paging);
			}
		} else {
			List<BundleGroupVersion> budlegroupsVersion = bundleGroupVersionRepository.findDistinctByStatus(BundleGroupVersion.Status.PUBLISHED);
			response = bundleRepository.findByBundleGroupVersionsIn(budlegroupsVersion, paging);
		}
		return response;
	}

    public List<Bundle> getBundles(Optional<String> bundleGroupVersionId) {
        if (bundleGroupVersionId.isPresent()) {
            Long bundleGroupVersioEntityId = Long.parseLong(bundleGroupVersionId.get());
            BundleGroupVersion bundleGroupVersionEntity = new BundleGroupVersion();
            bundleGroupVersionEntity.setId(bundleGroupVersioEntityId);
            return bundleRepository.findByBundleGroupVersionsIs(bundleGroupVersionEntity);
        }
        return bundleRepository.findAll();
    }

    public Optional<Bundle> getBundle(String bundleId) {
        return bundleRepository.findById(Long.parseLong(bundleId));
    }

    public Bundle createBundle(Bundle toSave) {
        return bundleRepository.save(toSave);
    }
    
    public void deleteBundle(Bundle toDelete){
         deleteFromBundleGroupVersion(toDelete);
         bundleRepository.delete(toDelete);
    }
    
    public void deleteFromBundleGroupVersion(Bundle bundle) {
       
    }
}
