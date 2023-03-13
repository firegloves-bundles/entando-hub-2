package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    List<Catalog> findByOrganisation_PortalUsers_Username(String username);


    Optional<Catalog> findByOrganisation_PortalUsers_UsernameAndId(String username, Long id);

    List<Catalog> findAll();

    boolean existsByOrganisationId(Long organisationId);

    Catalog findByOrganisationId(Long organisationId);

}
