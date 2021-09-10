package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.OrganisationController;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class OrganisationService {
    final private OrganisationRepository organisationRepository;
    final private BundleGroupRepository bundleGroupRepository;

    public OrganisationService(OrganisationRepository organisationRepository, BundleGroupRepository bundleGroupRepository) {
        this.organisationRepository = organisationRepository;
        this.bundleGroupRepository = bundleGroupRepository;
    }

    /**
     * Update the Organisation entity bundleGroups collection
     * with the mappedBy organisation field in every bundleGroup
     *
     * @param toUpdate
     * @param organisation
     */
    public void updateMappedBy(com.entando.hub.catalog.persistence.entity.Organisation toUpdate, OrganisationController.OrganisationNoId organisation) {
        Objects.requireNonNull(toUpdate.getId());
        if (organisation.getBundleGroups() != null) {
            //TODO replace with native query
            //delete all old connections
            bundleGroupRepository.findByOrganisationId(toUpdate.getId()).stream().forEach(bundleGroup -> {
                bundleGroup.setOrganisation(null); //this is the mappedBy field
                bundleGroupRepository.save(bundleGroup);
            });
            Set<BundleGroup> newBundleGroups = organisation.getBundleGroups().stream().map(bundleGroupId -> {
                BundleGroup bundleGroup = bundleGroupRepository.findById(Long.valueOf(bundleGroupId)).get();
                bundleGroup.setOrganisation(toUpdate);
                bundleGroupRepository.save(bundleGroup);
                return bundleGroup;
            }).collect(Collectors.toSet());
            toUpdate.setBundleGroups(newBundleGroups);
        }
    }

    public List<Organisation> getOrganisations() {
        return organisationRepository.findAll();
    }

    public Optional<Organisation> getOrganisation(String organisationId) {
        return organisationRepository.findById(Long.parseLong(organisationId));
    }

    @Transactional
    public Organisation createOrganisation(Organisation organisationEntity, OrganisationController.OrganisationNoId organisation) {
        Organisation entity = organisationRepository.save(organisationEntity);
        updateMappedBy(entity, organisation);
        return entity;
    }
}

