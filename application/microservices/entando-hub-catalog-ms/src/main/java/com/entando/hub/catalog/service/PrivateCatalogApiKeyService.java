package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.PrivateCatalogApiKeyRepository;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.rest.PagedContent;
import com.entando.hub.catalog.service.dto.apikey.ApiKeyResponseDTO;
import com.entando.hub.catalog.service.exception.BadRequestException;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapper;
import com.entando.hub.catalog.service.security.ApiKeyGeneratorHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrivateCatalogApiKeyService {
    private final PrivateCatalogApiKeyRepository privateCatalogApiKeyRepository;
    private final PortalUserRepository portalUserRepository;
    private final ApiKeyGeneratorHelper apiKeyGeneratorHelper;
    private final PrivateCatalogApiKeyMapper privateCatalogApiKeyMapper;
    public static final String API_KEY_MSG = "Api key ";
    private static final String NOT_FOUND_MSG = " not found for user ";
    public PrivateCatalogApiKeyService(PrivateCatalogApiKeyRepository privateCatalogApiKeyRepository,
                                       PortalUserRepository portalUserRepository,
                                       ApiKeyGeneratorHelper apiKeyGeneratorHelper,
                                       PrivateCatalogApiKeyMapper privateCatalogApiKeyMapper) {
        this.privateCatalogApiKeyRepository = privateCatalogApiKeyRepository;
        this.portalUserRepository = portalUserRepository;
        this.apiKeyGeneratorHelper = apiKeyGeneratorHelper;
        this.privateCatalogApiKeyMapper = privateCatalogApiKeyMapper;
    }
    public ApiKeyResponseDTO addApiKey(String username, String label) {
        PortalUser portalUser = this.portalUserRepository.findByUsername(username);
        if (portalUser == null) {
            String errorMessage = "User "+ username +" not found";
            throw new BadRequestException(errorMessage);
        }
        PrivateCatalogApiKey privateCatalogApiKey = new PrivateCatalogApiKey();
        privateCatalogApiKey.setPortalUser(portalUser);
        privateCatalogApiKey.setLabel(label);
        String generatedApiKey = apiKeyGeneratorHelper.generateApiKey();
        String apiKeySha = apiKeyGeneratorHelper.toSha(generatedApiKey);
        privateCatalogApiKey.setApiKey(apiKeySha);
        PrivateCatalogApiKey apiKeyResult = this.privateCatalogApiKeyRepository.save(privateCatalogApiKey);
        apiKeyResult.setApiKey(generatedApiKey);
        return privateCatalogApiKeyMapper.toAddApiKeyDto(apiKeyResult);
    }
    public boolean editLabel(long id, String username, String label) {
        Optional<PrivateCatalogApiKey> apiKeyOptional = this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(id, username);
        if (apiKeyOptional.isPresent()) {
            PrivateCatalogApiKey privateCatalogApiKey = apiKeyOptional.get();
            privateCatalogApiKey.setLabel(label);
            this.privateCatalogApiKeyRepository.save(privateCatalogApiKey);
            return true;
        } else {
            String errorMessage = API_KEY_MSG + id + NOT_FOUND_MSG + username;
            throw new BadRequestException(errorMessage);
        }
    }

    public ApiKeyResponseDTO regenerateApiKey(long id, String username) {
        Optional<PrivateCatalogApiKey> apiKeyOptional = this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(id, username);
        if (apiKeyOptional.isPresent()) {
            PrivateCatalogApiKey privateCatalogApiKey = apiKeyOptional.get();
            String generatedApiKey = apiKeyGeneratorHelper.generateApiKey();
            String apiKeySha = apiKeyGeneratorHelper.toSha(generatedApiKey);
            privateCatalogApiKey.setApiKey(apiKeySha);
            PrivateCatalogApiKey apiKeyResult = this.privateCatalogApiKeyRepository.save(privateCatalogApiKey);
            ApiKeyResponseDTO apiKeyResponseDTO = this.privateCatalogApiKeyMapper.toRefreshApiKeyDto(apiKeyResult);
            apiKeyResponseDTO.setApiKey(generatedApiKey);
            return apiKeyResponseDTO;
        } else {
            String errorMessage = API_KEY_MSG + id + NOT_FOUND_MSG + username;
            throw new BadRequestException(errorMessage);
        }
    }

    public boolean deleteApiKey(long id, String username) {
        Optional<PrivateCatalogApiKey> apiKeyOptional = this.privateCatalogApiKeyRepository.getPrivateCatalogApiKey(id, username);
        if (apiKeyOptional.isPresent()) {
            privateCatalogApiKeyRepository.delete(apiKeyOptional.get());
            return true;
        } else {
            String errorMessage = API_KEY_MSG + id + NOT_FOUND_MSG + username;
            throw new BadRequestException(errorMessage);
        }
    }

    public PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> getApiKeysByUsername(String username, Integer pageNum, Integer pageSize) {
        Pageable paging = getPageable(pageSize, pageNum - 1);
        Page<PrivateCatalogApiKey> apiKeys = this.privateCatalogApiKeyRepository.getPrivateCatalogApiKeys(username, paging);
        List<PrivateCatalogApiKey> content = apiKeys.getContent();
        List<ApiKeyResponseDTO> response = this.privateCatalogApiKeyMapper.toApiKeyResponseDTO(content);
        return new PagedContent<>( this.privateCatalogApiKeyMapper.removeApiKey(response), apiKeys);
    }

    public static Pageable getPageable(Integer pageSize, int pageNum) {
        Pageable paging;
        if (pageSize == 0) {
            paging = Pageable.unpaged();
        } else {
            paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        }
        return paging;
    }
}
