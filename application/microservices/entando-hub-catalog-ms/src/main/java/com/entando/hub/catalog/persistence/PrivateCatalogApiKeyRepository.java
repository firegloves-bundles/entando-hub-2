package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface PrivateCatalogApiKeyRepository extends JpaRepository<PrivateCatalogApiKey, Long> {

    @Query("select p from PrivateCatalogApiKey p where p.portalUser.username = :username")
    Page<PrivateCatalogApiKey> getPrivateCatalogApiKeys(@Param("username") String username, Pageable pageable);

    @Query("select p from PrivateCatalogApiKey p where p.id = :id and p.portalUser.username = :username")
    Optional<PrivateCatalogApiKey> getPrivateCatalogApiKey(@Param("id") long id, @Param("username") String username);
}
