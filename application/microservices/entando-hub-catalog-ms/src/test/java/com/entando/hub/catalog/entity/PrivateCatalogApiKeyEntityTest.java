package com.entando.hub.catalog.entity;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.entando.hub.catalog.service.PrivateCatalogApiKeyGeneratorHelper.generatePrivateCatalogApiKeyEntity;

class PrivateCatalogApiKeyEntityTest {
    private Long API_KEY_ID =1000L;
    private Long PORTAL_USER_ID= 2000L;

    @Test
    void testToString(){
        PrivateCatalogApiKey privateCatalogApiKey = generatePrivateCatalogApiKeyEntity(API_KEY_ID, PORTAL_USER_ID);
        String toString = privateCatalogApiKey.toString();
        Assertions.assertTrue(toString.contains("id=" + privateCatalogApiKey.getId()));
        Assertions.assertTrue(toString.contains("username='" + privateCatalogApiKey.getPortalUser().getUsername()));
        Assertions.assertTrue(toString.contains("label='" + privateCatalogApiKey.getLabel()));
        Assertions.assertTrue(toString.contains("creationDate='" + privateCatalogApiKey.getCreationDate()));
        Assertions.assertTrue(toString.contains("lastUpdateDate='" + privateCatalogApiKey.getLastUpdateDate()));
    }
}
