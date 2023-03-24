package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {
    
    List<PortalUser> findByOrganisationsIs(Organisation organisation);

    PortalUser findByUsername(String username);
    
}
