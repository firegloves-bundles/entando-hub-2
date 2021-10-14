package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    Page<Bundle> findByBundleGroupsIs(BundleGroup bundleGroups, Pageable pageable);
    Page<Bundle> findByBundleGroupsIsNotNull(Pageable pageable);
    List<Bundle> findByBundleGroupsIs(BundleGroup bundleGroups);
}
