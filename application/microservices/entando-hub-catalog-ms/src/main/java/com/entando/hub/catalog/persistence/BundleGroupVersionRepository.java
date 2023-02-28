package com.entando.hub.catalog.persistence;

import java.util.List;
import java.util.Set;

import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionOutDto;
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

	@Query(value = "select distinct bgv " +
			"from BundleGroupVersion bgv " +
			"	join bgv.bundles b " +
			"where bgv.status = 'PUBLISHED' " +
			"  and b.descriptorVersion in(:descriptorVersions)" +
			"  and b.gitRepoAddress is not null")
	List<BundleGroupVersion> getPublishedBundleGroups(@Param("descriptorVersions") Set<DescriptorVersion> descriptorVersions);

	Page<BundleGroupVersion> findByBundleGroupAndStatusIn(BundleGroup bundleGroup, Set<BundleGroupVersion.Status> statuses, Pageable pageable);
	
	List<BundleGroupVersion> findByBundleGroupAndVersion(BundleGroup bundleGroup, String version);

	int countByBundleGroup(BundleGroup bundleGroup);
	
	@Query(value = "SELECT * FROM BUNDLE_GROUP_version bgv where bgv.bundle_group_id = :bundleGroupId and (bgv.status in('NOT_PUBLISHED', 'PUBLISH_REQ','DELETE_REQ') or bgv.status = 'PUBLISHED');", nativeQuery = true)
	List<BundleGroupVersion> getByBundleGroupAndStatuses(@Param("bundleGroupId") Long bundleGroupId);

	@Query(value = "select distinct bgv " +
			"from BundleGroupVersion bgv " +
			"	join bgv.bundles b " +
			"where bgv.status = 'PUBLISHED' " +
			"  and b.gitSrcRepoAddress is not null")
	List<BundleGroupVersion> getByTemplateInIt();

	@Query(value = "select distinct bgv " +
			"from BundleGroupVersion bgv " +
			"	join bgv.bundles b " +
			"   join bgv.bundleGroup bg " +
			"where bg.name like :name " +
			"  and bgv.status = 'PUBLISHED' " +
			"  and b.gitSrcRepoAddress is not null")
	List<BundleGroupVersion> getByTemplateInItFilteredByName(@Param("name") String name);

	@Query(value = "select distinct bgv " +
			"from BundleGroupVersion bgv " +
			"	join bgv.bundles b " +
			"where bgv.id = :id " +
			"  and bgv.status = 'PUBLISHED' " +
			"  and b.gitSrcRepoAddress is not null")
	List<BundleGroupVersion> getByTemplateInItAndId(@Param("id") Long id);


}
