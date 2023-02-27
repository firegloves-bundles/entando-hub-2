package com.entando.hub.catalog.integration;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.dto.CatalogDTO;
import com.entando.hub.catalog.testhelper.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class CatalogFlowIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private CatalogRepository catalogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        catalogRepository.deleteAll();
        organisationRepository.deleteAll();
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_CATALOG_ID");
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_ORGANISATION_ID");
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldGetAllCatalogs() throws Exception {
        final List<Catalog> catalogs = populateCatalogs("OrgName", 3);
        List<CatalogDTO> catalogsDTO = catalogs.stream().map(this::mapToDTO).collect(Collectors.toList());

        String expectedBody = objectMapper.writeValueAsString(catalogsDTO);

        mockMvc.perform(get("/api/catalog/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody))
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @WithMockUser(roles={ADMIN})
    void shouldGetEmptyListOfCatalogsWhenDbIsEmpty() throws Exception {
        List<CatalogDTO> catalogsDTO = Collections.emptyList();

        String expectedBody = objectMapper.writeValueAsString(catalogsDTO);

        mockMvc.perform(get("/api/catalog/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody))
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @WithMockUser(roles={AUTHOR})
    void shouldReturnForbiddenForNonAdminRole() throws Exception {
        mockMvc.perform(get("/api/catalog/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
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
    @WithMockUser(roles={AUTHOR})
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
        this.populateOrganisation("Entando",1);

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
        this.populateCatalogs("Entando",1);

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

    private List<Catalog> populateCatalogs(String nameOrganisation, int catalogNumbers){
        return IntStream.range(1, catalogNumbers+1)
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
                    return c;
                })
                .collect(Collectors.toList());
    }

    private List<Organisation> populateOrganisation(String nameOrganisation, int orgNumbers){
        return IntStream.range(1, orgNumbers+1)
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
