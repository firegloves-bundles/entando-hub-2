package com.entando.hub.catalog.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest
public class CatalogFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private CatalogRepository catalogRepository;

//    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    public void tearDown() {
        catalogRepository.deleteAll();
        organisationRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles={"eh-admin"})
    void shouldGetAllCatalogs() throws Exception {

        final List<Catalog> catalogs = populateCatalogs();
//        String expectedBody = objectMapper.writeValueAsString(catalogs);

        mockMvc.perform(get("/api/catalog/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("cat 1"))
                .andExpect(jsonPath("$[0].organisationId").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("cat 2"))
                .andExpect(jsonPath("$[1].organisationId").value("2"));
    }

    @Test
    @WithMockUser(roles={"NON-AUTH"})
    void shouldReturnForbiddenWhileSendingANonAUthorizedRole() throws Exception {

        mockMvc.perform(get("/api/catalog/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    private List<Catalog> populateCatalogs() {
        List<Catalog> savedCatalogs = IntStream.range(1, 3)
                .mapToObj(i -> {
                    Organisation o = new Organisation()
                            .setId((long) i)
                            .setName("org " + i);
                    o = organisationRepository.save(o);

                    Catalog c = new Catalog()
                            .setId((long) i)
                            .setName("cat " + i)
                            .setOrganisation(o);
                    catalogRepository.saveAndFlush(c);

                    return c;
                })
                .collect(Collectors.toList());

        return savedCatalogs;
    }
}
