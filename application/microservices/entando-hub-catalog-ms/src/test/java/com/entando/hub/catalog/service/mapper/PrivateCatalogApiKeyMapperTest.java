package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.service.dto.apikey.ApiKeyResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.entando.hub.catalog.service.PrivateCatalogApiKeyGeneratorHelper.generatePrivateCatalogApiKeyEntity;
import static junit.framework.TestCase.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PrivateCatalogApiKeyMapperTest {

    private PrivateCatalogApiKeyMapper privateCatalogApiKeyMapper = Mappers.getMapper(PrivateCatalogApiKeyMapper.class);
    static final Long PORTAL_USER_ID_1 = 1001L;
    final static Long PORTAL_USER_ID_2 = 2002L;
    final static Long API_KEY_ID_1 = 8080L;
    final static Long API_KEY_ID_2 = 9000L;
    private static String PORTAL_USER_USERNAME = "username";
    public static String LABEL = "Test label";
    private static String GENERATED_USERNAME_1 = PORTAL_USER_ID_1 + PORTAL_USER_USERNAME;
    private static String GENERATED_USERNAME_2 = PORTAL_USER_ID_2 + PORTAL_USER_USERNAME;
    private static String GENERATED_LABEL_1 = API_KEY_ID_1 + LABEL;
    private static String GENERATED_LABEL_2 = API_KEY_ID_2 + LABEL;

    @Test
    void testToDto() {
        PrivateCatalogApiKey entity = generatePrivateCatalogApiKeyEntity(API_KEY_ID_1, PORTAL_USER_ID_1);
        ApiKeyResponseDTO dto = privateCatalogApiKeyMapper.toApiKeyResponseDTO(entity);
        assertNotNull(dto);
        assertEquals(GENERATED_LABEL_1, dto.getLabel());
        assertNotNull(dto.getId());
    }

    @Test
    void testToDtoNull() {
        ApiKeyResponseDTO dto = privateCatalogApiKeyMapper.toApiKeyResponseDTO((PrivateCatalogApiKey) null);
        assertNull(dto);
    }

    @Test
    void testListToDto() {
        PrivateCatalogApiKey entity1 = generatePrivateCatalogApiKeyEntity(API_KEY_ID_1, PORTAL_USER_ID_1);
        PrivateCatalogApiKey entity2 = generatePrivateCatalogApiKeyEntity(API_KEY_ID_2, PORTAL_USER_ID_2);
        List<PrivateCatalogApiKey> list = new ArrayList<>();
        list.add(entity1);
        list.add(entity2);
        List<ApiKeyResponseDTO> dto = privateCatalogApiKeyMapper.toApiKeyResponseDTO(list);
        assertNotNull(dto);
        assertEquals(2, dto.size());
        assertEquals(API_KEY_ID_1, dto.get(0).getId());
        assertEquals(API_KEY_ID_2, dto.get(1).getId());
        assertEquals(GENERATED_LABEL_1, dto.get(0).getLabel());
        assertEquals(GENERATED_LABEL_2, dto.get(1).getLabel());
    }

    @Test
    void testListToDtoNull() {
        List<ApiKeyResponseDTO> dto = privateCatalogApiKeyMapper.toApiKeyResponseDTO((List<PrivateCatalogApiKey>) null);
        assertNull(dto);
    }

}
