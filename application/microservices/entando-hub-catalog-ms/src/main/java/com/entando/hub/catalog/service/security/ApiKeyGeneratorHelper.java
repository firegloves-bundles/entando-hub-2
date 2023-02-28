package com.entando.hub.catalog.service.security;

import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.UUID;

@Service
public class ApiKeyGeneratorHelper {
    public String generateApiKey() {
        return UUID.randomUUID().toString();
    }

    public String toSha(String text) {
        return DigestUtils.sha3_512Hex(text);
    }
}
