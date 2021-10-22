package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/bundlegroups")
public class BundleGroupController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupService bundleGroupService;
    private final CategoryService categoryService;
    private final SecurityHelperService securityHelperService;

    public BundleGroupController(BundleGroupService bundleGroupService, CategoryService categoryService, SecurityHelperService securityHelperService) {
        this.bundleGroupService = bundleGroupService;
        this.categoryService = categoryService;
        this.securityHelperService = securityHelperService;
    }


    //PUBLIC
    @CrossOrigin
    @GetMapping("/")
    public List<BundleGroup> getBundleGroups(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get BundleGroups by organisation Id: {}", organisationId);
        return bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId)).stream().map(BundleGroup::new).collect(Collectors.toList());
    }


    //PUBLIC
    @CrossOrigin
    @GetMapping("/filtered")
    public PagedContent<BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses) {
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        String[] categoryIdFilterValues = categoryIds;
        if (categoryIdFilterValues == null) {
            categoryIdFilterValues = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        String[] statusFilterValues = statuses;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroup.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("REST request to get BundleGroups by organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Page<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupsPage = bundleGroupService.getBundleGroups(sanitizedPageNum, pageSize, Optional.ofNullable(organisationId), categoryIdFilterValues, statuses);
        PagedContent<BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> pagedContent = new PagedContent<>(bundleGroupsPage.getContent().stream().map(BundleGroup::new).collect(Collectors.toList()), bundleGroupsPage);
        return pagedContent;
    }

    //PUBLIC
    @CrossOrigin
    @GetMapping("/{bundleGroupId}")
    public ResponseEntity<BundleGroup> getBundleGroup(@PathVariable String bundleGroupId) {
        logger.debug("REST request to get BundleGroup by Id: {}", bundleGroupId);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
            return new ResponseEntity<>(bundleGroupOptional.map(BundleGroup::new).get(), HttpStatus.OK);
        } else {
            logger.warn("Requested bundleGroup '{}' does not exists", bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @PostMapping("/")
    public ResponseEntity<BundleGroup> createBundleGroup(@RequestBody BundleGroupNoId bundleGroup) {
        logger.debug("REST request to create BundleGroup: {}", bundleGroup);
        //if not admin the organisationid of the bundle must be the same of the user
        if (securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroup.getOrganisationId())) {
            logger.warn("Only {} users can create bundle groups for any organisation, the other ones can create bundle groups only for their organisation", ADMIN);
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.empty()), bundleGroup);
        return new ResponseEntity<>(new BundleGroup(saved), HttpStatus.CREATED);
    }

    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @PostMapping("/{bundleGroupId}")
    public ResponseEntity<BundleGroup> updateBundleGroup(@PathVariable String bundleGroupId, @RequestBody BundleGroupNoId bundleGroup) {
        logger.debug("REST request to update BundleGroup with id {}: {}", bundleGroupId, bundleGroup);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (!bundleGroupOptional.isPresent()) {
            logger.warn("BundleGroup '{}' does not exists", bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            //if the user is not ADMIN
            if (!securityHelperService.hasRoles(Set.of(ADMIN))) {
                //I'm going to check the organisation
                com.entando.hub.catalog.persistence.entity.BundleGroup bundleGroupEntity = bundleGroupOptional.get();

                //must exist and the user mat be in it
                if (bundleGroupEntity.getOrganisation() == null || !securityHelperService.userIsInTheOrganisation(bundleGroupEntity.getOrganisation().getId())) {
                    logger.warn("Only {} users can update bundle groups for any organisation, the other ones can update bundle groups only for their organisation", ADMIN);
                    return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
                }
            }
            com.entando.hub.catalog.persistence.entity.BundleGroup saved = bundleGroupService.createBundleGroup(bundleGroup.createEntity(Optional.of(bundleGroupId)), bundleGroup);
            return new ResponseEntity<>(new BundleGroup(saved), HttpStatus.OK);
        }
    }


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class BundleGroup extends BundleGroupNoId {
        private final String bundleGroupId;

        public BundleGroup(String bundleGroupId, String name, String description, String descriptionImage, String version) {
            super(name, description, descriptionImage, version);
            this.bundleGroupId = bundleGroupId;
        }

        public BundleGroup(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
            super(entity);
            this.bundleGroupId = entity.getId().toString();
        }
    }

    @Data
    public static class BundleGroupNoId {
        protected final String name;
        protected final String description;
        protected final String descriptionImage;
        protected String documentationUrl;
        protected String version;
        protected com.entando.hub.catalog.persistence.entity.BundleGroup.Status status;
        protected LocalDateTime lastUpdate;

        //the following must be merged with the entity using mappedBy
        protected List<String> children;
        protected String organisationId;
        protected List<String> categories;


        public BundleGroupNoId(String name, String description, String descriptionImage, String version) {
            this.name = name;
            this.description = description;
            this.descriptionImage = descriptionImage;
            this.version = version;
        }


        public BundleGroupNoId(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
            this.description = entity.getDescription();
            this.descriptionImage = entity.getDescriptionImage();
            this.name = entity.getName();
            this.status = entity.getStatus();
            this.documentationUrl = entity.getDocumentationUrl();
            this.version = entity.getVersion();
            this.lastUpdate = entity.getLastUpdate();

            if (entity.getOrganisation() != null) {
                this.organisationId = entity.getOrganisation().getId().toString();
            }
            //todo one single iteration
            if (entity.getBundles() != null) {
                this.children = entity.getBundles().stream().map((children) -> children.getId().toString()).collect(Collectors.toList());
            }
            if (entity.getCategories() != null) {
                this.categories = entity.getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
            }
        }

        public com.entando.hub.catalog.persistence.entity.BundleGroup createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.BundleGroup ret = new com.entando.hub.catalog.persistence.entity.BundleGroup();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            ret.setDescriptionImage(this.getDescriptionImage());
            ret.setDocumentationUrl(this.getDocumentationUrl());
            ret.setStatus(this.getStatus());
            ret.setVersion(this.getVersion());
            if (this.organisationId != null) {
                Organisation organisation = new Organisation();
                organisation.setId(Long.parseLong(this.organisationId));
                ret.setOrganisation(organisation);
            }
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }
    }


}
