package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    List<Catalog> findByOrganisation_PortalUsers_Username(String username);

    List<Catalog> findAll();

    boolean existsByOrganisationId(Long organisationId);

    Catalog findByOrganisationId(Long organisationId);

}
