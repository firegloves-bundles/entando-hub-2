package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.PortalUserRepository;
import com.entando.hub.catalog.persistence.PrivateCatalogApiKeyRepository;
import com.entando.hub.catalog.persistence.entity.PortalUser;
import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.rest.PagedContent;
import com.entando.hub.catalog.rest.exceptions.BadRequestException;
import com.entando.hub.catalog.rest.dto.apikey.GetApiKeyResponseDTO;
import com.entando.hub.catalog.service.mapper.PrivateCatalogApiKeyMapper;
import com.entando.hub.catalog.service.security.ApiKeyGeneratorHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrivateCatalogApiKeyService {

    private final Logger logger = LoggerFactory.getLogger(PrivateCatalogApiKeyService.class);
    private final PrivateCatalogApiKeyRepository privateCatalogApiKeyRepository;
    private final PortalUserRepository portalUserRepository;
    private final ApiKeyGeneratorHelper apiKeyGeneratorHelper;
    private final PrivateCatalogApiKeyMapper privateCatalogApiKeyMapper;
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
    public String addApiKey(String username, String label) {
        logger.debug("Add an api key for the username {} and label {}", username, label);
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
        this.privateCatalogApiKeyRepository.save(privateCatalogApiKey);
        return generatedApiKey;
    }

    public boolean editLabel(long id, String username, String label) {
        logger.debug("Edit the label api key for the username {} and label {}", username, label);
        Optional<PrivateCatalogApiKey> apiKeyOptional = this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(id, username);
        if (apiKeyOptional.isPresent()) {
            PrivateCatalogApiKey privateCatalogApiKey = apiKeyOptional.get();
            privateCatalogApiKey.setLabel(label);
            this.privateCatalogApiKeyRepository.save(privateCatalogApiKey);
            return true;
        } else {
            String errorMessage = "Label " + label + NOT_FOUND_MSG + username;
            throw new BadRequestException(errorMessage);
        }
    }

    public String regenerateApiKey(long id, String username) {
        logger.debug("Regenerate the api key for the username {} and id {}", username, id);
        Optional<PrivateCatalogApiKey> apiKeyOptional = this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(id, username);
        if (apiKeyOptional.isPresent()) {
            PrivateCatalogApiKey privateCatalogApiKey = apiKeyOptional.get();
            String generatedApiKey = apiKeyGeneratorHelper.generateApiKey();
            String apiKeySha = apiKeyGeneratorHelper.toSha(generatedApiKey);
            privateCatalogApiKey.setApiKey(apiKeySha);
            this.privateCatalogApiKeyRepository.save(privateCatalogApiKey);
            return generatedApiKey;
        } else {
            String errorMessage = "Api key " + id + NOT_FOUND_MSG + username;
            throw new BadRequestException(errorMessage);
        }
    }

    public boolean deleteApiKey(long id, String username) {
        logger.debug("Delete the api key for the username {} and id {}", username, id);
        Optional<PrivateCatalogApiKey> apiKeyOptional = this.privateCatalogApiKeyRepository.findByIdAndPortalUserUsername(id, username);
        if (apiKeyOptional.isPresent()) {
            privateCatalogApiKeyRepository.delete(apiKeyOptional.get());
            return true;
        } else {
            String errorMessage = "Api key " + id + NOT_FOUND_MSG + username;
            throw new BadRequestException(errorMessage);
        }
    }

    public PagedContent<GetApiKeyResponseDTO, PrivateCatalogApiKey> getApiKeysByUsername(String username, Integer pageNum, Integer pageSize) {
        logger.debug("Get api keys by username {}, pageNum {} and pageSize {}",username, pageNum, pageSize);
        Pageable paging = getPageable(pageSize, pageNum - 1);
        Page<PrivateCatalogApiKey> all = this.privateCatalogApiKeyRepository.findByPortalUserUsername(paging, username);
        List<PrivateCatalogApiKey> content = all.getContent();
        List<GetApiKeyResponseDTO> response = privateCatalogApiKeyMapper.toDto(content);
        return new PagedContent<>(response, all);
    }

    private static Pageable getPageable(Integer pageSize, int pageNum) {
        Pageable paging;
        if (pageSize == 0) {
            paging = Pageable.unpaged();
        } else {
            paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        }
        return paging;
    }
}
