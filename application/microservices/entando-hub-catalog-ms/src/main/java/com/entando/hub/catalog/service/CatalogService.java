package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {
    private final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final CatalogRepository catalogRepository;
    private final OrganisationService organisationService;
    private final PrivateCatalogApiKeyService privateCatalogApiKeyService;
    public CatalogService(CatalogRepository catalogRepository, OrganisationService organisationService,
                          PrivateCatalogApiKeyService privateCatalogApiKeyService) {
        this.catalogRepository = catalogRepository;
        this.organisationService = organisationService;
        this.privateCatalogApiKeyService = privateCatalogApiKeyService;
    }
    public List<Catalog> getCatalogs(String username, boolean userIsAdmin) {
        if (userIsAdmin) {
            return catalogRepository.findAll();
        } else {
            return catalogRepository.findByOrganisation_PortalUsers_Username(username);
        }
    }

    public Catalog getCatalogByApiKey(String apiKey){
        String username = privateCatalogApiKeyService.getUserByApiKey(apiKey);
        List<Catalog> catalogs = this.getCatalogs(username, false);
        return catalogs.stream().findFirst().orElseThrow(()-> new NotFoundException("Private catalog not found"));
    }

    public Catalog getCatalogById(String username, Long id, boolean userIsAdmin) {
        Optional<Catalog> catalog;
        if (userIsAdmin) {
           catalog = catalogRepository.findById(id);
        } else {
            catalog = catalogRepository.findByOrganisation_PortalUsers_UsernameAndId(username, id);
        }

        logger.debug("{} catalog details: {} ", CLASS_NAME, catalog);
        return catalog.orElseThrow(() -> new NotFoundException("Catalog not found"));
    }

    public Catalog createCatalog(Long organisationId) throws NotFoundException, ConflictException {

        Optional<Organisation> organisation = organisationService.getOrganisation(organisationId);

        logger.debug("{} organisation details: {} ", CLASS_NAME, organisation.toString());

        if (organisation.isPresent()) {
            if(this.catalogRepository.existsByOrganisationId(organisationId)) {
                throw new ConflictException("Catalog already exists");
            }

            Catalog entity = new Catalog()
                    .setOrganisation(organisation.get())
                    .setName(organisation.get().getName() + " private catalog");
            return catalogRepository.save(entity);

        } else {
            throw new NotFoundException("Organisation not found");
        }

    }

    public Catalog deleteCatalog(Long catalogId) throws NotFoundException {
        Optional<Catalog> catalog = catalogRepository.findById(catalogId);
        if (catalog.isPresent()) {
            logger.debug("{} deleting catalog: {}", CLASS_NAME, catalogId);
            catalogRepository.deleteById(catalogId);
            return catalog.get();
        } else {
            logger.debug("{} catalog {} not found, delete failed", CLASS_NAME, catalogId);
            throw new NotFoundException("Catalog not found");
        }
    }

    public Boolean existCatalogById(Long catalogId) {
        return this.catalogRepository.existsById(catalogId);
    }

}
