package com.entando.hub.catalog.service;

import static com.entando.hub.catalog.service.PrivateCatalogApiKeyGeneratorHelper.LABEL;
import static com.entando.hub.catalog.service.PrivateCatalogApiKeyGeneratorHelper.PORTAL_USER_USERNAME;
import static com.entando.hub.catalog.service.PrivateCatalogApiKeyGeneratorHelper.generatePortalUserEntity;
import static com.entando.hub.catalog.service.PrivateCatalogApiKeyGeneratorHelper.generatePrivateCatalogApiKeyEntity;
import static com.entando.hub.catalog.service.PrivateCatalogApiKeyService.getPageable;
import static org.mockito.ArgumentMatchers.any;

import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.PrivateCatalogApiKeyRepository;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.rest.PagedContent;
import com.entando.hub.catalog.service.dto.apikey.ApiKeyResponseDTO;
import com.entando.hub.catalog.service.exception.BadRequestException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapper;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapperImpl;
import com.entando.hub.catalog.service.security.ApiKeyGeneratorHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
        this.privateCatalogApiKeyService = new PrivateCatalogApiKeyService(this.privateCatalogApiKeyRepository,
                this.portalUserRepository,
                this.apiKeyGeneratorHelper,
                this.privateCatalogApiKeyMapper);
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
        Pageable paging = getPageable(PAGE_SIZE, PAGE - 1);
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKeys(GENERATED_USERNAME, paging)).thenReturn(response);
        PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> apiKeys = this.privateCatalogApiKeyService.getApiKeysByUsername(GENERATED_USERNAME, PAGE, PAGE_SIZE);
        Assertions.assertNotNull(apiKeys);
        Assertions.assertEquals(API_KEY_ID, apiKeys.getPayload().get(0).getId());
        Assertions.assertEquals(GENERATED_LABEL, apiKeys.getPayload().get(0).getLabel());
    }
    @Test
    void getAllApiKeysByUsernameUnpagedTest() {
        List<PrivateCatalogApiKey> apiKeyList = new ArrayList<>();
        PrivateCatalogApiKey privateCatalogApiKey1 = createPrivateCatalogApiKey1();
        PrivateCatalogApiKey privateCatalogApiKey2 = createPrivateCatalogApiKey2();
        apiKeyList.add(privateCatalogApiKey1);
        apiKeyList.add(privateCatalogApiKey2);
        Page<PrivateCatalogApiKey> response = new PageImpl<>(apiKeyList);
        Pageable paging = getPageable(0, PAGE-1);
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKeys(GENERATED_USERNAME, paging)).thenReturn(response);
        PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> apiKeys = this.privateCatalogApiKeyService.getApiKeysByUsername(GENERATED_USERNAME,PAGE, 0);
        Assertions.assertNotNull(apiKeys);
        Assertions.assertEquals(API_KEY_ID, apiKeys.getPayload().get(0).getId());
        Assertions.assertEquals(GENERATED_LABEL, apiKeys.getPayload().get(0).getLabel());
    }

    @Test
    void addApiKeyTest() {
        //Add an api key that exists should return a string as result
        PortalUser portalUser = createPortalUser();
        PrivateCatalogApiKey privateCatalogApiKey= createPrivateCatalogApiKey1();
        Mockito.when(this.portalUserRepository.findByUsername(GENERATED_USERNAME)).thenReturn(portalUser);
        Mockito.when(this.privateCatalogApiKeyRepository.save(any())).thenReturn(privateCatalogApiKey);
        Mockito.when(this.apiKeyGeneratorHelper.generateApiKey()).thenReturn(API_KEY);
        Mockito.when(this.apiKeyGeneratorHelper.toSha(API_KEY)).thenReturn(API_KEY_SHA);
        ApiKeyResponseDTO apiKeyResponseDTO = this.privateCatalogApiKeyService.addApiKey(GENERATED_USERNAME, GENERATED_LABEL);
        Assertions.assertNotNull(apiKeyResponseDTO);
        Assertions.assertNotNull(apiKeyResponseDTO.getId());
        Assertions.assertNotNull(apiKeyResponseDTO.getApiKey());
    }

    @Test
    void addApiKeyPortalUserNotExistTest() {
        Mockito.when(this.portalUserRepository.findByUsername(GENERATED_USERNAME)).thenReturn(null);
        try {
            privateCatalogApiKeyService.addApiKey(GENERATED_USERNAME, GENERATED_LABEL);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    void editLabelTest() {
        //Edit an api key that exists should return true as result
        PrivateCatalogApiKey privateCatalogApiKey = createPrivateCatalogApiKey1();
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Boolean result = this.privateCatalogApiKeyService.editLabel(API_KEY_ID, GENERATED_USERNAME, GENERATED_LABEL);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(true, result);
    }

    @Test
    void editLabelNotExistTest() {
        //Edit an api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            privateCatalogApiKeyService.editLabel(API_KEY_ID, GENERATED_USERNAME, GENERATED_LABEL);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    void deleteApiKeyTest() {
        //Delete an api key that exists should return true as result
        PrivateCatalogApiKey privateCatalogApiKey = createPrivateCatalogApiKey1();
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Boolean result = this.privateCatalogApiKeyService.deleteApiKey(API_KEY_ID, GENERATED_USERNAME);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(true, result);
    }

    @Test
    void deleteApiKeyNotExistTest() {
        //Delete an Api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            this.privateCatalogApiKeyService.deleteApiKey(API_KEY_ID, GENERATED_USERNAME);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }

    @Test
    void regenerateApiKeyTest() {
        //Regenerate an api key that exists should return a String as result
        PrivateCatalogApiKey privateCatalogApiKey = generatePrivateCatalogApiKeyEntity(API_KEY_ID,PORTAL_USER_ID );
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.of(privateCatalogApiKey));
        Mockito.when(this.privateCatalogApiKeyRepository.save(any())).thenReturn(privateCatalogApiKey);
        Mockito.when(this.apiKeyGeneratorHelper.generateApiKey()).thenReturn(API_KEY);
        Mockito.when(this.apiKeyGeneratorHelper.toSha(API_KEY)).thenReturn(API_KEY_SHA);
        ApiKeyResponseDTO apiKeyResponseDTO = this.privateCatalogApiKeyService.regenerateApiKey(API_KEY_ID, GENERATED_USERNAME);
        Assertions.assertNotNull(apiKeyResponseDTO);
        Assertions.assertNotNull(apiKeyResponseDTO.getApiKey());
    }

    @Test
    void regenerateMyApiKeyNotExistTest() {
        //Regenerate an api key that don't exist should throw a BadRequestException
        Mockito.when(this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(API_KEY_ID, GENERATED_USERNAME)).thenReturn(Optional.empty());
        try {
            this.privateCatalogApiKeyService.regenerateApiKey(API_KEY_ID, GENERATED_USERNAME);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof BadRequestException);
        }
    }
    @Test
    void getUsernameByApiKeyTest(){
        PrivateCatalogApiKey privateCatalogApiKey1 = createPrivateCatalogApiKey1();
        Mockito.when(this.privateCatalogApiKeyRepository.findByApiKey(apiKeyGeneratorHelper.toSha(API_KEY))).thenReturn(Optional.of(privateCatalogApiKey1));
        String usernameByApiKey = this.privateCatalogApiKeyService.getUsernameByApiKey(API_KEY);
        Assertions.assertNotNull(usernameByApiKey);
    }

    @Test
    void getUsernameByInvalidApiKeyTest(){
        Mockito.when(this.privateCatalogApiKeyRepository.findByApiKey(apiKeyGeneratorHelper.toSha(API_KEY))).thenReturn(Optional.empty());
        try {
            this.privateCatalogApiKeyService.getUsernameByApiKey(API_KEY);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NotFoundException);
        }
    }

    @Test
    void doesApiKeyExistsTest(){
        Mockito.when(this.apiKeyGeneratorHelper.toSha(API_KEY)).thenReturn(API_KEY_SHA);
        Mockito.when(this.privateCatalogApiKeyRepository.existsByApiKey(API_KEY_SHA)).thenReturn(true);
        boolean apiKeyExist = this.privateCatalogApiKeyService.doesApiKeyExist(API_KEY);
        Assert.assertTrue(apiKeyExist);
    }

    @Test
    void doesApiKeyNotExistsTest(){
        Mockito.when(this.apiKeyGeneratorHelper.toSha(API_KEY)).thenReturn(API_KEY_SHA);
        Mockito.when(this.privateCatalogApiKeyRepository.existsByApiKey(API_KEY_SHA)).thenReturn(false);
        boolean apiKeyExist = this.privateCatalogApiKeyService.doesApiKeyExist(API_KEY);
        Assert.assertFalse(apiKeyExist);
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
