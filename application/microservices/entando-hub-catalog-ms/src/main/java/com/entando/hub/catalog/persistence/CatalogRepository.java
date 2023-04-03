package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    @Query("select c from Catalog c inner join c.organisation.portalUsers portalUsers " +
            "where portalUsers.username = :username")
    List<Catalog> findByUsername(@Param("username") String username);


    Optional<Catalog> findByOrganisation_PortalUsers_UsernameAndId(String username, Long id);

    List<Catalog> findAll();

    boolean existsByOrganisationId(Long organisationId);

    Catalog findByOrganisationId(Long organisationId);


}
