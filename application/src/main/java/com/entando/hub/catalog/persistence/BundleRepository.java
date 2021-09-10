package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    List<Bundle> findByBundleGroupsIs(BundleGroup bundleGroups);
}
