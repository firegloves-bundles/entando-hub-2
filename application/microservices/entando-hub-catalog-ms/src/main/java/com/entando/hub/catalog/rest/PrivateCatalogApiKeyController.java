package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.rest.dto.apikey.AddApiKeyRequestDTO;
import com.entando.hub.catalog.rest.dto.apikey.ApiKeyResponseDTO;
import com.entando.hub.catalog.rest.dto.apikey.EditApiKeyRequestDTO;
import com.entando.hub.catalog.rest.dto.apikey.GetApiKeyResponseDTO;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@Validated
@RequestMapping("/api/private-catalog-api-key")
public class PrivateCatalogApiKeyController {

    private final Logger logger = LoggerFactory.getLogger(PrivateCatalogApiKeyController.class);

    private final PrivateCatalogApiKeyService privateCatalogApiKeyService;
    private final SecurityHelperService securityHelperService;

    public PrivateCatalogApiKeyController(PrivateCatalogApiKeyService privateCatalogApiKeyService, SecurityHelperService securityHelperService) {
        this.privateCatalogApiKeyService = privateCatalogApiKeyService;
        this.securityHelperService = securityHelperService;
    }

    @Operation(summary = "Get private catalog api keys", description = "Get the api keys of the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value = "/", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    public PagedContent<GetApiKeyResponseDTO, PrivateCatalogApiKey> getApiKeys(@RequestParam Integer page, @RequestParam Integer pageSize) {
        PagedContent<GetApiKeyResponseDTO, PrivateCatalogApiKey> apiKeys;
        String username = securityHelperService.getContextAuthenticationUsername();
        logger.debug("REST User {} request to get his api keys", username);
        apiKeys = privateCatalogApiKeyService.getApiKeysByUsername(username, page, pageSize);
        return apiKeys;
    }

    @Operation(summary = "Add new private catalog api key for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<ApiKeyResponseDTO> addApiKey(@Valid @RequestBody AddApiKeyRequestDTO request) {
        String username = securityHelperService.getContextAuthenticationUsername();
        logger.debug("REST User {} request to add a new api key", username);
        String apiKey = privateCatalogApiKeyService.addApiKey(username, request.getLabel());
        ApiKeyResponseDTO apiKeyResp = new ApiKeyResponseDTO();
        apiKeyResp.setApiKey(apiKey);
        return new ResponseEntity<>(apiKeyResp, HttpStatus.OK);
    }

    @Operation(summary = "Edit api key label for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PutMapping(value = "/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<Map<String, Boolean>> editMyLabel(@PathVariable @NotNull Long id, @Valid @RequestBody EditApiKeyRequestDTO request) {
        String username = securityHelperService.getContextAuthenticationUsername();
        logger.debug("REST User {} request to edit the api key label with id {}", username, id);
        boolean result = privateCatalogApiKeyService.editLabel(id, username, request.getLabel());
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Delete an api key for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<Map<String, Boolean>> deleteMyApiKey(@PathVariable @NotNull Long id) {
        String username = securityHelperService.getContextAuthenticationUsername();
        logger.debug("REST User {} request to delete the api key with id {}", username, id);
        boolean result = privateCatalogApiKeyService.deleteApiKey(id, username);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Regenerate an api key for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/regenerate/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<ApiKeyResponseDTO> regenerateMyApiKey(@PathVariable @NotNull long id) {
        String username = securityHelperService.getContextAuthenticationUsername();
        logger.debug("REST User {} request to regenerate the api key with id {}", username, id);
        String result = privateCatalogApiKeyService.regenerateApiKey(id, username);
        ApiKeyResponseDTO responseDTO = new ApiKeyResponseDTO();
        responseDTO.setApiKey(result);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
