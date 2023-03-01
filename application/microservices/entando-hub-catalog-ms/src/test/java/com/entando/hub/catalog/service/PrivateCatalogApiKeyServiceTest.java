package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.PrivateCatalogApiKeyRepository;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.rest.PagedContent;
import com.entando.hub.catalog.rest.dto.apikey.GetApiKeyResponseDTO;
import com.entando.hub.catalog.rest.exceptions.BadRequestException;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapper;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapperImpl;
import com.entando.hub.catalog.service.security.ApiKeyGeneratorHelper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.entando.hub.catalog.service.PrivateCatalogApiKeyGeneratorHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
@ComponentScan(basePackageClasses = {PrivateCatalogApiKeyMapper.class, PrivateCatalogApiKeyMapperImpl.class})
public class PrivateCatalogApiKeyServiceTest {
    PrivateCatalogApiKeyService privateCatalogApiKeyService;
    @Mock
    PrivateCatalogApiKeyRepository privateCatalogApiKeyRepository;
    @Mock
    private ApiKeyGeneratorHelper apiKeyGeneratorHelper;
    @Mock
    PortalUserRepository portalUserRepository;

    @Spy
    private static PrivateCatalogApiKeyMapper privateCatalogApiKeyMapper = new PrivateCatalogApiKeyMapperImpl();

    @BeforeEach
    void setUp() {
        this.privateCatalogApiKeyService = new PrivateCatalogApiKeyService(privateCatalogApiKeyRepository,
                portalUserRepository,
                apiKeyGeneratorHelper,
                privateCatalogApiKeyMapper);
    }

    private final Integer PAGE = 1;
    private final Integer PAGE_SIZE = 25;
    private final static Long API_KEY_ID = 1000L;
    private final static Long PORTAL_USER_ID = 2000L;
    public static String API_KEY = "api-key";
    private final static String GENERATED_USERNAME = PORTAL_USER_ID + PORTAL_USER_USERNAME;
    private final static String API_KEY_SHA = "api-key-sha";
    private final static String GENERATED_LABEL = API_KEY_ID + LABEL;
    private final static Long API_KEY_ID_2 = 1200L;
    private final static Long PORTAL_USER_ID_2 = 2200L;

    @Test
    void getAllApiKeysByUsernameTest() {
        List<PrivateCatalogApiKey> apiKeyList = new ArrayList<>();
        PrivateCatalogApiKey privateCatalogApiKey1 = createPrivateCatalogApiKey1();
        PrivateCatalogApiKey privateCatalogApiKey2 = createPrivateCatalogApiKey2();
        apiKeyList.add(privateCatalogApiKey1);
        apiKeyList.add(privateCatalogApiKey2);
        Page<PrivateCatalogApiKey> response = new PageImpl<>(apiKeyList);
        Mockito.when(privateCatalogApiKeyRepository.findByPortalUserUsername(any(Pageable.class), eq(GENERATED_USERNAME))).thenReturn(response);
        PagedContent<GetApiKeyResponseDTO, PrivateCatalogApiKey> apiKeys = privateCatalogApiKeyService.getApiKeysByUsername(GENERATED_USERNAME,PAGE, PAGE_SIZE);
        assertNotNull(apiKeys);
        assertEquals(API_KEY_ID, apiKeys.getPayload().get(0).getId());
        assertEquals(GENERATED_USERNAME, apiKeys.getPayload().get(0).getUsername());
        assertEquals(GENERATED_LABEL, apiKeys.getPayload().get(0).getLabel());
    }
    @Test
    void getAllApiKeysByUsernameUnpagedTest() {
        List<PrivateCatalogApiKey> apiKeyList = new ArrayList<>();
        PrivateCatalogApiKey privateCatalogApiKey1 = createPrivateCatalogApiKey1();
        PrivateCatalogApiKey privateCatalogApiKey2 = createPrivateCatalogApiKey2();
        apiKeyList.add(privateCatalogApiKey1);
        apiKeyList.add(privateCatalogApiKey2);
        Page<PrivateCatalogApiKey> response = new PageImpl<>(apiKeyList);
        Mockito.when(privateCatalogApiKeyRepository.findByPortalUserUsername(any(Pageable.class), eq(GENERATED_USERNAME))).thenReturn(response);
        PagedContent<GetApiKeyResponseDTO, PrivateCatalogApiKey> apiKeys = privateCatalogApiKeyService.getApiKeysByUsername(GENERATED_USERNAME,PAGE, 0);
        assertNotNull(apiKeys);
        assertEquals(API_KEY_ID, apiKeys.getPayload().get(0).getId());
        assertEquals(GENERATED_USERNAME, apiKeys.getPayload().get(0).getUsername());
        assertEquals(GENERATED_LABEL, apiKeys.getPayload().get(0).getLabel());
    }

