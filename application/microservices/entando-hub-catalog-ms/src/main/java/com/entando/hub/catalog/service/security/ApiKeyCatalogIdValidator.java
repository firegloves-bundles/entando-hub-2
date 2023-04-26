package com.entando.hub.catalog.service.security;

import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.service.CatalogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyCatalogIdValidator {

    private final CatalogService catalogService;

    public ApiKeyCatalogIdValidator(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public boolean validateApiKeyCatalogId(String apiKey, Long catalogId) {
        if ((StringUtils.isEmpty(apiKey) && null != catalogId) || (StringUtils.isNotEmpty(apiKey) && null == catalogId)){
            return false;
        } else if (StringUtils.isNotEmpty(apiKey)) {
            Catalog userCatalog = catalogService.getCatalogByApiKey(apiKey);
            Long userCatalogId = null;
            if (null != userCatalog) {
                userCatalogId = userCatalog.getId();
            }
            return userCatalogId == null || userCatalogId.equals(catalogId);
        } else {
            return true;
        }
    }
}
