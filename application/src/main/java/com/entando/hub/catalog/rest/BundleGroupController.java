package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CategoryService;
import com.entando.hub.catalog.service.security.SecurityHelperService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@RestController
@RequestMapping("/api/bundlegroups")
public class BundleGroupController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupController.class);

    private final BundleGroupService bundleGroupService;
    private final CategoryService categoryService;
    private final SecurityHelperService securityHelperService;
    private final BundleGroupVersionService bundleGroupVersionService;

    public BundleGroupController(BundleGroupService bundleGroupService, CategoryService categoryService, SecurityHelperService securityHelperService, BundleGroupVersionService bundleGroupVersionService) {
        this.bundleGroupService = bundleGroupService;
        this.categoryService = categoryService;
        this.securityHelperService = securityHelperService;
        this.bundleGroupVersionService = bundleGroupVersionService;
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle groups in the hub", description = "Public api, no authentication required. You can provide the organisationId.")
    @CrossOrigin
    @GetMapping("/")
    public List<BundleGroup> getBundleGroupsByOrgnisationId(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get BundleGroups by organisation Id: {}", organisationId);
        return bundleGroupService.getBundleGroups(Optional.ofNullable(organisationId)).stream().map(BundleGroup::new).collect(Collectors.toList());
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle groups in the hub", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @CrossOrigin
    @GetMapping("/filtered")
    public PagedContent<BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses) {
    	logger.debug("REST request to get BundleGroups by organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        String[] categoryIdFilterValues = categoryIds;
        if (categoryIdFilterValues == null) {
            categoryIdFilterValues = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        String[] statusFilterValues = statuses;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("Organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Page<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupsPage = bundleGroupService.getBundleGroups(sanitizedPageNum, pageSize, Optional.ofNullable(organisationId), categoryIdFilterValues, statuses);
        PagedContent<BundleGroup, com.entando.hub.catalog.persistence.entity.BundleGroup> pagedContent = new PagedContent<>(bundleGroupsPage.getContent().stream().map(BundleGroup::new).collect(Collectors.toList()), bundleGroupsPage);
        return pagedContent;
    }

    //PUBLIC
    @Operation(summary = "Get the bundleGroup details", description = "Public api, no authentication required. You have to provide the bundleGroupId")
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

    @Operation(summary = "Create a new bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
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

    @Operation(summary = "Update a bundleGroup", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupId identifying the bundleGroup")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @PostMapping("/{bundleGroupId}")
    public ResponseEntity<BundleGroup> updateBundleGroup(@PathVariable String bundleGroupId, @RequestBody BundleGroupNoId bundleGroup) {
        logger.debug("REST request to update BundleGroup with id {}: {}", bundleGroupId, bundleGroup);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);

		if (!bundleGroupOptional.isPresent()) {
			logger.warn("BundleGroup '{}' does not exists", bundleGroupId);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else if (!bundleGroupVersionService.isBundleGroupEditable(bundleGroupOptional.get())) {
			logger.warn("BundleGroup '{}' is not editable", bundleGroupId);
			return new ResponseEntity<>(null, HttpStatus.CONFLICT);
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

    @Operation(summary = "Delete a bundleGroup", description = "Protected api, only eh-admin and eh-manager can access it. A bundleGroup can be deleted only if it is in DELETE_REQ status  You have to provide the bundlegroupId identifying the category")
    @RolesAllowed({ADMIN, MANAGER})
    @CrossOrigin
    @DeleteMapping("/{bundleGroupId}")
    @Transactional
    public ResponseEntity<CategoryController.Category> deleteBundleGroup(@PathVariable String bundleGroupId) {
        logger.debug("REST request to delete bundleGroup {}", bundleGroupId);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (!bundleGroupOptional.isPresent()) {
            bundleGroupOptional.ifPresentOrElse(
                    bundleGroup -> logger.warn("Requested bundleGroup '{}' is not present", bundleGroupId),
                    () -> logger.warn("Requested bundleGroup '{}' does not exists", bundleGroupId)
            );
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            bundleGroupService.deleteBundleGroup(bundleGroupId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class BundleGroup extends BundleGroupNoId {
        private final String bundleGroupId;

        public BundleGroup(String bundleGroupId, String name, String organizationId) {
            super(name, organizationId);
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
        protected String organisationId;
        protected String organisationName;
        protected List<String> categories;
        protected BundleGroupVersionView versionDetails;

        public BundleGroupNoId(String name ,String organisationId) {
            this.name = name;
            this.organisationId = organisationId;
        }

        public BundleGroupNoId(com.entando.hub.catalog.persistence.entity.BundleGroup entity) {
            this.name = entity.getName();

            if (entity.getOrganisation() != null) {
                this.organisationId = entity.getOrganisation().getId().toString();
                this.organisationName = entity.getOrganisation().getName();
            }
            if (entity.getCategories() != null) {
                this.categories = entity.getCategories().stream().map((category) -> category.getId().toString()).collect(Collectors.toList());
            }
        }

        public com.entando.hub.catalog.persistence.entity.BundleGroup createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.BundleGroup ret = new com.entando.hub.catalog.persistence.entity.BundleGroup();
            ret.setName(this.getName());
            if (this.organisationId != null) {
                Organisation organisation = new Organisation();
                organisation.setId(Long.parseLong(this.organisationId));
                organisation.setName(this.organisationName);
                ret.setOrganisation(organisation);
            }
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }
    }
}
