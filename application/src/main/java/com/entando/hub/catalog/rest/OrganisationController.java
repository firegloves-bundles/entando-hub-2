package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.service.OrganisationService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/organisation")
public class OrganisationController {
    
    private final Logger logger = LoggerFactory.getLogger(OrganisationController.class);

    private final OrganisationService organisationService;

    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    //@RolesAllowed("codemotion-bff-admin")
    //@PreAuthorize("hasAuthority('ROLE_mf-widget-admin')")
    @CrossOrigin
    @GetMapping("/")
    public List<Organisation> getOrganisations() {
        logger.debug("REST request to get organisations");
        return organisationService.getOrganisations().stream().map(Organisation::new).collect(Collectors.toList());
    }

    @CrossOrigin
    @GetMapping("/{organisationId}")
    public ResponseEntity<Organisation> getOrganisation(@PathVariable String organisationId) {
        logger.debug("REST request to get organisation by id: {}", organisationId);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (organisationOptional.isPresent()) {
            return new ResponseEntity<>(organisationOptional.map(Organisation::new).get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PostMapping("/")
    public ResponseEntity<Organisation> createOrganisation(@RequestBody OrganisationNoId organisation) {
        logger.debug("REST request to create new organisation: {}", organisation);
        com.entando.hub.catalog.persistence.entity.Organisation entity = organisationService.createOrganisation(organisation.createEntity(Optional.empty()), organisation);
        return new ResponseEntity<>(new Organisation(entity), HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping("/{organisationId}")
    public ResponseEntity<Organisation> updateOrganisation(@PathVariable String organisationId, @RequestBody OrganisationNoId organisation) {
        logger.debug("REST request to update organisation {}: {}", organisationId, organisation);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (!organisationOptional.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            //com.entando.hub.catalog.persistence.entity.Organisation storedEntity = organisationOptional.get();
            com.entando.hub.catalog.persistence.entity.Organisation entity = organisationService.createOrganisation(organisation.createEntity(Optional.of(organisationId)), organisation);
            return new ResponseEntity<>(new Organisation(entity), HttpStatus.OK);
        }
    }


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class Organisation extends OrganisationNoId {
        private final String organisationId;

        public Organisation(com.entando.hub.catalog.persistence.entity.Organisation entity) {
            super(entity);
            this.organisationId = entity.getId().toString();
        }

        public Organisation(String organisationId, String name, String description) {
            super(name, description);
            this.organisationId = organisationId;
        }
    }


    @Data
    public static class OrganisationNoId {
        protected final String name;
        protected final String description;
        protected List<String> bundleGroups;

        public OrganisationNoId(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public OrganisationNoId(com.entando.hub.catalog.persistence.entity.Organisation entity) {
            this.name = entity.getName();
            this.description = entity.getDescription();
            if (entity.getBundleGroups() != null) {
                this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());
            }
        }


        public com.entando.hub.catalog.persistence.entity.Organisation createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.Organisation ret = new com.entando.hub.catalog.persistence.entity.Organisation();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }

    }

}
