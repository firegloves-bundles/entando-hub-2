package com.entando.hub.catalog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;
import com.entando.hub.catalog.rest.PagedContent;

@Service
public class BundleGroupVersionService {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupVersionService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final BundleGroupVersionRepository bundleGroupVersionRepository;
    final private BundleGroupRepository bundleGroupRepository;
    final private BundleRepository bundleRepository;
    final private CategoryRepository categoryRepository;
    private final BundleService bundleService;

    @Autowired
    private Environment environment;

    public BundleGroupVersionService(BundleGroupVersionRepository bundleGroupVersionRepository, BundleGroupRepository bundleGroupRepository,BundleRepository bundleRepository,CategoryRepository categoryRepository, BundleService bundleService) {
    	this.bundleGroupVersionRepository = bundleGroupVersionRepository;
    	this.bundleGroupRepository = bundleGroupRepository;
    	this.bundleRepository = bundleRepository;
    	this.categoryRepository = categoryRepository;
    	this.bundleService = bundleService;
    }

    public Optional<BundleGroupVersion> getBundleGroupVersion(String bundleGroupVersionId) {
    	logger.debug("{}: getBundleGroupVersion: Get a bundle group version by id: {}", CLASS_NAME, bundleGroupVersionId);
        return bundleGroupVersionRepository.findById(Long.parseLong(bundleGroupVersionId));
    }

    @Transactional
    public BundleGroupVersion createBundleGroupVersion(BundleGroupVersion bundleGroupVersionEntity, BundleGroupVersionView bundleGroupVersionView) {
    	logger.debug("{}: createBundleGroupVersion: Create a bundle group version: {}", CLASS_NAME, bundleGroupVersionView);

    	List<Bundle> savedBundles = bundleService.createBundleEntitiesAndSave(bundleGroupVersionView.getBundles());
    	if(Objects.nonNull(savedBundles)) {
    		List<Long> savedBundleIds = savedBundles.stream().map(c -> c.getId()).collect(Collectors.toList());
    		bundleGroupVersionView.setChildren(savedBundleIds);
    	}

    	if (bundleGroupVersionView.getStatus().equals(BundleGroupVersion.Status.PUBLISHED)) {
    		BundleGroupVersion publishedVersion = bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroupVersionEntity.getBundleGroup(), BundleGroupVersion.Status.PUBLISHED);
			if (publishedVersion != null) {
				logger.debug("{}: createBundleGroupVersion: Published bundle : {}", CLASS_NAME, publishedVersion);
				publishedVersion.setStatus(BundleGroupVersion.Status.ARCHIVE);
				bundleGroupVersionRepository.save(publishedVersion);
			}
    	}

    	bundleGroupVersionEntity.setLastUpdated(LocalDateTime.now());
    	BundleGroupVersion entity = bundleGroupVersionRepository.save(bundleGroupVersionEntity);

