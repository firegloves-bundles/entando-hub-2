package com.entando.hub.catalog.integration;

import com.entando.hub.catalog.persistence.*;
import com.entando.hub.catalog.persistence.entity.*;
import com.entando.hub.catalog.rest.model.UserOrganisationRequest;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.model.UserRepresentation;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class PortalUserFlowIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PortalUserRepository portalUserRepository;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @MockBean
    private SecurityHelperService securityHelperService;
    @MockBean
    private KeycloakService keycloakService;
    private static final String URI = "/api/users/";
    private final String ADMIN_USERNAME = "Admin";
    private final String MANAGER_USERNAME = "Manager";
    private final String AUTHOR_USERNAME = "Author";
    private final String EMAIL = "admin.123@test.co.in";

    @AfterEach
    public void tearDown() {
        portalUserRepository.deleteAll();
        organisationRepository.deleteAll();
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"hibernate_sequence");
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_CATALOG_ID");
        TestHelper.resetSequenceNumber(this.jdbcTemplate,"SEQ_ORGANISATION_ID");
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetUsers() throws Exception {
        Organisation org = this.populateOrganisation();
        PortalUser savedPortalUser = this.populatePortalUser(org.getId(), ADMIN_USERNAME);

        when(keycloakService.getUser(savedPortalUser.getUsername())).thenReturn(this.getStubUserRepresentation(savedPortalUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].username").value(savedPortalUser.getUsername()))
                .andExpect(jsonPath("$.[0].email").value(savedPortalUser.getEmail()))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetUsersReturnNotFoundWhenOrganisationDoesNotExist() throws Exception {
        long organisationId = 4L;
        this.populateOrganisation();

        mockMvc.perform(MockMvcRequestBuilders.get(URI + "?organisationId=" + organisationId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldGetPortalUserByUsernameFromToken() throws Exception {
        Organisation org = this.populateOrganisation();
        PortalUser savedAdminPortalUser = this.populatePortalUser(org.getId(), ADMIN_USERNAME);
        PortalUser savedManagerPortalUser = this.populatePortalUser(org.getId(), MANAGER_USERNAME);
        PortalUser savedAuthorPortalUser = this.populatePortalUser(org.getId(), AUTHOR_USERNAME);

        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(savedAdminPortalUser.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/details").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(savedAdminPortalUser.getUsername()))
                .andExpect(jsonPath("$.email").value(savedAdminPortalUser.getEmail()))
                .andReturn();

        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(savedManagerPortalUser.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/details").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(savedManagerPortalUser.getUsername()))
                .andExpect(jsonPath("$.email").value(savedManagerPortalUser.getEmail()))
                .andReturn();

        when(securityHelperService.getContextAuthenticationUsername()).thenReturn(savedAuthorPortalUser.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/details").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.username").value(savedAuthorPortalUser.getUsername()))
                .andExpect(jsonPath("$.email").value(savedAuthorPortalUser.getEmail()))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldAddUserToOrganisation() throws Exception{
        Organisation org = this.populateOrganisation();
        UserOrganisationRequest userOrganisationRequest = new UserOrganisationRequest();
        userOrganisationRequest.setUsername(ADMIN_USERNAME);
        String inputJson = TestHelper.mapToJson(userOrganisationRequest);

        when(keycloakService.getUser(ADMIN_USERNAME)).thenReturn(new UserRepresentation());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/{organisationId}", org.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.result").value(true)).andReturn();
    }

    @Test
    @WithMockUser(roles = {AUTHOR})
    void shouldReturnForbiddenWhenAddUserToOrganisationWithInvalidRole() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/{organisationId}", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"name\": \"inputJson\"}"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldDeleteUser() throws Exception{
        Organisation org = this.populateOrganisation();
        this.populatePortalUser(org.getId(), ADMIN_USERNAME);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{username}", ADMIN_USERNAME).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.result").value(true)).andReturn();
    }

    @Test
    @WithMockUser(roles = {MANAGER})
    void shouldReturnForbiddenWhenDeleteUserWithInvalidRole() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{username}", ADMIN_USERNAME).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {ADMIN})
    void shouldDeleteUserFromOrganisation() throws Exception {
        Organisation org = this.populateOrganisation();
        PortalUser savedPortalUser = this.populatePortalUser(org.getId(), ADMIN_USERNAME);

        when(keycloakService.getUser(savedPortalUser.getUsername())).thenReturn(new UserRepresentation());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{organisationId}/user/{username}", org.getId(), savedPortalUser.getUsername()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.result").value(true)).andReturn();
    }

    @Test
    @WithMockUser(roles = {AUTHOR})
    void shouldReturnForbiddenWhenDeleteUserFromOrganisationWithInvalidRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{organisationId}/user/{username}", 1L, AUTHOR_USERNAME).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private Organisation populateOrganisation(){
        Organisation organisation = new Organisation().setName("Test Organisation").setDescription("Simple Description");
        return organisationRepository.saveAndFlush(organisation);
    }

    private PortalUser populatePortalUser(Long organisationId, String username) {
        PortalUser portalUser = new PortalUser();
        portalUser.setUsername(username);
        portalUser.setEmail(EMAIL);
        portalUser.setOrganisations(new HashSet<>(Collections.singleton(new Organisation().setId(organisationId))));
        return portalUserRepository.saveAndFlush(portalUser);
    }

    private UserRepresentation getStubUserRepresentation(PortalUser user){
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(user.getId().toString());
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        return userRepresentation;
    }


}
