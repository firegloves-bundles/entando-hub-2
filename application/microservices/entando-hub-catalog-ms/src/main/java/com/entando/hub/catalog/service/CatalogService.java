package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CatalogService {
    private final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;

    }
    public List<Catalog> getCatalogues() {
        return catalogRepository.findAll();
    }
    public <T> Optional<Catalog> getCatalogById(T id) {
        if (id instanceof Long) {
            return catalogRepository.findById((Long) id);
        } else if (id instanceof String) {
            return catalogRepository.findById(Long.parseLong((String) id));
        } else {
            throw new IllegalArgumentException("The provided catalogId must be either a Long or a String.");
        }
    }

    public Catalog createCatalog(Catalog toSave) {
        return catalogRepository.save(toSave);
    }

    public Optional<Catalog> getCatalogByOrganisationId(Long id){ return catalogRepository.findByOrganisationId(id);}

    public void deleteCatalog(String catalogId) {
        catalogRepository.deleteById(Long.valueOf(catalogId));
    }

}
