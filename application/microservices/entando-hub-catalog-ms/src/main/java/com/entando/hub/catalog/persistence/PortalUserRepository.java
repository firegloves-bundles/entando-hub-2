package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.PortalUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {
    PortalUser findByUsername(String username);
    
}
