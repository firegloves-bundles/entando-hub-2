package com.entando.hub.catalog.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

public interface BundleGroupVersionRepository extends JpaRepository<BundleGroupVersion, Long>{
	

	Page<BundleGroupVersion> findByBundleGroupInAndStatusIn(List<BundleGroup> bundleGroup, Set<BundleGroupVersion.Status> statuses, Pageable pageable);
	
	List<BundleGroupVersion> findByBundleGroupAndStatus(BundleGroup bundleGroup, BundleGroupVersion.Status status);
	
	List<BundleGroupVersion> findByBundleGroupVersionAndStatus(BundleGroupVersion bundleGroupVesion, BundleGroupVersion.Status status);

	List<BundleGroupVersion> findDistinctByStatus(BundleGroupVersion.Status status);
	
	Page<BundleGroupVersion> findByBundleGroupAndStatusIn(BundleGroup bundleGroup, Set<BundleGroupVersion.Status> statuses, Pageable pageable);
	
	List<BundleGroupVersion> findByBundleGroupAndVersion(BundleGroup bundleGroup, String version);

	int countByBundleGroup(BundleGroup bundleGroup);
	
	List<BundleGroupVersion> findByBundleGroup(BundleGroup bundleGroup);

	@Query(value = "SELECT * FROM BUNDLE_GROUP_version bgv where bgv.bundle_group_id = :bundleGroupId and (bgv.status in('NOT_PUBLISHED', 'PUBLISH_REQ','DELETE_REQ') or bgv.status = 'PUBLISHED');", nativeQuery = true)
	List<BundleGroupVersion> getByBundleGroupAndStatuses(@Param("bundleGroupId") Long bundleGroupId);


}
