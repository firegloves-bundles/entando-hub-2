package com.entando.hub.catalog.testhelper;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.Bundle.DescriptorVersion;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.BundleController;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;
import com.entando.hub.catalog.rest.BundleGroupVersionController.BundleGroupVersionView;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.JdbcTemplate;

@UtilityClass
public class TestHelper {

    public static final String ADMIN_USERNAME = "eh-admin";
    public static final String NON_ADMIN_USERNAME = "eh-manager";
    public static final String NON_ADMIN_EMAIL = "eh-manager@mail.com";
    public static final String BUNDLE_NAME = "Amazing bundle";
    public static final String BUNDLE_DESCRIPTION = "An amazing bundle";
    public static final String BUNDLE_REPO_ADDRESS = "docker://registry.hub.docker.com/entando/amazing";
    public static final String BUNDLE_REPO_SRC_ADDRESS = "https://github.com/entando/amazing";
    public static final String BUNDLE_DEPENDENCIES = "my-dep";
    public static final Bundle.DescriptorVersion BUNDLE_DESCRIPTOR_VERSIONS = DescriptorVersion.V5;
    public static final Boolean PUBLIC_CATALOG = true;
    public static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
    public static final String BUNDLE_GROUP_VERSION = "1.0.1";
    public static final String BUNDLE_GROUP_VERSION_2 = "2.0.1";
    public static final String DESCRIPTION_IMAGE = "desc_image";
    public static final String ORG_NAME = "Test Org Name";
    public static final String ORG_DESCRIPTION = "Test Org Description";
    public static final String CAT_NAME = "Test Catalog Name";
    public static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "Bundle Group Version Description";
    public static final String BUNDLE_GROUP_VERSION_CONTACT_URL = "http://www.entando.com/contacts";
    public static final String DOCUMENTATION_URL = "http://justatest.com";
    public static final BundleGroupVersion.Status STATUS = BundleGroupVersion.Status.PUBLISHED;
    public static final BundleGroupVersion.Status STATUS_2 = BundleGroupVersion.Status.NOT_PUBLISHED;
    public static final boolean EDITABLE = true;
    public static final boolean CAN_ADD_NEW_VERSIONS = true;

    public static String mapToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetSequenceNumber(JdbcTemplate jdbcTemplate, String sequenceName) {
        jdbcTemplate.update(String.format("ALTER SEQUENCE %s RESTART WITH 1", sequenceName));
    }

    public static PortalUser stubPortalUser(Set<Organisation> organisationSet) {
        return new PortalUser()
                .setUsername(NON_ADMIN_USERNAME)
                .setEmail(NON_ADMIN_EMAIL)
                .setOrganisations(organisationSet);
    }

    public static Organisation stubOrganisation() {
        return new Organisation()
                .setName(ORG_NAME)
                .setDescription(ORG_DESCRIPTION);
    }

    public static Catalog stubCatalog(Organisation org) {
        return new Catalog()
                .setName(CAT_NAME)
                .setOrganisation(org);
    }

    public static Bundle stubBundle() {
        return new Bundle()
                .setName(BUNDLE_NAME)
                .setDescription(BUNDLE_DESCRIPTION)
                .setGitRepoAddress(BUNDLE_REPO_ADDRESS)
                .setGitSrcRepoAddress(BUNDLE_REPO_SRC_ADDRESS)
                .setDependencies(BUNDLE_DEPENDENCIES)
                .setDescriptorVersion(BUNDLE_DESCRIPTOR_VERSIONS);
    }

    public static BundleController.Bundle stubBundleDto(Long id, List<BundleGroupVersion> bundleGroupVersion) {
        return new BundleController.Bundle(
                id + "",
                BUNDLE_NAME,
                BUNDLE_DESCRIPTION,
                BUNDLE_REPO_ADDRESS,
                BUNDLE_REPO_SRC_ADDRESS,
                List.of(BUNDLE_DEPENDENCIES),
                bundleGroupVersion.stream().map(bgv -> bgv.getId() + "").collect(Collectors.toList()),
                BUNDLE_DESCRIPTOR_VERSIONS.name());
    }

    public static BundleGroup stubBundleGroup(Organisation organisation, Long catalogId, Set<Category> categorySet) {
        return new BundleGroup()
                .setName(BUNDLE_GROUP_NAME)
                .setOrganisation(organisation)
                .setPublicCatalog(PUBLIC_CATALOG)
                .setCategories(categorySet)
                .setCatalogId(catalogId);
    }

    public static BundleGroupVersion stubBundleGroupVersion(BundleGroup bundleGroup, Bundle bundle) {
        return new BundleGroupVersion()
                .setBundleGroup(bundleGroup)
                .setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION)
                .setDocumentationUrl(DOCUMENTATION_URL)
                .setVersion(BUNDLE_GROUP_VERSION)
                .setDescriptionImage(DESCRIPTION_IMAGE)
                .setStatus(STATUS)
                .setBundles(Collections.singleton(bundle));
    }

    public static BundleGroupVersionView stubBundleGroupVersionView(BundleGroupVersion bundleGroupVersion, Bundle bundle, BundleGroupVersion.Status status) {

        final BundleGroup bundleGroup = bundleGroupVersion.getBundleGroup();

        return new BundleGroupVersionView()
                .setBundleGroupVersionId(bundleGroupVersion.getId() + "")
                .setBundleGroupId(bundleGroup.getId() + "")
                .setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION)
                .setDocumentationUrl(DOCUMENTATION_URL)
                .setVersion(BUNDLE_GROUP_VERSION)
                .setDescriptionImage(DESCRIPTION_IMAGE)
                .setStatus(status)
                .setOrganisationId(bundleGroup.getOrganisation().getId())
                .setOrganisationName(bundleGroup.getOrganisation().getName())
                .setName(BUNDLE_GROUP_NAME)
                .setCategories(Arrays.asList("1", "2", "3"))
                .setChildren(List.of(bundle.getId()))
                .setDisplayContactUrl(true)
                .setContactUrl(BUNDLE_GROUP_VERSION_CONTACT_URL);
    }

    public static BundleGroupVersionFilteredResponseView stubBundleGroupVersionFilteredResponseView(Long bundleGroupId,
            Long bundleGroupVersionId, Long orgId, Long bundleId) {
        return new BundleGroupVersionFilteredResponseView()
                .setBundleGroupId(bundleGroupId)
                .setBundleGroupUrl("")
                .setBundleGroupVersionId(bundleGroupVersionId)
                .setName(BUNDLE_GROUP_NAME)
                .setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION)
                .setDescriptionImage(DESCRIPTION_IMAGE)
                .setDocumentationUrl(DOCUMENTATION_URL)
                .setVersion(BUNDLE_GROUP_VERSION)
                .setStatus(STATUS)
                .setOrganisationId(orgId)
                .setOrganisationName(ORG_NAME)
                .setPublicCatalog(PUBLIC_CATALOG)
                .setChildren(List.of(bundleId + ""))
                .setCategories(Arrays.asList("1", "2", "3"))
                .setAllVersions(Arrays.asList(BUNDLE_GROUP_VERSION))
                .setIsEditable(EDITABLE)
                .setCanAddNewVersion(CAN_ADD_NEW_VERSIONS);
    }

    public static BundleGroupVersionFilteredResponseView stubSecondBundleGroupVersionFilteredResponseView(
            Long bundleGroupId, Long bundleGroupVersionId, Long orgId, Long bundleId) {
        return stubBundleGroupVersionFilteredResponseView(bundleGroupId, bundleGroupVersionId, orgId, bundleId)
                .setStatus(STATUS_2);
    }
}
