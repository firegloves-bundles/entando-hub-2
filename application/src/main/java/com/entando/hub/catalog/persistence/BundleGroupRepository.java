package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface BundleGroupRepository extends JpaRepository<BundleGroup, Long> {
    List<BundleGroup> findByOrganisationId(Long organisationId);
    List<BundleGroup> findByOrganisationIdAndAndCategoriesIn(Long organisationId, Set<Category> categories);
}
