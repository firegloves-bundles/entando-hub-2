package com.entando.hub.catalog.testhelper;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.JdbcTemplate;

@UtilityClass
public class TestHelper {

    public static final Long BUNDLE_GROUP_ID_1 = 1L;
    public static final Long BUNDLE_GROUP_ID_2 = 2L;
    public static final Long BUNDLE_GROUP_VERSION_ID = 3L;
    public static final Boolean PUBLIC_CATALOG = true;
    public static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
    public static final String BUNDLE_GROUP_VERSION = "1.0.1";
    public static final String DESCRIPTION_IMAGE = "desc_image";
    public static final Long ORG_ID = 1L;
    public static final String ORG_NAME = "Test Org Name";
    public static final String ORG_DESCRIPTION = "Test Org Description";
    public static final Long CAT_ID = 5L;
    public static final String CAT_NAME = "Test Catalog Name";
    public static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "Bundle Group Version Description";
    public static final String DOCUMENTATION_URL = "http://justatest.com";
    public static final BundleGroupVersion.Status STATUS = BundleGroupVersion.Status.PUBLISHED;
    public static final BundleGroupVersion.Status STATUS_2 = BundleGroupVersion.Status.NOT_PUBLISHED;
    public static final boolean EDITABLE = true;
    public static final boolean CAN_ADD_NEW_VERSIONS = true;


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetSequenceNumber(JdbcTemplate jdbcTemplate, String sequenceName) {
        jdbcTemplate.update(String.format("ALTER SEQUENCE %s RESTART WITH 1", sequenceName));
    }

    public static Organisation stubOrganisation() {
        return new Organisation()
                .setId(ORG_ID)
                .setName(ORG_NAME)
                .setDescription(ORG_DESCRIPTION);
    }

    public static Catalog stubCatalog(Organisation org) {
        return new Catalog()
                .setId(CAT_ID)
                .setName(CAT_NAME)
                .setOrganisation(org);
    }


    public static BundleGroup stubBundleGroup(Organisation organisation, Long catalogId, Set<Category> categorySet) {
        return new BundleGroup()
                .setId(BUNDLE_GROUP_ID_1)
                .setName(BUNDLE_GROUP_NAME)
                .setOrganisation(organisation)
                .setPublicCatalog(PUBLIC_CATALOG)
                .setCategories(categorySet)
                .setCatalogId(catalogId);
    }

    public static BundleGroupVersion stubBundleGroupVersion(BundleGroup bundleGroup) {
        return new BundleGroupVersion()
                .setBundleGroup(bundleGroup)
                .setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION)
                .setDocumentationUrl(DOCUMENTATION_URL)
                .setVersion(BUNDLE_GROUP_VERSION)
                .setDescriptionImage(DESCRIPTION_IMAGE)
                .setStatus(STATUS);
    }

    public static BundleGroupVersionFilteredResponseView stubBundleGroupVersionFilteredResponseView(Long bundleGroupId,
            Long bundleGroupVersionId) {
        return new BundleGroupVersionFilteredResponseView()
                .setBundleGroupId(bundleGroupId)
                .setBundleGroupUrl("")
                .setBundleGroupVersionId(bundleGroupVersionId)
                .setName(BUNDLE_GROUP_NAME)
                .setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION)
                .setDescriptionImage(DESCRIPTION_IMAGE)
                .setDocumentationUrl(DOCUMENTATION_URL)
                .setVersion(BUNDLE_GROUP_VERSION)
                .setVersion(BUNDLE_GROUP_VERSION)
                .setStatus(STATUS)
                .setOrganisationId(ORG_ID)
                .setOrganisationName(ORG_NAME)
                .setPublicCatalog(PUBLIC_CATALOG)
                .setCategories(Arrays.asList("1", "2", "3"))
                .setAllVersions(Arrays.asList(BUNDLE_GROUP_VERSION))
                .setIsEditable(EDITABLE)
                .setCanAddNewVersion(CAN_ADD_NEW_VERSIONS);
    }

    public static BundleGroupVersionFilteredResponseView stubSecondBundleGroupVersionFilteredResponseView(
            Long bundleGroupId,
            Long bundleGroupVersionId) {
        return stubBundleGroupVersionFilteredResponseView(bundleGroupId, bundleGroupVersionId)
                .setStatus(STATUS_2);
    }
}
