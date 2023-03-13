package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Page<Bundle> findByBundleGroupVersionsIsAndDescriptorVersionIn(BundleGroupVersion bundleGroupVersions, Collection<DescriptorVersion> descriptorVersion, Pageable pageable);
    Page<Bundle> findByBundleGroupVersionsInAndDescriptorVersionIn(
      List<BundleGroupVersion> bundleGroupVersions, Collection<DescriptorVersion> descriptorVersion, Pageable pageable);
    List<Bundle> findByBundleGroupVersions(BundleGroupVersion bundleGroupVersion, Sort sort);

    List<Bundle> findByBundleGroupVersionsIdAndBundleGroupVersionsBundleGroupPublicCatalogTrue(Long id, Sort sort);

    List<Bundle> findByBundleGroupVersionsBundleGroupCatalogId(Long catalogId);

    List<Bundle> findByBundleGroupVersionsBundleGroupCatalogIdAndBundleGroupVersionsId(Long catalogId, Long id);

    List<Bundle> findByBundleGroupVersionsBundleGroupPublicCatalogTrue();

    List<Bundle> findByBundleGroupVersionsId(Long id, Sort sort);

    List<Bundle> findByBundleGroupVersionsBundleGroupOrganisation(Organisation organisation);


}