    @Test
    void addApiKeyTest() {
        //Add an api key that exists should return a string as result
        PortalUser portalUser = createPortalUser();
        Mockito.when(this.portalUserRepository.findByUsername(GENERATED_USERNAME)).thenReturn(portalUser);
        Mockito.when(apiKeyGeneratorHelper.generateApiKey()).thenReturn(API_KEY);
        String result = privateCatalogApiKeyService.addApiKey(GENERATED_USERNAME, GENERATED_LABEL);
        assertNotNull(result);
    }

    @Test
    void addApiKeyPortalUserNotExistTest() {
        Mockito.when(this.portalUserRepository.findByUsername(GENERATED_USERNAME)).thenReturn(null);
        try {
            privateCatalogApiKeyService.addApiKey(GENERATED_USERNAME, GENERATED_LABEL);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    void editLabelTest() {
        //Edit an api key that exists should return true as result
        PrivateCatalogApiKey privateCatalogApiKey = createPrivateCatalogApiKey1();
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Boolean result = privateCatalogApiKeyService.editLabel(API_KEY_ID, GENERATED_USERNAME, GENERATED_LABEL);
        assertNotNull(result);
        assertEquals(true, result);
    }

    @Test
    void editLabelNotExistTest() {
        //Edit an api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            privateCatalogApiKeyService.editLabel(API_KEY_ID, GENERATED_USERNAME, GENERATED_LABEL);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    void deleteApiKeyTest() {
        //Delete an api key that exists should return true as result
        PrivateCatalogApiKey privateCatalogApiKey = createPrivateCatalogApiKey1();
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Boolean result = privateCatalogApiKeyService.deleteApiKey(API_KEY_ID, GENERATED_USERNAME);
        assertNotNull(result);
        assertEquals(true, result);
    }

    @Test
    void deleteApiKeyNotExistTest() {
        //Delete an Api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            privateCatalogApiKeyService.deleteApiKey(API_KEY_ID, GENERATED_USERNAME);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    void regenerateApiKeyTest() {
        //Regenerate an api key that exists should return a String as result
        PrivateCatalogApiKey privateCatalogApiKey = generatePrivateCatalogApiKeyEntity(API_KEY_ID,PORTAL_USER_ID );
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Mockito.when(this.apiKeyGeneratorHelper.generateApiKey()).thenReturn(API_KEY);
        Mockito.when(this.apiKeyGeneratorHelper.toSha(API_KEY)).thenReturn(API_KEY_SHA);
        String result = privateCatalogApiKeyService.regenerateApiKey(API_KEY_ID, GENERATED_USERNAME);
        assertNotNull(result);
    }

    @Test
    void regenerateMyApiKeyNotExistTest() {
        //Regenerate an api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            privateCatalogApiKeyService.regenerateApiKey(API_KEY_ID, GENERATED_USERNAME);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }

    private PrivateCatalogApiKey createPrivateCatalogApiKey1() {
        return generatePrivateCatalogApiKeyEntity(API_KEY_ID,PORTAL_USER_ID);
    }
    private PrivateCatalogApiKey createPrivateCatalogApiKey2() {
        return generatePrivateCatalogApiKeyEntity(API_KEY_ID_2,PORTAL_USER_ID_2);
    }
    private PortalUser createPortalUser() {
        return generatePortalUserEntity(PORTAL_USER_ID);
    }
}
