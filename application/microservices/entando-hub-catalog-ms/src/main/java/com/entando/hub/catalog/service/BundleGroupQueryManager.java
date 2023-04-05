package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroup_;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.SetJoin;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

public class BundleGroupQueryManager {
    public static Specification<BundleGroup> hasCatalogId(Long catalogId){
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.equal(root.get(BundleGroup_.CATALOG_ID), catalogId);
        };
    }
    public static Specification<BundleGroup> hasOrganisationId(Long organisationId){
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.equal(root.get(BundleGroup_.organisation).get(Organisation_.ID), organisationId);
        };
    }

    public static Specification<BundleGroup> isInPublicCatalog(boolean publicCatalog){
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.equal(root.get(BundleGroup_.PUBLIC_CATALOG), publicCatalog);
        };
    }

    public static Specification<BundleGroup> belongsToCategories(Set<Category> categories) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            SetJoin<BundleGroup, Category> join = root.joinSet(BundleGroup_.CATEGORIES);
            return join.in(categories.stream().map(Category::getId).collect(Collectors.toSet()));
        };
    }

    public static Specification<BundleGroup> getSpecificationFromFilters(List<Specification<BundleGroup>> filter) {
        Specification<BundleGroup> specification = where(filter.remove(0));
        for (Specification<BundleGroup> input : filter) {
            specification = specification.and(input);
        }
        return specification;
    }

}
