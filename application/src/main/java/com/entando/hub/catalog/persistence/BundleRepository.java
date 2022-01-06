package com.entando.hub.catalog.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Page<Bundle> findByBundleGroupVersionsIs(BundleGroupVersion bundleGroupVersions, Pageable pageable);
    Page<Bundle> findByBundleGroupVersionsIn(List<BundleGroupVersion> bundleGroupVersions, Pageable pageable);
    List<Bundle> findByBundleGroupVersionsIs(BundleGroupVersion bundleGroupVersions);
}
