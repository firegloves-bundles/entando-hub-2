package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.PrivateCatalogApiKeyRepository;
import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.rest.PagedContent;
import com.entando.hub.catalog.rest.dto.apikey.GetApiKeyResponseDTO;
import com.entando.hub.catalog.rest.exceptions.BadRequestException;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapper;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapperImpl;
import com.entando.hub.catalog.service.security.ApiKeyGeneratorHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;

import java.util.*;

import static com.entando.hub.catalog.service.helpers.PrivateCatalogApiKeyTestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
@ComponentScan(basePackageClasses = {PrivateCatalogApiKeyMapper.class, PrivateCatalogApiKeyMapperImpl.class})
public class PrivateCatalogApiKeyServiceTest {
    @InjectMocks
    PrivateCatalogApiKeyService privateCatalogApiKeyService;
    @Mock
    PrivateCatalogApiKeyRepository privateCatalogApiKeyRepository;
    @Mock
    private ApiKeyGeneratorHelper apiKeyGeneratorHelper;
    @Spy
    private static PrivateCatalogApiKeyMapper mapper = new PrivateCatalogApiKeyMapperImpl();

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
    public void getAllApiKeysByUsernameTest() {
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
    public void editLabelTest() {
        //Edit an api key that exists should return true as result
        PrivateCatalogApiKey privateCatalogApiKey = createPrivateCatalogApiKey1();
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Boolean result = privateCatalogApiKeyService.editLabel(API_KEY_ID, GENERATED_USERNAME, GENERATED_LABEL);
        assertNotNull(result);
        assertEquals(true, result);
    }

    @Test
    public void editLabelNotExistTest() {
        //Edit an api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            privateCatalogApiKeyService.editLabel(API_KEY_ID, GENERATED_USERNAME, GENERATED_LABEL);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    public void deleteApiKeyTest() {
        //Delete an api key that exists should return true as result
        PrivateCatalogApiKey privateCatalogApiKey = createPrivateCatalogApiKey1();
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Boolean result = privateCatalogApiKeyService.deleteApiKey(API_KEY_ID, GENERATED_USERNAME);
        assertNotNull(result);
        assertEquals(true, result);
    }

    @Test
    public void deleteApiKeyNotExistTest() {
        //Delete an Api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            privateCatalogApiKeyService.deleteApiKey(API_KEY_ID, GENERATED_USERNAME);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    public void regenerateApiKeyTest() {
        //Regenerate an api key that exists should return a String as result
        PrivateCatalogApiKey privateCatalogApiKey = generatePrivateCatalogApiKeyEntity(API_KEY_ID,PORTAL_USER_ID );
        Mockito.when(this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Mockito.when(this.apiKeyGeneratorHelper.generateApiKey()).thenReturn(API_KEY);
        Mockito.when(this.apiKeyGeneratorHelper.toSha(eq(API_KEY))).thenReturn(API_KEY_SHA);
        String result = privateCatalogApiKeyService.regenerateApiKey(API_KEY_ID, GENERATED_USERNAME);
        assertNotNull(result);
    }

    @Test
    public void regenerateMyApiKeyNotExistTest() {
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

}