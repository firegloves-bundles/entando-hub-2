package com.entando.hub.catalog.integration;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.entando.hub.catalog.testhelper.TestHelper;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
//@SpringBootTest
abstract class BaseFlowIT {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected BundleGroupRepository bundleGroupRepository;
    @Autowired
    protected BundleGroupVersionRepository bundleGroupVersionRepository;
    @Autowired
    protected CatalogRepository catalogRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected OrganisationRepository organisationRepository;

    @MockBean
    protected SecurityHelperService securityHelperService;

    protected Organisation organisation;
    protected Catalog catalog;
    protected Set<Category> categorySet;
    protected BundleGroup bundleGroup1;
    protected BundleGroup bundleGroup2;
    protected BundleGroupVersion bundleGroupVersion1;
    protected BundleGroupVersion bundleGroupVersion2;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        organisation = organisationRepository.save(TestHelper.stubOrganisation());

        catalog = catalogRepository.save(TestHelper.stubCatalog(organisation));

        categorySet = new HashSet<>(categoryRepository.findAll());

        bundleGroup1 = bundleGroupRepository.save(
                TestHelper.stubBundleGroup(organisation, catalog.getId(), categorySet)
                        .setId(TestHelper.BUNDLE_GROUP_ID_1));
        bundleGroup2 = bundleGroupRepository.save(
                TestHelper.stubBundleGroup(organisation, catalog.getId(), categorySet)
                        .setId(TestHelper.BUNDLE_GROUP_ID_2));

        bundleGroupVersion1 = bundleGroupVersionRepository.save(TestHelper.stubBundleGroupVersion(bundleGroup1));
        bundleGroupVersion2 = bundleGroupVersionRepository.save(
                TestHelper.stubBundleGroupVersion(bundleGroup2).setStatus(
                        Status.NOT_PUBLISHED));
    }

    @AfterEach
    public void tearDown() {
        bundleGroupVersionRepository.deleteAll();
        bundleGroupRepository.deleteAll();
        catalogRepository.deleteAll();
        organisationRepository.deleteAll();
//        portalUserRepository.deleteAll();
        TestHelper.resetSequenceNumber(this.jdbcTemplate, "hibernate_sequence");
        TestHelper.resetSequenceNumber(this.jdbcTemplate, "SEQ_CATALOG_ID");
        TestHelper.resetSequenceNumber(this.jdbcTemplate, "SEQ_ORGANISATION_ID");
    }
}
