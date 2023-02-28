package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PrivateCatalogApiKeyRepository extends JpaRepository<PrivateCatalogApiKey, Long> {
    Page<PrivateCatalogApiKey> findByPortalUserUsername(Pageable paging, String username);

    Optional<PrivateCatalogApiKey> findByIdAndPortalUserUsername(long id, String username);
}
