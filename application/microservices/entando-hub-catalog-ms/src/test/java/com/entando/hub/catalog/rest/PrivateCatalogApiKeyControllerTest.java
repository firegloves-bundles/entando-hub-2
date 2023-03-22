package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import com.entando.hub.catalog.service.dto.apikey.AddApiKeyRequestDTO;
import com.entando.hub.catalog.service.dto.apikey.ApiKeyResponseDTO;
import com.entando.hub.catalog.service.dto.apikey.EditApiKeyRequestDTO;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapper;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapperImpl;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static com.entando.hub.catalog.rest.CategoryControllerTest.asJsonString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackageClasses = {PrivateCatalogApiKeyMapper.class, PrivateCatalogApiKeyMapperImpl.class})
class PrivateCatalogApiKeyControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PrivateCatalogApiKeyMapper privateCatalogApiKeyMapper;
    @InjectMocks
    PrivateCatalogApiKeyController privateCatalogApiKeyController;
    @MockBean
    PrivateCatalogApiKeyService privateCatalogApiKeyService;
    @MockBean
    SecurityHelperService securityHelperService;

    private final Long ID = 1001L;
    private final String ADMIN_USERNAME = "Admin";
    private final String LABEL = "Test label";
    private final Long ADMIN_API_KEY_ID = 2000l;
    private final String API_KEY = "api-key";
    private final String ADMIN_API_KEY = "admin-api-key";
    private final Integer PAGE = 1;
    private final Integer PAGE_SIZE = 25;
    private final String URI = "/api/private-catalog-api-key/";
    private final String ID_URI = URI + "{id}";
    private final String REGENERATE_MY_API_KEY_URI = URI + "regenerate/{id}";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {ADMIN})
    void testGetMyApiKeys() throws Exception {
        PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> apiKeyList = getListApiKeyResponseDTO(createPrivateCatalogApiKey());
        Mockito.when(securityHelperService.getContextAuthenticationUsername()).thenReturn(ADMIN_USERNAME);
        Mockito.when(privateCatalogApiKeyService.getApiKeysByUsername(ADMIN_USERNAME, PAGE, PAGE_SIZE)).thenReturn(apiKeyList);
        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", PAGE.toString())
                        .param("pageSize", PAGE_SIZE.toString())).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.payload.[0].id").value(ADMIN_API_KEY_ID))
                .andExpect(jsonPath("$.payload.[0].label").value(LABEL))
                .andExpect(jsonPath("$.metadata.page").value(1))
                .andExpect(jsonPath("$.metadata.pageSize").value(1))
                .andExpect(jsonPath("$.metadata.lastPage").value(1))
                .andExpect(jsonPath("$.metadata.totalItems").value(1));
    }


    @Test
    @WithMockUser(username = "admin", roles = {ADMIN})
    void TestDeleteMyApiKey() throws Exception {
        Mockito.when(privateCatalogApiKeyService.deleteApiKey(ADMIN_API_KEY_ID, ADMIN_USERNAME)).thenReturn(Boolean.TRUE);
        mockMvc.perform(MockMvcRequestBuilders.delete(ID_URI, ADMIN_API_KEY_ID)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {ADMIN})
    void TestEditMyLabel() throws Exception {
        EditApiKeyRequestDTO request = new EditApiKeyRequestDTO();
        request.setLabel(LABEL);
        Mockito.when(securityHelperService.getContextAuthenticationUsername()).thenReturn(ADMIN_USERNAME);
        Mockito.when(privateCatalogApiKeyService.editLabel(ADMIN_API_KEY_ID, ADMIN_USERNAME, LABEL)).thenReturn(Boolean.TRUE);
        mockMvc.perform(MockMvcRequestBuilders.put(ID_URI, ADMIN_API_KEY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding("UTF-8")
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {ADMIN})
    void TestRegenerateMyApiKey() throws Exception {
        EditApiKeyRequestDTO request = new EditApiKeyRequestDTO();
        request.setLabel(LABEL);
        ApiKeyResponseDTO response = new ApiKeyResponseDTO();
        response.setApiKey(API_KEY);
        response.setId(ADMIN_API_KEY_ID);
        Mockito.when(securityHelperService.getContextAuthenticationUsername()).thenReturn(ADMIN_USERNAME);
        Mockito.when(privateCatalogApiKeyService.regenerateApiKey(ADMIN_API_KEY_ID, ADMIN_USERNAME)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post(REGENERATE_MY_API_KEY_URI, ADMIN_API_KEY_ID)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("apiKey").value(API_KEY));
    }

    @Test
    @WithMockUser(username = "admin", roles = {ADMIN})
    void TestAddMyApiKey() throws Exception {
        AddApiKeyRequestDTO request = new AddApiKeyRequestDTO();
        request.setLabel(LABEL);
        ApiKeyResponseDTO response = new ApiKeyResponseDTO();
        response.setApiKey(API_KEY);
        response.setId(ADMIN_API_KEY_ID);
        Mockito.when(securityHelperService.getContextAuthenticationUsername()).thenReturn(ADMIN_USERNAME);
        Mockito.when(privateCatalogApiKeyService.addApiKey(ADMIN_USERNAME, LABEL)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding("UTF-8")
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("apiKey").value(API_KEY));
        ;
    }

    private PrivateCatalogApiKey createPrivateCatalogApiKey() {
        PortalUser portalUser = new PortalUser();
        portalUser.setId(ID);
        portalUser.setUsername(ADMIN_USERNAME);
        PrivateCatalogApiKey privateCatalogApiKey = new PrivateCatalogApiKey();
        privateCatalogApiKey.setPortalUser(portalUser);
        privateCatalogApiKey.setId(ADMIN_API_KEY_ID);
        privateCatalogApiKey.setApiKey(ADMIN_API_KEY);
        privateCatalogApiKey.setLabel(LABEL);
        return privateCatalogApiKey;
    }

    private PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> getListApiKeyResponseDTO(PrivateCatalogApiKey apiKey) {
        List<PrivateCatalogApiKey> responseList = new ArrayList<>();
        List<ApiKeyResponseDTO> responseDTOList = new ArrayList<>();
        PrivateCatalogApiKey privateCatalogApiKey = apiKey;
        responseList.add(privateCatalogApiKey);
        Page<PrivateCatalogApiKey> pageObj = new PageImpl<>(responseList);

        ApiKeyResponseDTO responseDTO1 = privateCatalogApiKeyMapper.toApiKeyResponseDTO(privateCatalogApiKey);
        responseDTOList.add(responseDTO1);

        PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> apiKeyList =
                new PagedContent<>(responseDTOList, pageObj);
        return apiKeyList;
    }

}
