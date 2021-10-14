package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface BundleGroupRepository extends JpaRepository<BundleGroup, Long> {
    List<BundleGroup> findByOrganisationId(Long organisationId);
    Page<BundleGroup> findByOrganisationId(Long organisationId, Pageable pageable);


    Page<BundleGroup> findDistinctByCategoriesInAndStatusIn(Set<Category> categories, Set<BundleGroup.Status> statuses, Pageable pageable);

    Page<BundleGroup> findDistinctByOrganisationAndCategoriesInAndStatusIn(Organisation organisation, Set<Category> categories, Set<BundleGroup.Status> statuses, Pageable pageable);
    Page<BundleGroup> findDistinctByOrganisationAndStatusIn(Organisation organisation, Set<Category> categories, Set<BundleGroup.Status> statuses, Pageable pageable);
}
