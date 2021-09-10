package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
}
