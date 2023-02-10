package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.service.dto.CatalogDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.entando.hub.catalog.service.OrganisationService;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CatalogService {
    private final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final CatalogRepository catalogRepository;
    private final OrganisationService organisationService;

    public CatalogService(CatalogRepository catalogRepository, OrganisationService organisationService) {
        this.catalogRepository = catalogRepository;
        this.organisationService = organisationService;
    }
    public List<CatalogDTO> getCatalogs() {
        List<Catalog> catalogs = catalogRepository.findAll();
        return catalogs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ResponseEntity<CatalogDTO> getCatalogById(Long id) {
        Optional<Catalog> catalog = catalogRepository.findById(id);
        logger.debug("{} catalog details: {} ", CLASS_NAME, catalog);
        if (catalog.isPresent()){
            return new ResponseEntity<>(this.mapToDTO(catalog.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CatalogDTO> createCatalog(Long organisationId) {

        Optional<Organisation> organisation = organisationService.getOrganisation(organisationId);

        logger.debug("{} organisation details: {} ", CLASS_NAME, organisation.toString());

        if (organisation.isPresent()) {
            if(this.catalogRepository.existsByOrganisationId(organisationId)) {
                logger.debug("{} catalog already exists", CLASS_NAME);
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

            Catalog entity = new Catalog();
            if(organisation.isPresent()) {
                entity.setOrganisation(organisation.get());
            }
            entity.setName(organisation.get().getName() + " private catalog");


            Catalog response = catalogRepository.save(entity);
            return new ResponseEntity<>(this.mapToDTO(response), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<CatalogDTO> deleteCatalog(Long catalogId) {
        Optional<CatalogDTO> catalog = Optional.ofNullable(this.getCatalogById(catalogId).getBody());
        if (catalog.isPresent()) {
            logger.debug("{} deleting catalog: {}", CLASS_NAME, catalogId);
            catalogRepository.deleteById(catalogId);
            return new ResponseEntity<>(catalog.get(), HttpStatus.OK);
        } else {
            logger.debug("{} catalog {} not found, delete failed", CLASS_NAME, catalogId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    public CatalogDTO mapToDTO(Catalog catalog) {
        return new CatalogDTO(catalog.getId(), catalog.getOrganisation().getId(), catalog.getName());
    }

}
