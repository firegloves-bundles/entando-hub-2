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
    
//  EHUB-147 commented
//    final private BundleGroupRepository bundleGroupRepository;
    final private BundleGroupVersionRepository bundleGroupVersionRepository;

//    public BundleService(BundleRepository bundleRepository, BundleGroupRepository bundleGroupRepository,BundleGroupVersionRepository bundleGroupVersionRepository) {
    public BundleService(BundleRepository bundleRepository, BundleGroupVersionRepository bundleGroupVersionRepository) {
        this.bundleRepository = bundleRepository;
//        this.bundleGroupRepository = bundleGroupRepository;
        this.bundleGroupVersionRepository = bundleGroupVersionRepository;
    }


//    EHUB-147 commented
//    public Page<Bundle> getBundles(Integer pageNum, Integer pageSize, Optional<String> bundleGroupId) {
//        Pageable paging;
//        if(pageSize == 0){
//            paging = Pageable.unpaged();
//        }else{
//            paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "name"));
//        }
//        Page<Bundle> response = new PageImpl<>(new ArrayList<Bundle>());
//        if (bundleGroupId.isPresent()) {
//            Long bundleGroupEntityId = Long.parseLong(bundleGroupId.get()); 
//            Optional<BundleGroup> bundleGroupEntity = bundleGroupRepository.findById(bundleGroupEntityId);
//            List<BundleGroupVersion> versionList= bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroupEntity.get(), BundleGroupVersion.Status.PUBLISHED);
//            if(versionList !=null  && !versionList.isEmpty())
//            response = bundleRepository.findByBundleGroupsIs(bundleGroupEntity.get(), paging);
//        } else {
//        	List<BundleGroup> bumdleGroups = new ArrayList<BundleGroup>();
//        	List<BundleGroupVersion>  budlegroupsVersion = bundleGroupVersionRepository.findDistinctByStatus(BundleGroupVersion.Status.PUBLISHED);
//        	for( BundleGroupVersion version  : budlegroupsVersion) {
//        		bumdleGroups.add(version.getBundleGroup());
//        	}
//            response = bundleRepository.findByBundleGroupsIn(bumdleGroups,paging);
//        }
//        return response;
//    }
    
//    EHUB-147 newly added
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
				List<BundleGroupVersion> versionList = bundleGroupVersionRepository.findByBundleGroupVersionAndStatus(bundleGroupVersionEntity.get(), BundleGroupVersion.Status.PUBLISHED);
				if (versionList != null && !versionList.isEmpty())
					response = bundleRepository.findByBundleGroupVersionsIs(bundleGroupVersionEntity.get(), paging);
			}
		} else {
			List<BundleGroupVersion> bumdleGroupVersions = new ArrayList<BundleGroupVersion>();
			List<BundleGroupVersion> budlegroupsVersion = bundleGroupVersionRepository
					.findDistinctByStatus(BundleGroupVersion.Status.PUBLISHED);
			for (BundleGroupVersion version : budlegroupsVersion) {
				bumdleGroupVersions.add(version);
			}
			response = bundleRepository.findByBundleGroupVersionsIn(bumdleGroupVersions, paging);
		}
		return response;
	}

//    EHUB-147 commented
//    public List<Bundle> getBundles(Optional<String> bundleGroupId) {
//        if (bundleGroupId.isPresent()) {
//            Long bundleGroupEntityId = Long.parseLong(bundleGroupId.get());
//            BundleGroup bundleGroupEntity = new BundleGroup();
//            bundleGroupEntity.setId(bundleGroupEntityId);
//            return bundleRepository.findByBundleGroupsIs(bundleGroupEntity);
//        }
//        return bundleRepository.findAll();
//    }
//    EHUB-147 newly added method
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
