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
	
	BundleGroupVersion findByBundleGroupAndStatus(BundleGroup bundleGroup, BundleGroupVersion.Status status);
	
	BundleGroupVersion findDistinctByIdAndStatus(Long bundleGroupVesionId, BundleGroupVersion.Status status);

	List<BundleGroupVersion> findDistinctByStatus(BundleGroupVersion.Status status);
	
	Page<BundleGroupVersion> findByBundleGroupAndStatusIn(BundleGroup bundleGroup, Set<BundleGroupVersion.Status> statuses, Pageable pageable);
	
	List<BundleGroupVersion> findByBundleGroupAndVersion(BundleGroup bundleGroup, String version);

	int countByBundleGroup(BundleGroup bundleGroup);
	
	List<BundleGroupVersion> findByBundleGroup(BundleGroup bundleGroup);

	@Query(value = "SELECT * FROM BUNDLE_GROUP_version bgv where bgv.bundle_group_id = :bundleGroupId and (bgv.status in('NOT_PUBLISHED', 'PUBLISH_REQ','DELETE_REQ') or bgv.status = 'PUBLISHED');", nativeQuery = true)
	List<BundleGroupVersion> getByBundleGroupAndStatuses(@Param("bundleGroupId") Long bundleGroupId);

	@Query(value = "select distinct bgv.* " +
			"from bundle_group_version bgv " +
			"         join bundle_group bg on bgv.bundle_group_id = bg.id " +
			"         join bundle_versions bv on bgv.id = bv.bundle_group_version_id " +
			"         join bundle b on bv.bundle_id = b.id " +
			"where bgv.status = 'PUBLISHED' " +
			"  and b.git_src_repo_address is not null;", nativeQuery = true)
	List<BundleGroupVersion> getByTemplateInIt();

	@Query(value = "select distinct bgv.* " +
			"from bundle_group_version bgv " +
			"         join bundle_group bg on bgv.bundle_group_id = bg.id " +
			"         join bundle_versions bv on bgv.id = bv.bundle_group_version_id " +
			"         join bundle b on bv.bundle_id = b.id " +
			"where bgv.status = 'PUBLISHED' " +
			"and bg.name like :name " +
			"and b.git_src_repo_address is not null;", nativeQuery = true)
	List<BundleGroupVersion> getByTemplateInItFilteredByName(@Param("name") String name);

	@Query(value = "select distinct bgv.* " +
			"from bundle_group_version bgv " +
			"         join bundle_group bg on bgv.bundle_group_id = bg.id " +
			"         join bundle_versions bv on bgv.id = bv.bundle_group_version_id " +
			"         join bundle b on bv.bundle_id = b.id " +
			"where bgv.id = :id " +
			"and bgv.status = 'PUBLISHED' " +
			"and b.git_src_repo_address is not null;", nativeQuery = true)
	List<BundleGroupVersion> getByTemplateInItAndId(@Param("id") Long id);


}
