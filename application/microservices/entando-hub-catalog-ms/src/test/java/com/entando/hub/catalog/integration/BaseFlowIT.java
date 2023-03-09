package com.entando.hub.catalog.integration;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.entando.hub.catalog.testhelper.TestHelper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

@AutoConfigureMockMvc
//@SpringBootTest
abstract class BaseFlowIT {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected BundleGroupRepository bundleGroupRepository;
    @Autowired
    protected BundleRepository bundleRepository;
    @Autowired
    protected BundleGroupVersionRepository bundleGroupVersionRepository;
    @Autowired
    protected CatalogRepository catalogRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected OrganisationRepository organisationRepository;
    @Autowired
    protected PortalUserRepository portalUserRepository;

    @MockBean
    protected SecurityHelperService securityHelperService;

    // belongs to organisation 1 and catalog 1
    protected PortalUser portalUser;
    // org1 belongs to catalog1, org2 belongs to catalog2
    protected Organisation organisation1, organisation2;
    protected Catalog catalog1, catalog2;
    protected Set<Category> categorySet;
    // all public but bundleGroup4
    // bundleGroup1 and 2 belong to org1 and cat1, bundleGroup3 belongs to org2 and cat2
    protected BundleGroup bundleGroup1, bundleGroup2, bundleGroup3, bundleGroup4;
    protected Bundle bundle1, bundle2, bundle3, bundle4;
    protected BundleGroupVersion bundleGroupVersion1, bundleGroupVersion2, bundleGroupVersion3, bundleGroupVersion4;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        portalUserRepository.deleteAll();
        bundleGroupVersionRepository.deleteAll();
        bundleGroupRepository.deleteAll();
        catalogRepository.deleteAll();
        organisationRepository.deleteAll();
        bundleRepository.deleteAll();
        TestHelper.resetSequenceNumber(this.jdbcTemplate, "hibernate_sequence");
        TestHelper.resetSequenceNumber(this.jdbcTemplate, "SEQ_CATALOG_ID");
        TestHelper.resetSequenceNumber(this.jdbcTemplate, "SEQ_ORGANISATION_ID");
    }

    protected void setUpBundleGroupVersionFlowData() {
        createOrganisation1();
        createCatalog1();
        createCategories();
        createBundleGroup1();
        createBundleGroup2();
        createBundleGroupVersion1();
        createBundleGroupVersion2();
    }

    protected void setUpBundleFlowData() {
        createOrganisation1();
        createOrganisation2();
        createCatalog1();
        createCatalog2();
        createNonAdminUser();
        createCategories();
        createBundleGroup1();
        createBundleGroup2();
        createBundleGroup3();
        createBundleGroup4();
        createBundle1();
        createBundle2();
        createBundle3();
        createBundle4();
        createBundleGroupVersion1();
        createBundleGroupVersion2();
        createBundleGroupVersion3();
        createBundleGroupVersion4();
    }

    private void createNonAdminUser() {
        portalUser = portalUserRepository.save(TestHelper.stubPortalUser(Collections.singleton((organisation1))));
    }

    private void createOrganisation1() {
        organisation1 = organisationRepository.save(TestHelper.stubOrganisation());
    }

    private void createOrganisation2() {
        organisation2 = organisationRepository.save(TestHelper.stubOrganisation());
    }

    private void createCatalog1() {
        catalog1 = catalogRepository.save(TestHelper.stubCatalog(organisation1));
    }

    private void createCatalog2() {
        catalog2 = catalogRepository.save(TestHelper.stubCatalog(organisation2));
    }

    private void createCategories() {
        categorySet = new HashSet<>(categoryRepository.findAll());
    }

    private void createBundle1() {
        bundle1 = bundleRepository.save(TestHelper.stubBundle());
    }

    private void createBundle2() {
        bundle2 = bundleRepository.save(TestHelper.stubBundle());
    }

    private void createBundle3() {
        bundle3 = bundleRepository.save(TestHelper.stubBundle());
    }

    private void createBundle4() {
        bundle4 = bundleRepository.save(TestHelper.stubBundle());
    }

    private void createBundleGroup1() {
        bundleGroup1 = bundleGroupRepository.save(
                TestHelper.stubBundleGroup(organisation1, catalog1.getId(), categorySet));
    }

    private void createBundleGroup2() {
        bundleGroup2 = bundleGroupRepository.save(
                TestHelper.stubBundleGroup(organisation1, catalog1.getId(), categorySet));
    }

    private void createBundleGroup3() {
        bundleGroup3 = bundleGroupRepository.save(
                TestHelper.stubBundleGroup(organisation2, catalog2.getId(), categorySet));
    }

    private void createBundleGroup4() {
        bundleGroup4 = bundleGroupRepository.save(
                TestHelper.stubBundleGroup(organisation2, null, categorySet).setPublicCatalog(false));
    }

    private void createBundleGroupVersion1() {
        bundleGroupVersion1 = bundleGroupVersionRepository.save(
                TestHelper.stubBundleGroupVersion(bundleGroup1, bundle1));
    }

    private void createBundleGroupVersion2() {
        bundleGroupVersion2 = bundleGroupVersionRepository.save(
                TestHelper.stubBundleGroupVersion(bundleGroup2, bundle2)
                        .setStatus(
                                Status.NOT_PUBLISHED));
    }

    private void createBundleGroupVersion3() {
        bundleGroupVersion3 = bundleGroupVersionRepository.save(
                TestHelper.stubBundleGroupVersion(bundleGroup3, bundle3)
                        .setVersion(TestHelper.BUNDLE_GROUP_VERSION_2));
    }

    private void createBundleGroupVersion4() {
        bundleGroupVersion4 = bundleGroupVersionRepository.save(
                TestHelper.stubBundleGroupVersion(bundleGroup4, bundle4));
    }

    @FunctionalInterface
    public interface StatusMatcher {
        ResultMatcher checkStatus(StatusResultMatchers statusResultMatchers);
    }
}
