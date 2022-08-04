package com.entando.hub.catalog.persistence;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Page<Bundle> findByBundleGroupVersionsIsAndDescriptorVersionIn(BundleGroupVersion bundleGroupVersions, Collection<Bundle.DescriptorVersion> descriptorVersion, Pageable pageable);
    Page<Bundle> findByBundleGroupVersionsInAndDescriptorVersionIn(
            List<BundleGroupVersion> bundleGroupVersions, Collection<Bundle.DescriptorVersion> descriptorVersion, Pageable pageable);
    List<Bundle> findByBundleGroupVersions(BundleGroupVersion bundleGroupVersion, Sort sort);
}
