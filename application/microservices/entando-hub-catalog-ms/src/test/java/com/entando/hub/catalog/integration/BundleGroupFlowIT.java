package com.entando.hub.catalog.integration;

import com.entando.hub.catalog.persistence.*;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroupNoId;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.BundleGroupController;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.entando.hub.catalog.testhelper.TestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest
class BundleGroupFlowIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BundleGroupRepository bundleGroupRepository;
    @Autowired
    private BundleGroupVersionRepository bundleGroupVersionRepository;
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private PortalUserRepository portalUserRepository;
    @MockBean
    SecurityHelperService securityHelperService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String URI = "/api/bundlegroups/";
    private static final Long BUNDLE_GROUP_ID = 1L;
    private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
    private static final String ORG_NAME = "Test Org Name";
    private static final String ORG_DESCRIPTION = "Test Org Description";
    private static final String CAT_NAME = "Test Catalog Name";
    private static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "Bundle Group Version Description";
    private static final String documentation_url = "http://justatest.com";
    @AfterEach
    public void tearDown() {
        bundleGroupVersionRepository.deleteAll();
        bundleGroupRepository.deleteAll();
        catalogRepository.deleteAll();
        organisationRepository.deleteAll();
        portalUserRepository.deleteAll();
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"hibernate_sequence");
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_CATALOG_ID");
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_ORGANISATION_ID");
    }

    @Test
    void shouldGetBundleGroupsByOrganisationId() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        Catalog catalogSaved = catalogRepository.save(new Catalog().setName(CAT_NAME).setOrganisation(organisationSaved));

        BundleGroup stubBundleGroup1 = getStubBundleGroup(true, organisationSaved, Optional.of(catalogSaved.getId())).setId(null);
        bundleGroupRepository.save(stubBundleGroup1);
        BundleGroup stubBundleGroup2 = getStubBundleGroup(true, organisationSaved, Optional.of(catalogSaved.getId())).setId(null);
        bundleGroupRepository.save(stubBundleGroup2);

        //Case 1: no organisation specified
        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(1L))
                .andExpect(jsonPath("$.[1].bundleGroupId").value(2L))
                .andExpect(jsonPath("$.[0].name").value(BUNDLE_GROUP_NAME))
                .andExpect(jsonPath("$.[1].name").value(BUNDLE_GROUP_NAME));

        //Case 2: testing with specific organisation
        mockMvc.perform(MockMvcRequestBuilders.get(URI + "?organisationId=" + organisationSaved.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(1L))
                .andExpect(jsonPath("$.[1].bundleGroupId").value(2L))
                .andExpect(jsonPath("$.[0].name").value(BUNDLE_GROUP_NAME))
                .andExpect(jsonPath("$.[1].name").value(BUNDLE_GROUP_NAME));
    }

    @Test
    void shouldGetBundleGroups() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        Catalog catalogSaved = catalogRepository.save(new Catalog().setName(CAT_NAME).setOrganisation(organisationSaved));

        BundleGroup stubBundleGroup1 = getStubBundleGroup(true, organisationSaved, Optional.of(catalogSaved.getId())).setId(null);
        bundleGroupRepository.save(stubBundleGroup1);
        BundleGroup stubBundleGroup2 = getStubBundleGroup(true, organisationSaved, Optional.of(catalogSaved.getId())).setId(null);
        bundleGroupRepository.save(stubBundleGroup2);

        mockMvc.perform(MockMvcRequestBuilders.get(URI + stubBundleGroup1.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.bundleGroupId").value(1L))
                .andExpect(jsonPath("$.name").value(BUNDLE_GROUP_NAME));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldCreateBundleGroup() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));

        BundleGroup stubBundleGroup = getStubBundleGroup(true, organisationSaved, Optional.empty());
        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bundleGroupId").value(stubBundleGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value(stubBundleGroup.getName()))
                .andExpect(jsonPath("$.catalogId").doesNotExist());
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldCreateBundleGroupAndAssociationWithCatalogWhenIsPrivate() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        catalogRepository.save(new Catalog().setName(CAT_NAME).setOrganisation(organisationSaved));

        BundleGroup stubBundleGroup = getStubBundleGroup(false, organisationSaved, Optional.empty());
        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bundleGroupId").value(stubBundleGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value(stubBundleGroup.getName()))
                .andExpect(jsonPath("$.publicCatalog").value(stubBundleGroup.getPublicCatalog()));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldCreateBundleGroupAndNotAssociationWithPrivateCatalogWhenIsPublic() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));

        BundleGroup stubBundleGroup = getStubBundleGroup(true, organisationSaved, Optional.empty());
        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bundleGroupId").value(stubBundleGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value(stubBundleGroup.getName()))
                .andExpect(jsonPath("$.publicCatalog").value(stubBundleGroup.getPublicCatalog()));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldCreateBundleGroupAndAssociationWithPublicAndPrivateCatalog() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        catalogRepository.save(new Catalog().setName(CAT_NAME).setOrganisation(organisationSaved));

        BundleGroup stubBundleGroup = getStubBundleGroup(true, organisationSaved, Optional.empty());
        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bundleGroupId").value(stubBundleGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value(stubBundleGroup.getName()))
                .andExpect(jsonPath("$.publicCatalog").value(stubBundleGroup.getPublicCatalog()));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldNotCreateBundleGroupAndWhenIsNotPublicAndPrivateCatalogIsNotFound() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));

        BundleGroup stubBundleGroup = getStubBundleGroup(false, organisationSaved, Optional.empty());
        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Private Catalog is required for non-public bundle groups"));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldUpdateBundleGroup() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        catalogRepository.save(new Catalog().setName(CAT_NAME).setOrganisation(organisationSaved));
        BundleGroup stubBundleGroup = getStubBundleGroup(true, organisationSaved, Optional.empty());
        bundleGroupRepository.save(stubBundleGroup);
        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI + stubBundleGroup.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bundleGroupId").value(stubBundleGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value(stubBundleGroup.getName()));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldReturnNotFoundWhenUpdateBundleGroupThatDoesntExist() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        catalogRepository.save(new Catalog().setName(CAT_NAME).setOrganisation(organisationSaved));
        BundleGroup stubBundleGroup = getStubBundleGroup(true, organisationSaved, Optional.empty());

        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI + stubBundleGroup.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldReturnConflictWhenBundleGroupIsNotEditable() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        catalogRepository.save(new Catalog().setName(CAT_NAME).setOrganisation(organisationSaved));
        BundleGroup stubBundleGroup = getStubBundleGroup(true, organisationSaved, Optional.empty());
        BundleGroup savedStubBundleGroup = bundleGroupRepository.save(stubBundleGroup);

        BundleGroupVersion bgv1 = new BundleGroupVersion()
                .setBundleGroup(savedStubBundleGroup)
                .setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION)
                .setDocumentationUrl(documentation_url)
                .setVersion("1.0.0")
                .setDescriptionImage("desc_image")
                .setStatus(BundleGroupVersion.Status.ARCHIVE);
        BundleGroupVersion bgv2 = new BundleGroupVersion()
                .setBundleGroup(savedStubBundleGroup)
                .setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION)
                .setDocumentationUrl(documentation_url)
                .setVersion("1.0.1")
                .setDescriptionImage("desc_image")
                .setStatus(BundleGroupVersion.Status.PUBLISHED);

        bundleGroupVersionRepository.saveAndFlush(bgv1);
        bundleGroupVersionRepository.saveAndFlush(bgv2);
        BundleGroupNoId bundleGroupNoId = new BundleGroupController.BundleGroupNoId(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.post(URI + stubBundleGroup.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(bundleGroupNoId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldDeleteBundleGroup() throws Exception {
        Organisation organisationSaved = organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));
        BundleGroup stubBundleGroup = getStubBundleGroup(true, organisationSaved, Optional.empty());
        BundleGroup savedStubBundleGroup = bundleGroupRepository.save(stubBundleGroup);

        mockMvc.perform(MockMvcRequestBuilders.delete(URI + savedStubBundleGroup.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldNotDeleteBundleGroupWhenItIsNotFound() throws Exception {
        String bundleGroupId = "1";
        organisationRepository.save(new Organisation().setName(ORG_NAME).setDescription(ORG_DESCRIPTION));

        mockMvc.perform(MockMvcRequestBuilders.delete(URI + bundleGroupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private BundleGroup getStubBundleGroup(Boolean publicCatalog, Organisation organisation, Optional<Long> catalogId){
        return new BundleGroup()
                .setId(BUNDLE_GROUP_ID)
                .setName(BUNDLE_GROUP_NAME)
                .setOrganisation(organisation)
                .setPublicCatalog(publicCatalog)
                .setCatalogId(catalogId.orElse(null));
    }

}
