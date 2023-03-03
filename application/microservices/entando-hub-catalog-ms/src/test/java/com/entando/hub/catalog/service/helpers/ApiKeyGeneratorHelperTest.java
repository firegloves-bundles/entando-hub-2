package com.entando.hub.catalog.service.helpers;

import com.entando.hub.catalog.service.security.ApiKeyGeneratorHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ApiKeyGeneratorHelperTest {

    @Autowired
    private ApiKeyGeneratorHelper apiKeyGeneratorHelper;

    @Test
    void testApiKeyGeneration() {
        String apiKey = this.apiKeyGeneratorHelper.generateApiKey();
        Assertions.assertEquals(UUID.fromString(apiKey).toString(), apiKey);
    }

    @Test
    void testApiKeyGenerationSha() {
        String apiKey = this.apiKeyGeneratorHelper.generateApiKey();
        String validSha = this.apiKeyGeneratorHelper.toSha(apiKey);
        Assertions.assertEquals(DigestUtils.sha3_512Hex(apiKey).toString(), validSha);
    }
}
