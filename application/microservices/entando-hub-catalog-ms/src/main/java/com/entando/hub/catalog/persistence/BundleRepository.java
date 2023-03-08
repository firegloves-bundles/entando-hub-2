package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Page<Bundle> findByBundleGroupVersionsIsAndDescriptorVersionIn(BundleGroupVersion bundleGroupVersions, Collection<Bundle.DescriptorVersion> descriptorVersion, Pageable pageable);
    Page<Bundle> findByBundleGroupVersionsInAndDescriptorVersionIn(
            List<BundleGroupVersion> bundleGroupVersions, Collection<Bundle.DescriptorVersion> descriptorVersion, Pageable pageable);
    List<Bundle> findByBundleGroupVersions(BundleGroupVersion bundleGroupVersion, Sort sort);

    List<Bundle> findByBundleGroupVersions_IdAndBundleGroupVersions_BundleGroup_PublicCatalogTrue(Long id, Sort sort);

    List<Bundle> findByBundleGroupVersions_BundleGroup_CatalogId(Long catalogId);

    List<Bundle> findByBundleGroupVersions_BundleGroup_CatalogIdAndBundleGroupVersions_Id(Long catalogId, Long id);

    List<Bundle> findByBundleGroupVersions_BundleGroup_PublicCatalogTrue();

    List<Bundle> findByBundleGroupVersions_Id(Long id, Sort sort);
}
