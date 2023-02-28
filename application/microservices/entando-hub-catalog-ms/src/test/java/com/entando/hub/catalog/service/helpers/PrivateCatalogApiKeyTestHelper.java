package com.entando.hub.catalog.service.helpers;

import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;

import java.util.HashSet;
import java.util.Set;


public class PrivateCatalogApiKeyTestHelper {
    public static String PORTAL_USER_EMAIL = "email";
    public static String PORTAL_USER_USERNAME = "username";
    public static String API_KEY = "api-key";
    public static String LABEL = "Test label";

    public static PortalUser generatePortalUserEntity(Long id) {
        Set<Organisation> organisations = new HashSet<>();
        PortalUser portalUser = new PortalUser();
        portalUser.setId(id);
        portalUser.setUsername(id + PORTAL_USER_USERNAME);
        portalUser.setEmail(id + PORTAL_USER_EMAIL);
        portalUser.setOrganisations(organisations);
        return portalUser;
    }

    public static PrivateCatalogApiKey generatePrivateCatalogApiKeyEntity(Long apiKeyId, Long portalUserId) {
        PortalUser portalUser = generatePortalUserEntity(portalUserId);
        PrivateCatalogApiKey privateCatalogApiKey = new PrivateCatalogApiKey();
        privateCatalogApiKey.setId(apiKeyId);
        privateCatalogApiKey.setPortalUser(portalUser);
        privateCatalogApiKey.setApiKey(apiKeyId + API_KEY);
        privateCatalogApiKey.setLabel(apiKeyId + LABEL);
        return privateCatalogApiKey;
    }

}