		try {
			if (bundleGroupVersionView.getChildren() != null) {
				List<Bundle> mappedBundles = bundleRepository.findByBundleGroupVersionsIs(entity);
				List<Long> mappedBundleIds = mappedBundles.stream().map(e -> e.getId()).collect(Collectors.toList());
				mappedBundles.stream().forEach(bundle -> {
					bundle.getBundleGroupVersions().remove(entity);
					bundleRepository.save(bundle);
				});

				Set<Bundle> bundleSet = bundleGroupVersionView.getChildren().stream().map((bundleChildId) -> {
					com.entando.hub.catalog.persistence.entity.Bundle bundle = bundleRepository.findById(Long.valueOf(bundleChildId)).get();
					bundle.getBundleGroupVersions().add(entity);
					bundleRepository.save(bundle);
					return bundle;
				}).collect(Collectors.toSet());
				entity.setBundles(bundleSet);

//			Remove orphan bundles from database
				mappedBundleIds.forEach((bundleId) -> {
					Optional<Bundle> optBundle = bundleRepository.findById(bundleId);
					optBundle.ifPresent((bundle) -> {
						Set<BundleGroupVersion> bundleGroups = bundle.getBundleGroupVersions();
						if (CollectionUtils.isEmpty(bundleGroups)) {
							bundleRepository.deleteById(bundleId);
							logger.debug("{}: Removed bundle {} from db", CLASS_NAME, bundleId);
						}
					});
				});
				logger.debug("{}: createBundleGroupVersion: Bundles: {}", CLASS_NAME, bundleSet);
			}
		} catch (Exception e) {
			logger.error("{}: createBundleGroupVersion: Error: {}", CLASS_NAME, e.getStackTrace());
		}
    	return entity;
    }

    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> getBundleGroupVersions(Integer pageNum, Integer pageSize, Optional<String> organisationId, String[] categoryIds, String[] statuses) {
    	logger.debug("{}: getBundleGroupVersions: Get bundle group versions paginated by organisation id: {}, categories: {}, statuses: {}", CLASS_NAME, organisationId, categoryIds, statuses);
        Pageable paging;
        if (pageSize == 0) {
            paging = Pageable.unpaged();
        } else {
            Sort.Order order = new Sort.Order(Sort.Direction.DESC, "lastUpdated");
            paging = PageRequest.of(pageNum, pageSize, Sort.by(order));
        }
        Set<Category> categories = Arrays.stream(categoryIds).map(cid -> {
            Category category = new Category();
            category.setId(Long.valueOf(cid));
            return category;
        }).collect(Collectors.toSet());

       Set<BundleGroupVersion.Status> statusSet = Arrays.stream(statuses).map(BundleGroupVersion.Status::valueOf).collect(Collectors.toSet());
        List<BundleGroup> bunleGroups ;
        if (organisationId.isPresent()) {
            Organisation organisation = new Organisation();
            organisation.setId(Long.valueOf(organisationId.get()));
            bunleGroups = bundleGroupRepository.findDistinctByOrganisationAndCategoriesIn(organisation, categories);
        } else {
        	bunleGroups = bundleGroupRepository.findDistinctByCategoriesIn(categories);
        }
        
        Page<BundleGroupVersion> page = bundleGroupVersionRepository.findByBundleGroupInAndStatusIn(bunleGroups, statusSet, paging);  
        PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> pagedContent = new PagedContent<>(toResponseViewList(page).stream()
        	.sorted(Comparator.comparing(BundleGroupVersionFilteredResponseView::getName, String::compareToIgnoreCase))
        	.collect(Collectors.toList()), page);
        logger.debug("{}: getBundleGroupVersions: Number of elements: {}", CLASS_NAME, organisationId, page.getNumberOfElements());
        return pagedContent;
    }

    /**
     * Set bundle group url
     * @param bundleGroupVersionId
     * @return
     */
	private String getBundleGroupUrl(Long bundleGroupVersionId) {
		String hubGroupDeatilUrl = environment.getProperty("HUB_GROUP_DETAIL_BASE_URL");
		logger.debug("{}: getBundleGroupUrl: Get a bundle group hub url from config file: {}, bundle group version id: {}", CLASS_NAME, hubGroupDeatilUrl, bundleGroupVersionId);
		if (Objects.nonNull(hubGroupDeatilUrl)) {
			return hubGroupDeatilUrl + "bundlegroup/versions/" + bundleGroupVersionId;
		}
		return "";
	}
	
	public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion>  getBundleGroupVersions(Integer pageNum, Integer pageSize, String[] statuses, BundleGroup bundleGroup) {
		logger.debug("{}: getBundleGroupVersions: Get bundle group versions paginated by statuses: {} and bundle group: {}", CLASS_NAME, statuses, bundleGroup);
        Pageable paging;
        if (pageSize == 0) {
            paging = Pageable.unpaged();
        } else {
        	   Sort.Order order = new Sort.Order(Sort.Direction.DESC, "lastUpdated");
            paging = PageRequest.of(pageNum, pageSize, Sort.by(order));
        }

        Set<BundleGroupVersion.Status> statusSet = Arrays.stream(statuses).map(BundleGroupVersion.Status::valueOf).collect(Collectors.toSet());
        Page<BundleGroupVersion> page = bundleGroupVersionRepository.findByBundleGroupAndStatusIn(bundleGroup, statusSet, paging);
        logger.debug("{}: getBundleGroupVersions: Found pages, number of elements: {}", CLASS_NAME, page.getNumberOfElements());
        List<BundleGroupVersion> versions = new ArrayList<BundleGroupVersion>();
        versions.addAll(page.getContent().stream().filter(version -> !version.getStatus().equals(BundleGroupVersion.Status.ARCHIVE)).collect(Collectors.toList()));
        versions.addAll(page.getContent().stream().filter(version -> version.getStatus().equals(BundleGroupVersion.Status.ARCHIVE)).collect(Collectors.toList()));
        Page<BundleGroupVersion> pageResponse = new PageImpl<>(versions);

        PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersion> pagedContent = new PagedContent<>(toResponseViewList(pageResponse).stream()
        		.collect(Collectors.toList()), pageResponse);
        return pagedContent;
	 }

	@Transactional
	public void deleteBundleGroupVersion(Optional<BundleGroupVersion> bundleGroupVersionOptional) {
		logger.debug("{}: deleteBundleGroupVersion: Delete a bundle group version: {}", CLASS_NAME, bundleGroupVersionOptional);
		try {
			bundleGroupVersionOptional.ifPresent(bundleGroupVersion -> {
				BundleGroup parentBundleGroup = bundleGroupVersion.getBundleGroup();
				/**
				 * First remove this bundle group version from bundles if mapped, and also delete the orphan bundles.
				 */
				removeBundleGroupVersionFromBundles(bundleGroupVersion);

				/**
				 * Now remove this bundle group version from the parent bundle group and delete it.
				 */
				parentBundleGroup.getVersion().remove(bundleGroupVersion);
				bundleGroupVersionRepository.delete(bundleGroupVersion);

				/**
				 * Delete the parent bundle group if it does not have any other version.
				 */
				if(parentBundleGroup.getVersion().size() == 0) {
					/**
					 * First remove the bundle group from categories.
					 */
					removeBundleGroupFromCategories(parentBundleGroup);
					bundleGroupRepository.delete(parentBundleGroup);
				}
			});
		} catch (Exception e) {
			logger.debug("{}: deleteBundleGroupVersion: Error: {}", CLASS_NAME, e.getStackTrace());
		}
	}

	private void removeBundleGroupFromCategories(BundleGroup bundleGroup) {
		logger.debug("{}: deleteFromCategories: Delete bundle group version from categories", CLASS_NAME);
		bundleGroup.getCategories().forEach((category) -> {
			category.getBundleGroups().remove(bundleGroup);
			categoryRepository.save(category);
		});
	}

	private void removeBundleGroupVersionFromBundles(BundleGroupVersion bundleGroupVersion) {
		logger.debug("{}: deleteFromBundles: Delete bundle group version from bundles", CLASS_NAME);

		Set<Bundle> mappedBundles = bundleGroupVersion.getBundles();
		mappedBundles.forEach((bundle) -> {
			bundle.getBundleGroupVersions().remove(bundleGroupVersion);
			bundleRepository.save(bundle);

			Set<BundleGroupVersion> bundleGroupVersions = bundle.getBundleGroupVersions();
			if (CollectionUtils.isEmpty(bundleGroupVersions)) {
				bundleRepository.delete(bundle);
				logger.debug("{}: Removed bundle {} from db", CLASS_NAME, bundle);
			}
		});
	}

	public List<BundleGroupVersion> getBundleGroupVersions(com.entando.hub.catalog.persistence.entity.BundleGroup bundleGroup, String version) {
		logger.debug("{}: getBundleGroupVersions: Get a bundle group version by Bundle Group and version: {}", CLASS_NAME, version);
		return bundleGroupVersionRepository.findByBundleGroupAndVersion(bundleGroup, version);
	}

	/**
	 * If a bundle group has 1 or no bundle group versions then it is editable.
	 * @param bundleGroup
	 * @return
	 */
	public boolean isBundleGroupEditable(BundleGroup bundleGroup) {
		logger.debug("{}: isBundleGroupEditable: Check if the bundle group {} is editable or not", CLASS_NAME, bundleGroup.getId());
		if (bundleGroupVersionRepository.countByBundleGroup(bundleGroup) <= 1) {
			return true;
		}
		return false;
	}

	/**
	 * Check if New Version option can be added on menu or not
	 * @param bundleGroup
	 * @return
	 */
	public boolean canAddNewVersion(BundleGroup bundleGroup) {
		logger.debug("{}: canAddNewVersion: Check if a new version can be added on a bundle group {}", CLASS_NAME, bundleGroup.getId());
		List<BundleGroupVersion> versions = bundleGroupVersionRepository.getByBundleGroupAndStatuses(bundleGroup.getId());
		if (!CollectionUtils.isEmpty(versions) && versions.size() > 1) {
			return false;
		}
		return true;
	}

	/**
	 * Convert to response view list
	 * @param page
	 * @return
	 */
	private List<BundleGroupVersionFilteredResponseView> toResponseViewList(Page<BundleGroupVersion> page) {
		logger.debug("{}: toResponseViewList: Convert Bundle Group Version list to response view list", CLASS_NAME);
		List<BundleGroupVersionFilteredResponseView> list = new ArrayList<BundleGroupVersionFilteredResponseView>();
		page.getContent().stream().forEach((entity) -> {
			BundleGroupVersionFilteredResponseView viewObj = new BundleGroupVersionFilteredResponseView();
			viewObj.setBundleGroupVersionId(entity.getId());
			viewObj.setDescription(entity.getDescription());
			viewObj.setDescriptionImage(entity.getDescriptionImage());
			viewObj.setStatus(entity.getStatus());
			viewObj.setDocumentationUrl(entity.getDocumentationUrl());
			viewObj.setVersion(entity.getVersion());
			viewObj.setBundleGroupUrl(getBundleGroupUrl(entity.getId()));
			viewObj.setLastUpdate(entity.getLastUpdated());
			
			if (!CollectionUtils.isEmpty(entity.getBundles())) {
				viewObj.setChildren(entity.getBundles().stream()
						.map(child -> child.getId().toString()).collect(Collectors.toList()));
			}

			if (Objects.nonNull(entity.getBundleGroup())) {
				viewObj.setName(entity.getBundleGroup().getName());
				viewObj.setBundleGroupId(entity.getBundleGroup().getId());
				viewObj.setIsEditable(isBundleGroupEditable(entity.getBundleGroup()));
				viewObj.setCanAddNewVersion(canAddNewVersion(entity.getBundleGroup()));
				if (Objects.nonNull(entity.getBundleGroup().getOrganisation())) {
					viewObj.setOrganisationId(entity.getBundleGroup().getOrganisation().getId());
					viewObj.setOrganisationName(entity.getBundleGroup().getOrganisation().getName());
				}
				if (!CollectionUtils.isEmpty(entity.getBundleGroup().getCategories())) {
					viewObj.setCategories(entity.getBundleGroup().getCategories().stream()
							.map((category) -> category.getId().toString()).collect(Collectors.toList()));
				}
				if (!CollectionUtils.isEmpty(entity.getBundleGroup().getVersion())) {
					viewObj.setAllVersions(entity.getBundleGroup().getVersion().stream()
							.map(version -> version.getVersion().toString()).collect(Collectors.toList()));
				}
			}
			list.add(viewObj);
		});
		return list;
	}
}
