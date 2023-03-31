package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.*;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.service.exception.BadRequestException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.mapper.inclusion.BundleStandardMapper;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BundleService {

    private final BundleRepository bundleRepository;
    private final BundleGroupVersionRepository bundleGroupVersionRepository;
    private final BundleGroupRepository bundleGroupRepository;

    private final SecurityHelperService securityHelperService;

	private final BundleStandardMapper bundleMapper;

    private final CatalogService catalogService;
    private final Logger logger = LoggerFactory.getLogger(BundleService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final PortalUserService portalUserService;

    public BundleService(BundleRepository bundleRepository, BundleGroupVersionRepository bundleGroupVersionRepository,
                         BundleGroupRepository bundleGroupRepository, SecurityHelperService securityHelperService,
                         BundleStandardMapper bundleMapper, PortalUserService portalUserService,
                         CatalogService catalogService) {
        this.bundleRepository = bundleRepository;
        this.bundleGroupVersionRepository = bundleGroupVersionRepository;
        this.bundleGroupRepository = bundleGroupRepository;
        this.securityHelperService = securityHelperService;
        this.bundleMapper = bundleMapper;
        this.portalUserService = portalUserService;
        this.catalogService = catalogService;
    }

    public Page<Bundle> getBundles(String apiKey, Integer pageNum, Integer pageSize, Optional<String> bundleGroupId, Set<DescriptorVersion> descriptorVersions) {

        logger.debug("{}: getBundles: Get bundles paginated by bundle group  id: {}, descriptorVersions: {}", CLASS_NAME, bundleGroupId, descriptorVersions);
        Pageable paging;
        if (pageSize == 0) {
            paging = Pageable.unpaged();
        } else {
            paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        }
        //Controllers can override but default to all versions otherwise.
        if (descriptorVersions == null) {
            descriptorVersions = new HashSet<>();
            Collections.addAll(descriptorVersions, DescriptorVersion.values());
        }
        Page<Bundle> response = new PageImpl<>(new ArrayList<>());
        Catalog userCatalog = null;
        if (null != apiKey) {
            userCatalog = catalogService.getCatalogByApiKey(apiKey);
        }
        if (bundleGroupId.isPresent()) {
            Long bundleGroupEntityId = Long.parseLong(bundleGroupId.get());
            Optional<BundleGroup> bundleGroupEntity = bundleGroupRepository.findById(bundleGroupEntityId);
            if (bundleGroupEntity.isPresent()) {
                 if (null!=apiKey
                        && !Objects.equals(bundleGroupEntity.get().getCatalogId(), userCatalog.getId())) {
                     throw new BadRequestException("Invalid api key and bundleGroupId");
                }
                BundleGroupVersion publishedVersion = bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroupEntity.get(), BundleGroupVersion.Status.PUBLISHED);
                Boolean isPublicCatalog= false;
                 if (publishedVersion != null) {
                    isPublicCatalog = publishedVersion.getBundleGroup().getPublicCatalog();
                }
                if ((Boolean.TRUE.equals(isPublicCatalog))
                    || (null!=userCatalog && Objects.equals(bundleGroupEntity.get().getCatalogId(), userCatalog.getId()))) {
                     response = bundleRepository.findByBundleGroupVersionsIsAndDescriptorVersionIn(
                            publishedVersion, descriptorVersions, paging);
                } else {
                    throw new NotFoundException("Bundle Group " +bundleGroupEntityId+ " not found");
                }
            } else {
                throw new NotFoundException("Bundle Group " +bundleGroupEntityId+ " not found");
            }
        } else {
            if (null != apiKey){
                response = bundleRepository.getPrivateCatalogBundlesPublished(userCatalog.getId(),descriptorVersions, paging);
            } else {
                logger.debug("{}: getBundles: bundle group id is not present: {}, descriptorVersion: {}", CLASS_NAME, bundleGroupId, descriptorVersions);
                List<BundleGroupVersion> bundleGroupsVersion = bundleGroupVersionRepository.getPublicCatalogPublishedBundleGroups(descriptorVersions);
                response = bundleRepository.findByBundleGroupVersionsInAndDescriptorVersionIn(bundleGroupsVersion, descriptorVersions, paging);
            }
        }
        return response;
    }

    public Page<Bundle> getBundles(Integer pageNum, Integer pageSize, Optional<String> bundleGroupId, Set<DescriptorVersion> descriptorVersions) {
        return getBundles(null, pageNum, pageSize, bundleGroupId,  descriptorVersions);
    }
    public List<Bundle> getBundles() {
        return bundleRepository.findAll();
    }
    public List<Bundle> getPublicBundles() {
        return bundleRepository.findByBundleGroupVersionsBundleGroupPublicCatalogTrue();
    }

    public List<Bundle> getBundles(Optional<String> bundleGroupVersionId) {
        logger.debug("{}: getBundles: Get Bundles by bundle group version id: {}", CLASS_NAME, bundleGroupVersionId);
        if (securityHelperService.isAdmin()) {
			if (bundleGroupVersionId.isPresent()) {
				Long bundleGroupVersionEntityId = Long.parseLong(bundleGroupVersionId.get());
				return bundleRepository.findByBundleGroupVersionsId(bundleGroupVersionEntityId, Sort.by("id"));
			}
			return bundleRepository.findAll();
		}
		else{
            if (bundleGroupVersionId.isPresent()) {
        		Long bundleGroupVersionEntityId = Long.parseLong(bundleGroupVersionId.get());
                return bundleRepository.findByBundleGroupVersionsId(bundleGroupVersionEntityId, Sort.by("id"));
			}
            return this.getPublicBundles();
		}
    }

    public List<Bundle> getBundlesByCatalogId(Long catalogId) {
        return bundleRepository.findByBundleGroupVersionsBundleGroupCatalogId(catalogId);
    }

    public List<Bundle> getBundlesByCatalogIdAndBundleGroupVersionId(Long catalogId, Long bundleGroupVersionId) {
        return bundleRepository.findByBundleGroupVersionsBundleGroupCatalogIdAndBundleGroupVersionsId(catalogId, bundleGroupVersionId);
    }

    public Optional<Bundle> getBundle(String bundleId) {
        logger.debug("{}: getBundle: Get a Bundle by bundle id: {}", CLASS_NAME, bundleId);
        return bundleRepository.findById(Long.parseLong(bundleId));
    }

    public Bundle createBundle(Bundle toSave) {
        logger.debug("{}: createBundle: Create a Bundle: {}", CLASS_NAME, toSave);
        return bundleRepository.save(toSave);
    }

    public void deleteBundle(Bundle toDelete) {
        logger.debug("{}: deleteBundle: Delete a Bundle: {}", CLASS_NAME, toDelete);
        deleteFromBundleGroupVersion(toDelete);
        bundleRepository.delete(toDelete);
    }

    public void deleteFromBundleGroupVersion(Bundle bundle) {

    }

    /**
     * Save list of bundles
     *
     * @param bundles
     * @return list of saved bundles
     */
    public List<Bundle> createBundles(List<Bundle> bundles) {
        logger.debug("{}: createBundles: Create bundles: {}", CLASS_NAME, bundles);
        return bundleRepository.saveAll(bundles);
    }

    /**
     * Convert list of bundle request into list of Bundle entity.
     *
     * @param bundleRequest
     * @return list of saved bundles or empty list
     */
    public List<Bundle> createBundleEntitiesAndSave(List<BundleDto> bundleRequest) {
        logger.debug("{}: createBundleEntitiesAndSave: Create bundles: {}", CLASS_NAME, bundleRequest);
        try {
            List<Bundle> bundles = new ArrayList<Bundle>();
            if (!CollectionUtils.isEmpty(bundleRequest)) {
                bundleRequest.forEach(element -> bundles.add(bundleMapper.toEntity(element)));
                return createBundles(bundles);
            }
        } catch (Exception e) {
            logger.debug("{}: createBundleEntitiesAndSave: Error {} {}", CLASS_NAME, e.getMessage(), e.getStackTrace());
        }
        return Collections.emptyList();
    }

    public List<Bundle> getBundles(String bundleGroupVersionId, Long catalogId) {
        List<Bundle> bundles;
        Boolean isUserAuthenticated = securityHelperService.isUserAuthenticated();
        if (Boolean.TRUE.equals(isUserAuthenticated)) {
            if (null == bundleGroupVersionId && null == catalogId) {
                if (securityHelperService.isAdmin()) {
                    return this.getBundles();
                }
                bundles = this.getPublicBundles();
                bundles.addAll(this.getBundlesByAuthenticatedUserOrganizations());
                return bundles.stream().distinct().collect(Collectors.toList());
            }

            if (null != catalogId) {
                if (null != bundleGroupVersionId) {
                    bundles = this.getBundlesByCatalogIdAndBundleGroupVersionId(catalogId, Long.valueOf(bundleGroupVersionId));
                } else {
                    bundles = this.getBundlesByCatalogId(catalogId);
                }
            } else {
                bundles = this.getBundles(Optional.of(bundleGroupVersionId));
            }
        } else {
            bundles = this.getBundles(Optional.ofNullable(bundleGroupVersionId));
        }
        return bundles;
    }

    public List<Bundle> getBundlesByAuthenticatedUserOrganizations() {
        Set<Organisation> userOrganizations = portalUserService.getAuthenticatedUserOrganizations();
        List<Bundle> bundles = new ArrayList<>();
        userOrganizations.forEach (organisation -> {
            List<Bundle> organisationBundles = bundleRepository.findByBundleGroupVersionsBundleGroupOrganisation(organisation);
            bundles.addAll(organisationBundles);
        });
        return bundles;
    }

}
