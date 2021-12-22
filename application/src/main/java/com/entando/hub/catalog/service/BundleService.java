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

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

@Service
public class BundleService {
    final private BundleRepository bundleRepository;
    final private BundleGroupRepository bundleGroupRepository;
    final private BundleGroupVersionRepository bundleGroupVersionRepository;

    public BundleService(BundleRepository bundleRepository,BundleGroupRepository bundleGroupRepository,BundleGroupVersionRepository bundleGroupVersionRepository) {
        this.bundleRepository = bundleRepository;
        this.bundleGroupRepository = bundleGroupRepository;
        this.bundleGroupVersionRepository = bundleGroupVersionRepository;
    }


    public Page<Bundle> getBundles(Integer pageNum, Integer pageSize, Optional<String> bundleGroupId) {
        Pageable paging;
        if(pageSize == 0){
            paging = Pageable.unpaged();
        }else{
            paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        }
        Page<Bundle> response = new PageImpl<>(new ArrayList<Bundle>());
        if (bundleGroupId.isPresent()) {
            Long bundleGroupEntityId = Long.parseLong(bundleGroupId.get()); 
            Optional<BundleGroup> bundleGroupEntity = bundleGroupRepository.findById(bundleGroupEntityId);
            List<BundleGroupVersion> versionList= bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroupEntity.get(), BundleGroupVersion.Status.PUBLISHED);
            if(versionList !=null  && !versionList.isEmpty())
            response = bundleRepository.findByBundleGroupsIs(bundleGroupEntity.get(), paging);
        } else {
        	List<BundleGroup> bumdleGroups = new ArrayList<BundleGroup>();
        	List<BundleGroupVersion>  budlegroupsVersion = bundleGroupVersionRepository.findDistinctByStatus(BundleGroupVersion.Status.PUBLISHED);
        	for( BundleGroupVersion version  : budlegroupsVersion) {
        		bumdleGroups.add(version.getBundleGroup());
        	}
            response = bundleRepository.findByBundleGroupsIn(bumdleGroups,paging);
        }
        return response;
    }

    public List<Bundle> getBundles(Optional<String> bundleGroupId) {
        if (bundleGroupId.isPresent()) {
            Long bundleGroupEntityId = Long.parseLong(bundleGroupId.get());
            BundleGroup bundleGroupEntity = new BundleGroup();
            bundleGroupEntity.setId(bundleGroupEntityId);
            return bundleRepository.findByBundleGroupsIs(bundleGroupEntity);
        }
        return bundleRepository.findAll();
    }

    public Optional<Bundle> getBundle(String bundleId) {
        return bundleRepository.findById(Long.parseLong(bundleId));
    }

    public Bundle createBundle(Bundle toSave) {
        return bundleRepository.save(toSave);
    }
}
