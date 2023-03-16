package com.entando.hub.catalog.integration;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.config.AuthoritiesConstants.AUTHOR;
import static com.entando.hub.catalog.config.AuthoritiesConstants.MANAGER;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.service.dto.CatalogDTO;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.entando.hub.catalog.testhelper.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@AutoConfigureMockMvc
@SpringBootTest
public class CatalogFlowIT {

    public static final String USER_PREFIX = "falcon-";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private PortalUserRepository portalUserRepository;
    @Autowired
    private CatalogRepository catalogRepository;
    @MockBean
    private SecurityHelperService securityHelperService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    public void tearDown() {
        portalUserRepository.deleteAll();
        catalogRepository.deleteAll();
        organisationRepository.deleteAll();
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_CATALOG_ID");
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_ORGANISATION_ID");
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetAllCatalogsWhileAdmin() throws Exception {

        when(securityHelperService.getContextAuthenticationUsername()).thenReturn("admin");
        when(securityHelperService.isAdmin()).thenReturn(true);

        final List<Catalog> catalogs = populateCatalogs("OrgName", 3);
        List<CatalogDTO> catalogsDTO = catalogs.stream().map(this::mapToDTO).collect(Collectors.toList());

        String expectedBody = objectMapper.writeValueAsString(catalogsDTO);

        mockMvc.perform(get("/api/catalog/").with(user("admin").roles("eh-admin"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody))
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetOnlyUserScopedCatalogsWhileNONAdmin() throws Exception {

        String user = USER_PREFIX + "1";
        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(user);
        when(securityHelperService.isAdmin()).thenReturn(false);

        String orgName = "OrgName";
        String catalogName = orgName + "1 private catalog";
        final List<Catalog> catalogs = populateCatalogs(orgName, 3);
        List<CatalogDTO> catalogsDTO = catalogs.stream().filter(c -> c.getName().equals(catalogName))
                .map(this::mapToDTO).collect(Collectors.toList());

        String expectedBody = objectMapper.writeValueAsString(catalogsDTO);

        mockMvc.perform(get("/api/catalog/").with(user("admin").roles("eh-admin"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody))
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetEmptyListOfCatalogsWhileNONAdminAndUserNonAssociatedToAnyCatalog() throws Exception {

        String user = USER_PREFIX + "5";
        PortalUser p = new PortalUser()
                .setId((long) 5)
                .setUsername(user)
                .setEmail(user + "@mail.com");
        portalUserRepository.save(p);

        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(user);
        when(securityHelperService.isAdmin()).thenReturn(false);

        String orgName = "OrgName";
        populateCatalogs(orgName, 3);

        mockMvc.perform(get("/api/catalog/").with(user("admin").roles("eh-admin"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetEmptyListOfCatalogsWhenDbIsEmpty() throws Exception {

        when(securityHelperService.getContextAuthenticationUsername()).thenReturn("admin");
        when(securityHelperService.isAdmin()).thenReturn(true);

        List<CatalogDTO> catalogsDTO = Collections.emptyList();

        String expectedBody = objectMapper.writeValueAsString(catalogsDTO);

        mockMvc.perform(get("/api/catalog/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody))
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    void shouldReturnUnauthorizedForNoRole() throws Exception {
        mockMvc.perform(get("/api/catalog/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetCatalogById() throws Exception {
        List<Catalog> catalogs = this.populateCatalogs("Entando", 1);
        String expectedBody = objectMapper.writeValueAsString(this.mapToDTO(catalogs.get(0)));

        when(securityHelperService.getContextAuthenticationUsername()).thenReturn("admin");
        when(securityHelperService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody))
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldReturnNotFoundWhenGettingNonExistentCatalog() throws Exception {
        mockMvc.perform(get("/api/catalog/100")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"no-authorized-role"})
    void shouldReturnForbiddenWhenGettingWithNonAdminRole() throws Exception {
        mockMvc.perform(get("/api/catalog/100")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenGettingWithNoRole() throws Exception {
        mockMvc.perform(get("/api/catalog/100")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldCreateCatalog() throws Exception {
        this.populateOrganisation("Entando", 1);

        MvcResult response = mockMvc.perform(post("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = response.getResponse().getContentAsString();
        CatalogDTO catalogDTO = this.objectMapper.readValue(responseString, CatalogDTO.class);
        Assertions.assertEquals(1L, catalogDTO.getId());
        Assertions.assertEquals(1L, catalogDTO.getOrganisationId());
        Assertions.assertEquals("Entando1 private catalog", catalogDTO.getName());
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldReturnNotFoundWhenCreateCatalogForNoOrganisationInDB() throws Exception {
        mockMvc.perform(post("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    void shouldReturnForbiddenWhenCreateCatalogWithNoAdminRole() throws Exception {
        mockMvc.perform(post("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void shouldReturnUnauthorizedWhenCreateCatalogWithNoRole() throws Exception {
        mockMvc.perform(post("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldDeleteCatalog() throws Exception {
        this.populateCatalogs("Entando", 1);

        MvcResult response = mockMvc.perform(delete("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = response.getResponse().getContentAsString();
        CatalogDTO catalogDTO = this.objectMapper.readValue(responseString, CatalogDTO.class);
        Assertions.assertEquals(1L, catalogDTO.getId());
        Assertions.assertEquals(1L, catalogDTO.getOrganisationId());
        Assertions.assertEquals("Entando1 private catalog", catalogDTO.getName());
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldReturnNotFoundWhenDeleteCatalogForNoCatalogIdInDb() throws Exception {
        mockMvc.perform(delete("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    void shouldReturnForbiddenWhenDeleteCatalogWithNoAdminRole() throws Exception {
        mockMvc.perform(delete("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteCatalogWithNoRole() throws Exception {
        mockMvc.perform(delete("/api/catalog/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    private List<Catalog> populateCatalogs(String nameOrganisation, int catalogNumbers) {

        return IntStream.range(1, catalogNumbers + 1)
                .mapToObj(i -> {
                    Organisation o = new Organisation()
                            .setId((long) i)
                            .setName(nameOrganisation + i);
                    o = organisationRepository.save(o);

                    Catalog c = new Catalog()
                            .setId((long) i)
                            .setName(nameOrganisation + i + " private catalog")
                            .setOrganisation(o);
                    catalogRepository.saveAndFlush(c);

                    PortalUser p = new PortalUser()
                            .setId((long) i)
                            .setUsername(USER_PREFIX + i)
                            .setEmail(USER_PREFIX + i + "@mail.com")
                            .setOrganisations(Set.of(o));
                    portalUserRepository.save(p);

                    return c;
                })
                .collect(Collectors.toList());
    }

    private List<Organisation> populateOrganisation(String nameOrganisation, int orgNumbers) {
        return IntStream.range(1, orgNumbers + 1)
                .mapToObj(i -> {
                    Organisation o = new Organisation()
                            .setId((long) i)
                            .setName(nameOrganisation + i);
                    return organisationRepository.save(o);
                })
                .collect(Collectors.toList());
    }

    public CatalogDTO mapToDTO(Catalog catalog) {
        return new CatalogDTO(catalog.getId(), catalog.getOrganisation().getId(), catalog.getName());
    }

}
