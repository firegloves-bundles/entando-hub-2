package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BundleService {
    final private BundleRepository bundleRepository;

    public BundleService(BundleRepository bundleRepository) {
        this.bundleRepository = bundleRepository;
    }

    public List<Bundle> getBundles(Optional<String> bundleGroupId){
        if(bundleGroupId.isPresent()){
            Long bundleGroupEntityId = Long.parseLong(bundleGroupId.get());
            BundleGroup bundleGroupEntity = new BundleGroup();
            bundleGroupEntity.setId(bundleGroupEntityId);
            return bundleRepository.findByBundleGroupsIs(bundleGroupEntity);
        }
        return bundleRepository.findAll();
    }

    public Optional<Bundle> getBundle(String bundleId){
        return bundleRepository.findById(Long.parseLong(bundleId));
    }

    public Bundle createBundle(Bundle toSave) {
        return bundleRepository.save(toSave);
    }
    }
